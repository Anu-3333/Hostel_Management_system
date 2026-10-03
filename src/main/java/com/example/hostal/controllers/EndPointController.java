package com.example.hostal.controllers;

import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.hostal.entities.BankAccount;
import com.example.hostal.entities.Changes;
import com.example.hostal.entities.Checkout;
import com.example.hostal.entities.Complaint;
import com.example.hostal.entities.MyRoom;
import com.example.hostal.entities.Payment;
import com.example.hostal.entities.User;
import com.example.hostal.repo.MyRoomRepo;
import com.example.hostal.repo.PaymentRepo;
import com.example.hostal.repo.UserRepo;
import com.example.hostal.services.BankAccountService;
import com.example.hostal.repo.AdminRepo;
import com.example.hostal.entities.Admin;
import com.example.hostal.services.ChangesServices;
import com.example.hostal.services.CheckoutService;
import com.example.hostal.services.ComplaintService;
import com.example.hostal.services.MyRoomService;
import com.example.hostal.services.UserService;

@Controller
public class EndPointController {

    @Autowired
    private ChangesServices changesServices;

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private PaymentRepo paymentRepo;

    @Autowired
    private MyRoomRepo myRoomRepo;

    @Autowired
    private MyRoomService myRoomService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private CheckoutService checkoutService;

    @Autowired
    private ComplaintService complaintService;

    @Autowired
    private BankAccountService bankAccountService;

    @Autowired
    private com.example.hostal.repo.ReviewRepo reviewRepo;

    @Autowired
    private AdminRepo adminRepo;

    @GetMapping("/notification")
    public String showNotificationPage(Model model, Authentication authentication) {
        System.out.println("show notification page enter................");
        String email = authentication.getName();

        User user = userRepository.findUserByUserEmail(email);
        // MyRoom myRoom = myRoomRepo.findMyRoomByUser(user);
        if (user != null) {
            System.out.println("user email is: " + email);
            System.out.println("user role is: " + user.getRoles());

            // set for user
            if (user.getRoles().equalsIgnoreCase("ROLE_USER")) {
                List<Changes> notifications = changesServices.getLastFiveUserChanges(user.getId());
                model.addAttribute("notifications", notifications);
            }
            // set for admin
            else if (user.getRoles().equalsIgnoreCase("ROLE_ADMIN")) {
                List<Changes> notifications = changesServices.getAllAdminChanges();
                model.addAttribute("notifications", notifications);
            }
        } else {
            Admin admin = adminRepo.findByEmail(email);
            if (admin != null) {
                System.out.println("Found Admin for notifications: " + email);
                List<Changes> notifications = changesServices.getAllAdminChanges();
                model.addAttribute("notifications", notifications);
            }
        }

        return "notification";
    }

    @GetMapping("/index")
    public String login() {
        return "index";
    }

    @GetMapping("/gallery")
    public String gallery() {
        return "gallery";
    }

