package com.example.hostal.controllers;

import java.time.LocalDate;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

import com.example.hostal.entities.Activite;
import com.example.hostal.entities.BankAccount;
import com.example.hostal.entities.Changes;
import com.example.hostal.entities.Complaint;
import com.example.hostal.entities.Feedback;
import com.example.hostal.entities.MyRoom;
import com.example.hostal.entities.Payment;
import com.example.hostal.entities.Review;
import com.example.hostal.entities.User;
import com.example.hostal.repo.ActiveRepo;
import com.example.hostal.repo.BankAccountRepo;
import com.example.hostal.repo.ChangesRepo;
import com.example.hostal.repo.ComplaintRepo;
import com.example.hostal.repo.FeedbackRepo;
import com.example.hostal.repo.MyRoomRepo;
import com.example.hostal.repo.PaymentRepo;
import com.example.hostal.repo.ReviewRepo;
import com.example.hostal.repo.UserRepo;
import com.example.hostal.services.MyRoomService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminUpdateController {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private MyRoomRepo myRoomRepo;

    @Autowired
    private MyRoomService myRoomService;

    @Autowired
    private ChangesRepo changesRepo;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Autowired
    private BankAccountRepo bankAccountRepo;

    @Autowired
    private ActiveRepo activeRepo;

    @Autowired
    private PaymentRepo paymentRepo;

    @Autowired
    private ComplaintRepo complaintRepo;

    @Autowired
    private ReviewRepo reviewRepo;

    @Autowired
    private FeedbackRepo feedbackRepo;

    // update admin name
    @PostMapping("/submit_username")
    public String submitUsername(@RequestParam("username") String username, @RequestParam("phone") String phone,
            Authentication authentication, Model model) {
        System.out.println("Admin update userName.......");
        User user = userRepo.findUserByUserEmail(authentication.getName());
        if (user != null) {
            user.setUserName(username);
            user.setUserPhone(phone);
            userRepo.save(user);
        }
        model.addAttribute("user", user);
        return "redirect:/dashboard";
    }

    @PostMapping("/admin/addstudent")
    public String editStudent(RedirectAttributes redirectAttribute, @RequestParam("userName") String userName,
            @RequestParam("userEmail") String userEmail, @RequestParam("userPhone") String userPhone,
            @RequestParam("gender") String gender, @RequestParam("hostelNumber") String hostelNumber) {
        // Handle the add logic
        System.out.println("Add Student in AdminUpdateController*********************");

        User user = userRepo.findUserByUserEmail(userEmail);

        if (user == null) {
            // Create new user if not exists
            user = new User();
            user.setUserEmail(userEmail);
            user.setUserPassword(passwordEncoder.encode("1234")); // Default password
            user.setRoles("ROLE_USER");
        }

        user.setUserName(userName);
        user.setGender(gender);
        user.setUserPhone(userPhone);
        user.setRoomType("Single");
        user.setHostelNumber(Integer.parseInt(hostelNumber));
        user.setRoles("ROLE_USER");

        // set roomnumber
        int roomNumber = myRoomService.lastRoomThroughHostel(Integer.parseInt(hostelNumber));
        user.setRoomNumber(roomNumber + "");
        userRepo.save(user);

        // Check if user already has a pending or approved room to avoid duplicates
        if (user.getMyRoom() == null) {
            // set booking id
            Random random = new Random();
            int x = random.nextInt(10, 100);
            int y = random.nextInt(101, 200);
            String bookingId = "BKNo" + x + "Id" + y;
            MyRoom myRoom = new MyRoom();
            myRoom.setBookingId(bookingId);
            myRoom.setRoomNumber(roomNumber + "");
            myRoom.setDate(getCurrentDate());
            myRoom.setRoomType("Single");
            myRoom.setStatus("Pending");

            myRoom.setUser(user);
            myRoomRepo.save(myRoom);

            user.setMyRoom(myRoom);
            userRepo.save(user);

            // update changes----
            Changes changes = new Changes();
            changes.setDate(getCurrentDate());
            changes.setNotify("New Student Register! " + bookingId + " " + myRoom.getStatus());
            changes.setUserId(user.getId());
            changes.setType("Admin");
            changesRepo.save(changes);

            redirectAttribute.addFlashAttribute("successMessage", "Student added successfully!");
        } else {
            redirectAttribute.addFlashAttribute("errorMessage", "User already has a room assigned!");
        }

        return "redirect:/manage_student";
    }

    @PostMapping("/admin/deletestudent/{id}")
    public String deleteStudent(@PathVariable int id, RedirectAttributes redirectAttribute) {
        // Handle the add logic
        System.out.println("Add Student in AdminUpdateController*********************");
        try {
            User user = userRepo.findById(id);
            if (user == null) {
                redirectAttribute.addFlashAttribute("errorMessage", "User not found!");
                return "redirect:/manage_student";
            }

            // 1. Delete MyRoom (Robust check using Repo)

            // 1. Delete Checkout (Must be first due to FK on MyRoom)
            com.example.hostal.entities.Checkout checkout = checkoutRepo.findByUser(user);
            if (checkout != null) {
                checkoutRepo.delete(checkout);
            }

            // 2. Delete MyRoom (Robust check using Repo)
            // First, break the link from User to MyRoom to avoid FK violation when deleting
            // MyRoom
            // Forcefully break link in DB to satisfy FK constraint via custom query
            userRepo.resetUserRoom(id);

            // Now safely delete all rooms associated with this user
            List<MyRoom> myRooms = myRoomRepo.findAllMyRoomsByUser(user);
            if (myRooms != null && !myRooms.isEmpty()) {
                for (MyRoom mr : myRooms) {
                    // Check if it still exists
                    if (myRoomRepo.existsById(mr.getId())) {
                        mr.setUser(null);
                        mr.setStatus("Available");
                        mr.setBookingId(null); // Clear booking ID (null to avoid unique constraint "duplicate N/A")
                        myRoomRepo.save(mr);
                        // Do NOT delete the room entity if it represents the physical room
                        // myRoomRepo.deleteById(mr.getId());
                    }
                }
            }

            // 3. Delete BankAccounts
            List<BankAccount> bankAccounts = bankAccountRepo.findByUser(user);
            if (bankAccounts != null && !bankAccounts.isEmpty()) {
                bankAccountRepo.deleteAll(bankAccounts);
            }

            // 4. Delete Payments
            List<Payment> payments = paymentRepo.findByUser(user);
            if (payments != null && !payments.isEmpty()) {
                paymentRepo.deleteAll(payments);
            }

            // 5. Delete Complaint
            List<Complaint> complaints = complaintRepo.findComplaintByUser(user);
            if (complaints != null && !complaints.isEmpty()) {
                complaintRepo.deleteAll(complaints);
            }

            // 6. Delete Changes
            List<Changes> changes = changesRepo.findByUserId(user.getId());
            if (changes != null && !changes.isEmpty()) {
                changesRepo.deleteAll(changes);
            }

            // 7. Delete Activity
            List<Activite> activites = activeRepo.findAllActivitesByUser(user);
            if (activites != null && !activites.isEmpty()) {
                activeRepo.deleteAll(activites);
            }

            // 8. Delete Review
            List<Review> reviews = reviewRepo.findAllReviewsByUser(user);
            if (reviews != null && !reviews.isEmpty()) {
                reviewRepo.deleteAll(reviews);
            }

            // 9. Delete Feedback
            List<Feedback> feedbacks = feedbackRepo.findAllFeedbacksByUser(user);
            if (feedbacks != null && !feedbacks.isEmpty()) {
                feedbackRepo.deleteAll(feedbacks);
            }

            userRepo.deleteById(id);
            redirectAttribute.addFlashAttribute("successMessage", "User Delete Successful!");
        } catch (Exception e) {
            System.out.println("error message: " + e.getLocalizedMessage());
            e.printStackTrace();
            redirectAttribute.addFlashAttribute("errorMessage", "Something went wrong: " + e.getMessage());
        }
        return "redirect:/manage_student";
    }

    @Autowired
    private com.example.hostal.repo.CheckoutRepo checkoutRepo;

    // ... (existing methods)

    @PostMapping("/admin/deletebooking/{id}")
    public String deleteBooking(@PathVariable int id, RedirectAttributes redirectAttribute) {
        // Handle the deletion logic
        System.out.println("Delete Booking in AdminUpdateController*********************");
        try {
            MyRoom myRoom = myRoomRepo.findById(id);
            if (myRoom != null) {
                User user = myRoom.getUser();

                if (user == null) {
                    user = userRepo.findByMyRoomId(id);
                }

                if (user != null) {
                    // Check for and delete associated checkout request to prevent FK violation
                    com.example.hostal.entities.Checkout checkout = checkoutRepo.findByUser(user);
                    if (checkout != null && checkout.getRoom().getId() == myRoom.getId()) {
                        checkoutRepo.delete(checkout);
                    }

                    // Forcefully break link in DB to satisfy FK constraint via custom query
                    userRepo.resetUserRoom(user.getId());
                }

                // Update room to Available instead of deleting
                myRoom.setUser(null);
                myRoom.setStatus("Available");
                myRoom.setBookingId(null); // Clear booking info
                myRoomRepo.save(myRoom);
                // myRoomRepo.deleteById(id);
                redirectAttribute.addFlashAttribute("successMessage", "Booking deleted successfully!");
            } else {
                redirectAttribute.addFlashAttribute("errorMessage", "Booking not found!");
            }
        } catch (Exception e) {
            System.out.println("Error deleting booking: " + e.getMessage());
            redirectAttribute.addFlashAttribute("errorMessage", "Error deleting booking: " + e.getMessage());
        }
        return "redirect:/manage_bookings";
    }

    public String getCurrentDate() {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = currentDate.format(formatter);
        return formattedDate;
    }

}