    @GetMapping("/dashboard_admin")
    public String showDashboardAdmin(Authentication authentication, Model model) {
        System.out.println("Enter dashboard admin*********************");
        if (authentication != null) {
            String email = authentication.getName();
            User user = userRepository.findUserByUserEmail(email);
            model.addAttribute("user", user);

            // Add other necessary attributes to prevent null pointer errors in template
            // Ideally these should come from services, using 0/empty as safe defaults for
            // now
            // or fetching actual data where services are available

            // Checkout Requests
            List<Checkout> checkouts = checkoutService.getpendingCheckoutList();
            model.addAttribute("checkoutRequestsCount", checkouts != null ? checkouts.size() : 0);
            model.addAttribute("newCheckoutRequests", checkouts != null && !checkouts.isEmpty());

            // Pending Room Requests
            List<MyRoom> pendingRooms = myRoomRepo.findByStatus("Pending");
            model.addAttribute("pendingRequests", pendingRooms != null ? pendingRooms.size() : 0);

            // Complaints
            List<Complaint> complaints = complaintService.getPendingComplaint();
            model.addAttribute("newComplaint", complaints != null && !complaints.isEmpty() ? true : null);

            // Recent Activities (Placeholder or fetch if service exists)
            model.addAttribute("recentActivities", Collections.emptyList());

            // Ratings
            List<com.example.hostal.entities.Review> allReviews = reviewRepo.findAll();
            if (allReviews != null && !allReviews.isEmpty()) {
                double average = allReviews.stream().mapToInt(com.example.hostal.entities.Review::getRating)
                        .average().orElse(0.0);
                model.addAttribute("avgRating", String.format("%.1f", average));

                com.example.hostal.entities.Review lastReview = allReviews.get(allReviews.size() - 1);
                model.addAttribute("lastMessage", lastReview.getMessage());
                model.addAttribute("date", lastReview.getDate());
            } else {
                model.addAttribute("avgRating", "0.0");
                model.addAttribute("lastMessage", "No recent reviews");
                model.addAttribute("date", "");
            }

            // Updates/Notifications
            model.addAttribute("newUpdate", null);

        }
        return "dashboard_admin";
    }

    @GetMapping("/bank_account")
    public String showBankAccount(Model model, Authentication authentication) {
        String email = authentication.getName();
        User user = userRepo.findUserByUserEmail(email);
        model.addAttribute("bankAccount", new BankAccount());
        List<BankAccount> userBankAccounts = bankAccountService.getBankAccountsByUser(user);
        model.addAttribute("bankAccounts", userBankAccounts);
        System.out.println("DEBUG: showBankAccount - User: " + email + ", Found Accounts: " + userBankAccounts.size());
        return "bank_account";
    }

    @GetMapping("/settings")
    public String showSettings(Authentication authentication, Model model) {
        String email = authentication.getName();
        User user = userRepository.findUserByUserEmail(email);
        model.addAttribute("user", user);
        System.out.println("Show setting run...................");
        return "settings";
    }

    @GetMapping("/login")
    public String showLoginPage2() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterPage() {
        System.out.println("Enter in register*****************");
        return "register";
    }

    @GetMapping("/admin-register")
    public String showAdminRegisterPage() {
        return "admin_register";
    }

    @GetMapping("/payment_report")
    public String showPaymentReport(Authentication authentication, Model model) {

        String email = authentication.getName();
        User user = userRepository.findUserByUserEmail(email);
        List<Payment> payment = paymentRepo.findByUser(user);
        System.out.println("DEBUG: Payment Report - User: " + email);
        if (payment != null) {
            System.out.println("DEBUG: Payment Report - Count: " + payment.size());
            payment.forEach(
                    p -> System.out.println("DEBUG: Payment ID: " + p.getPaymentId() + ", Amount: " + p.getAmount()));
        } else {
            System.out.println("DEBUG: Payment Report - Payment list is NULL");
        }
        MyRoom myRoom = myRoomRepo.findMyRoomByUser(user);
        model.addAttribute("payments", payment);
        model.addAttribute("user", user);
        model.addAttribute("myroom", myRoom);
        return "payment_report";

    }

    @GetMapping("/room_report")
    public String showRoomReport(Authentication authentication, Model model) {
        String email = authentication.getName();
        User user = userRepository.findUserByUserEmail(email);
        MyRoom myRoom = myRoomRepo.findMyRoomByUser(user);
        model.addAttribute("user", user);
        model.addAttribute("myroom", myRoom);
        return "room_report";
    }

    @GetMapping("/requestformadmin")
    public String showRequestForm(Authentication authentication, Model model) {
        System.out.println("Enter in request for admin******************");
        List<User> userList = myRoomService.getPendingRequestUser();
        List<MyRoom> myRoomsList = myRoomRepo.findByStatus("Pending");
        model.addAttribute("myRoomList", myRoomsList);
        model.addAttribute("userList", userList);
        int max = Math.max(userList.size(), myRoomsList.size());
        model.addAttribute("max", max);

        return "requestformadmin";
    }

    @GetMapping("/manage_student")
    public String showMangeStudent(Model model, @RequestParam(defaultValue = "0") int page) {
        List<User> users = userRepository.findAll();
        List<User> students = userService.getOnlyNormalUser(users);
        model.addAttribute("students", students);
        System.out.println("Enter in manage student*********************");
        return "manage_student";
    }

    @GetMapping("/manage_rooms")
    public String showMangeRooms(Model model) {
        // Total capacity changed to 50
        long totalCapacity = 50;
        long totalOccupied = myRoomRepo.countByStatus("Occupied");
        long totalAvailable = totalCapacity - totalOccupied;

        // Fetch all rooms from DB (contains Occupied, Pending, Maintenance)
        List<MyRoom> dbRooms = myRoomRepo.findAll();

        // Create a definitive list of rooms 1-50
        List<java.util.Map<String, Object>> allRooms = new java.util.ArrayList<>();

        for (int i = 1; i <= 50; i++) {
            String roomNumStr = String.valueOf(i);
            java.util.Map<String, Object> roomData = new java.util.HashMap<>();
            roomData.put("roomNumber", roomNumStr);

            // Check if this room exists in DB
            java.util.Optional<MyRoom> dbRoomOpt = dbRooms.stream()
                    .filter(r -> r.getRoomNumber().equals(roomNumStr))
                    .findFirst();

            if (dbRoomOpt.isPresent()) {
                MyRoom r = dbRoomOpt.get();
                roomData.put("id", r.getId());
                roomData.put("status", r.getStatus()); // Occupied, Pending, Maintenance
                roomData.put("roomType", r.getRoomType());
                roomData.put("user", r.getUser());
            } else {
                roomData.put("id", null);
                roomData.put("status", "Available");
                roomData.put("roomType", "Single"); // Default
                roomData.put("user", null);
            }

            // Set Hostel Name
            if (i <= 25) {
                roomData.put("hostelName", "Hostel 1");
            } else {
                roomData.put("hostelName", "Hostel 2");
            }

            allRooms.add(roomData);
        }

        model.addAttribute("roomList", allRooms);

        model.addAttribute("totalAvailable", totalAvailable);
        model.addAttribute("totalOccupied", totalOccupied);
        return "manage_rooms";
    }

    @GetMapping("/admin/room/delete/{id}")
    public String deleteRoom(@org.springframework.web.bind.annotation.PathVariable("id") int id,
            RedirectAttributes redirectAttributes) {
        try {
            MyRoom room = myRoomRepo.findById(id);
            if (room != null) {
                // Also update the user to remove room assignment
                User user = room.getUser();
                if (user != null) {
                    user.setRoomNumber("N/A");
                    user.setRoomType("N/A");
                    user.setMyRoom(null);
                    userRepo.save(user);
                }
                myRoomRepo.deleteById(id);
                redirectAttributes.addFlashAttribute("successMessage", "Room deleted successfully!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Room not found!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting room: " + e.getMessage());
        }
        return "redirect:/manage_rooms";
    }

    @GetMapping("/admin/room/edit/number/{roomNumber}")
    public String editRoomByNumber(
            @org.springframework.web.bind.annotation.PathVariable("roomNumber") String roomNumber, Model model) {
        java.util.Optional<MyRoom> roomOpt = java.util.Optional.ofNullable(myRoomRepo.findRoomByRoomNumber(roomNumber));
        if (roomOpt.isPresent()) {
            model.addAttribute("room", roomOpt.get());
        } else {
            MyRoom newRoom = new MyRoom();
            newRoom.setRoomNumber(roomNumber);
            newRoom.setRoomType("Single");
            newRoom.setStatus("Available");
            model.addAttribute("room", newRoom);
        }
        return "edit_room";
    }

    @org.springframework.web.bind.annotation.PostMapping("/admin/room/update")
    public String updateRoom(@org.springframework.web.bind.annotation.ModelAttribute MyRoom room,
            RedirectAttributes redirectAttributes) {
        try {
            // Check if room exists by ID (update) or needs creation
            MyRoom existingRoom = null;
            if (room.getId() > 0) {
                existingRoom = myRoomRepo.findById(room.getId());
            } else {
                // Try finding by number in case ID was missed but room exists
                existingRoom = myRoomRepo.findRoomByRoomNumber(room.getRoomNumber());
            }

            if (existingRoom != null) {
                existingRoom.setRoomType(room.getRoomType());
                existingRoom.setStatus(room.getStatus());
                myRoomRepo.save(existingRoom);
                redirectAttributes.addFlashAttribute("successMessage", "Room updated successfully!");
            } else {
                // Create new room
                myRoomRepo.save(room);
                redirectAttributes.addFlashAttribute("successMessage", "Room created successfully!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating room: " + e.getMessage());
        }
        return "redirect:/manage_rooms";
    }

    @org.springframework.web.bind.annotation.PostMapping("/admin/booking/delete/number/{roomNumber}")
    public String deleteBookingByNumber(
            @org.springframework.web.bind.annotation.PathVariable("roomNumber") String roomNumber,
            RedirectAttributes redirectAttributes) {
        try {
            MyRoom room = myRoomRepo.findRoomByRoomNumber(roomNumber);
            if (room != null) {
                // Also update the user to remove room assignment
                User user = room.getUser();
                if (user != null) {
                    user.setRoomNumber("N/A");
                    user.setRoomType("N/A");
                    user.setMyRoom(null);
                    userRepo.save(user);
                }
                myRoomRepo.delete(room);
                redirectAttributes.addFlashAttribute("successMessage", "Booking deleted (Room reset) successfully!");
            } else {
                redirectAttributes.addFlashAttribute("successMessage", "Room is already empty.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting booking: " + e.getMessage());
        }
        return "redirect:/manage_bookings";
    }

    @GetMapping("/search-room")
    @ResponseBody
    public org.springframework.http.ResponseEntity<?> searchRoom(@RequestParam("query") String roomNo) {
        System.out.println("DEBUG: Searching for Room: " + roomNo);
        int roomNumber;
        try {
            roomNumber = Integer.parseInt(roomNo);
        } catch (NumberFormatException e) {
            return org.springframework.http.ResponseEntity.badRequest().body("Invalid room number format");
        }

        if (roomNumber < 1 || roomNumber > 50) {
            return org.springframework.http.ResponseEntity.badRequest().body("Room number must be between 1 and 50");
        }

        MyRoom myRoom = myRoomRepo.findRoomByRoomNumber(roomNo);
        java.util.Map<String, Object> response = new java.util.HashMap<>();

        if (myRoom != null) {
            response.put("id", myRoom.getId());
            response.put("roomNumber", myRoom.getRoomNumber());
            response.put("roomType", myRoom.getRoomType());
            response.put("status", myRoom.getStatus());
            if (myRoom.getUser() != null) {
                response.put("userName", myRoom.getUser().getUserName());
                response.put("userId", myRoom.getUser().getId());
            } else {
                response.put("userName", "N/A");
            }
            return org.springframework.http.ResponseEntity.ok(response);
        } else {
            // Room is not in DB, meaning it is Available (Vacant)
            response.put("roomNumber", roomNo);
            response.put("roomType", "N/A"); // Or unknown
            response.put("status", "Available");
            response.put("userName", "N/A");
            return org.springframework.http.ResponseEntity.ok(response);
        }
    }

    @GetMapping("/manage_bookings")
    public String showMangeBookings(Model model) {
        // Fetch all rooms from DB
        List<MyRoom> dbRooms = myRoomRepo.findAll();

        // Create a definitive list of rooms 1-50
        List<java.util.Map<String, Object>> allBookings = new java.util.ArrayList<>();

        for (int i = 1; i <= 50; i++) {
            String roomNumStr = String.valueOf(i);
            java.util.Map<String, Object> roomData = new java.util.HashMap<>();
            roomData.put("roomNumber", roomNumStr);

            // Check if this room exists in DB
            java.util.Optional<MyRoom> dbRoomOpt = dbRooms.stream()
                    .filter(r -> r.getRoomNumber().equals(roomNumStr))
                    .findFirst();

            if (dbRoomOpt.isPresent()) {
                MyRoom r = dbRoomOpt.get();
                roomData.put("id", r.getId());
                roomData.put("status", r.getStatus());
                roomData.put("roomType", r.getRoomType());
                roomData.put("date", r.getDate());
                if (r.getUser() != null) {
                    roomData.put("userName", r.getUser().getUserName());
                    roomData.put("userEmail", r.getUser().getUserEmail());
                } else {
                    roomData.put("userName", "N/A");
                    roomData.put("userEmail", "N/A");
                }
            } else {
                roomData.put("id", null);
                roomData.put("status", "Available");
                roomData.put("roomType", "Single");
                roomData.put("date", "N/A");
                roomData.put("userName", "N/A");
                roomData.put("userEmail", "N/A");
            }

            // Set Hostel Name
            if (i <= 25) {
                roomData.put("hostelName", "Hostel 1");
            } else {
                roomData.put("hostelName", "Hostel 2");
            }

            allBookings.add(roomData);
        }

        model.addAttribute("myRoom", allBookings);
        System.out.println("DEBUG: manage_bookings populated with 50 rooms");
        return "manage_bookings";
    }

    @GetMapping("/admin_setting")
    public String showAdminSetting() {
        System.out.println("Enter in admin setting*******************");
        return "admin_setting";
    }

    @GetMapping("/manage_hostel")
    public String showManageHostel() {
        System.out.println("Enter manage hostl.................");
        return "manage_hostel";
    }

    @GetMapping("/complaint")
    public String showComplaintPage(Authentication authentication, Model model) {

        String email = authentication.getName();
        System.out.println("email is: " + email);
        User user = userRepository.findUserByUserEmail(email);
        model.addAttribute("user", user);
        MyRoom myRoom = myRoomRepo.findMyRoomByUser(user);
        model.addAttribute("room", myRoom);
        System.out.println("Enter in complaint page...........");
        return "complaint";
    }

    @GetMapping("/privacy")
    public String showPrivacyPage() {
        return "privacy";
    }

    @GetMapping("/terms_of_services")
    public String sowTemrsOfSerivices() {
        return "terms_of_services";
    }

    @GetMapping("/about")
    public String showAbout() {
        return "about";
    }

    @GetMapping("/student_biodata")
    public String showStudentBiodataPage(Model model, @RequestParam("bookingId") String bookingId) {
        // You can now use the bookingId to retrieve student biodata or perform other
        // actions
        model.addAttribute("bookingId", bookingId);

        return "student_biodata"; // Return the page view
    }

    @GetMapping("/admin_complaint")
    public String showAdminComplaint(Model model) {
        System.out.println("Enter in admin complaint..............");
        List<Complaint> list = complaintService.getPendingComplaint();
        Collections.reverse(list);
        model.addAttribute("complaints", list);
        return "admin_complaint";
    }

    @GetMapping("/review")
    public String showReviewPage() {
        System.out.println("Review Page Show**********************");
        return "review";
    }

    @GetMapping("/checkout")
    public String showCheckOutRequestPage() {
        System.out.println("Enter in check out request page............");
        return "checkout";
    }

    @GetMapping("/admin_checkout")
    public String showAdminCheckout(Model model) {
        System.out.println("Enter in Admin checkout.............");
        List<Checkout> list = checkoutService.getpendingCheckoutList();
        model.addAttribute("checkoutRequests", list);
        return "admin_checkout";
    }
}
