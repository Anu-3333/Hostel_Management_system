package com.example.hostal.controllers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.hostal.entities.Complaint;
import com.example.hostal.entities.MyRoom;
import com.example.hostal.entities.Review;
import com.example.hostal.entities.User;
import com.example.hostal.repo.ComplaintRepo;
import com.example.hostal.repo.MyRoomRepo;
import com.example.hostal.repo.ReviewRepo;
import com.example.hostal.repo.UserRepo;
import com.example.hostal.services.ChangesServices;
import com.example.hostal.services.ComplaintService;
import com.example.hostal.services.ReviewService;

@Controller
public class DashboardController {

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private com.example.hostal.repo.AdminRepo adminRepo;

    @Autowired
    private MyRoomRepo myRoomRepo;

    @Autowired
    private ChangesServices changesServices;

    @Autowired
    private ComplaintRepo complaintRepo;

    @Autowired
    private com.example.hostal.repo.PaymentRepo paymentRepo;

    @Autowired
    private ComplaintService complaintService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ReviewRepo reviewRepo;

    @Autowired
    private com.example.hostal.services.CheckoutService checkoutService;

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        System.out.println("Enter in dashbaord controller...............");
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            System.out.println("DEBUG: Dashboard for email: " + email);

            User user = userRepository.findUserByUserEmail(email);
            System.out.println("DEBUG: Found User: " + (user != null));

            // If user is null, checking for Admin
            if (user == null) {
                com.example.hostal.entities.Admin admin = adminRepo.findByEmail(authentication.getName());
                if (admin != null) {
                    // Create a dummy user object for the view to use (since view expects 'user')
                    user = new User();
                    user.setId(admin.getId());
                    user.setUserName(admin.getName());
                    user.setUserEmail(admin.getEmail());
                    user.setRoles(admin.getRole());
                    // Set dummy dates to prevent null pointer in view if it uses them
                    LocalDate currentDate = LocalDate.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    user.setActivityDate1(currentDate.minusDays(5).format(formatter));
                    user.setActivityDate2(currentDate.minusDays(2).format(formatter));
                    user.setActivityDate3(currentDate.format(formatter));
                } else {
                    System.out.println("DEBUG: Admin also null! Creating fallback user.");
                    user = new User();
                    user.setUserName("Admin");
                    user.setUserEmail(email);
                    user.setRoles("ROLE_ADMIN");
                    LocalDate currentDate = LocalDate.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    user.setActivityDate1(currentDate.minusDays(5).format(formatter));
                    user.setActivityDate2(currentDate.minusDays(2).format(formatter));
                    user.setActivityDate3(currentDate.format(formatter));
                }
            } else {
                // Logic for normal user
                LocalDate currentDate = LocalDate.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String formattedDate = currentDate.format(formatter);

                user.setActivityDate1(currentDate.minusDays(5).format(formatter));
                user.setActivityDate2(currentDate.minusDays(2).format(formatter));
                user.setActivityDate3(formattedDate);

                LocalDate upcomingBookingDate = currentDate.plusDays(10);
                user.setUpcomingBookingId("BKG12345");
                user.setUpcomingBookingDate(upcomingBookingDate.format(formatter));
            }

            if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
                model.addAttribute("user", user);
                // List<User> totalUsersList = userRepository.findUserByRoles("ROLE_USER");
                List<MyRoom> totalStudents = myRoomRepo.findByStatus("Approved");
                model.addAttribute("totalStudents", totalStudents.size());
                List<MyRoom> pendingrequestList = myRoomRepo.findByStatus("Pending");
                model.addAttribute("pendingRequests", pendingrequestList.size());

                System.out.println("Enter in dashbord controller..............");
                if (changesServices.isNewUpdateForAdmin()) {
                    model.addAttribute("newUpdate", "New Update!");
                }

                if (complaintService.isNewComplaint()) {
                    List<Complaint> complaints = complaintRepo.findAll();
                    model.addAttribute("newComplaint", complaints);
                }

                // Fetch pending checkout requests
                List<com.example.hostal.entities.Checkout> checkoutList = checkoutService.getpendingCheckoutList();
                int checkoutCount = (checkoutList != null) ? checkoutList.size() : 0;
                model.addAttribute("checkoutRequestsCount", checkoutCount);

                if (checkoutCount > 0) {
                    model.addAttribute("newCheckoutRequests", true);
                }

                System.out.println("DEBUG DASHBOARD: Pending Requests: " + pendingrequestList.size());
                System.out.println("DEBUG DASHBOARD: Checkout Requests Count: " + checkoutCount);

                // System.out.println("user name is: "+user.getUserName());
                System.out.println("User info \n" + user.toString());

                // review info send using model
                String lastMessage = reviewService.getLastReview();
                float avgRating = reviewService.getAvgRating();
                model.addAttribute("lastMessage", lastMessage);
                model.addAttribute("avgRating", avgRating);
                model.addAttribute("date", reviewService.getLastDate());

                // Add Checkout Requests to Recent Activity
                // This is a simplified approach. Ideally, you'd have a unified 'Activity'
                // class.
                // For now, we'll format the checkout requests into a simple list of maps or
                // objects
                // that the dashboard can iterate over.
                // Assuming 'checkoutList' from above
                List<java.util.Map<String, String>> recentActivities = new java.util.ArrayList<>();

                if (checkoutList != null) {
                    for (com.example.hostal.entities.Checkout checkout : checkoutList) {
                        java.util.Map<String, String> activity = new java.util.HashMap<>();
                        String userName = (checkout.getUser() != null) ? checkout.getUser().getUserName()
                                : "Unknown User";
                        activity.put("description", "Checkout Request from " + userName);
                        activity.put("date",
                                checkout.getCheckoutRequestDate() != null ? checkout.getCheckoutRequestDate().toString()
                                        : "N/A");
                        recentActivities.add(activity);
                    }
                }

                model.addAttribute("recentActivities", recentActivities);

                // Payment List for Admin Dashboard
                List<com.example.hostal.entities.Payment> allPayments = paymentRepo.findAll();
                List<java.util.Map<String, Object>> paymentHistory = new java.util.ArrayList<>();

                if (allPayments != null) {
                    for (com.example.hostal.entities.Payment payment : allPayments) {
                        java.util.Map<String, Object> paymentData = new java.util.HashMap<>();
                        User payUser = payment.getUser();
                        paymentData.put("name", payUser != null ? payUser.getUserName() : "Unknown");

                        MyRoom payRoom = (payUser != null) ? myRoomRepo.findMyRoomByUser(payUser) : null;
                        paymentData.put("roomNo",
                                (payRoom != null && payRoom.getRoomNumber() != null) ? payRoom.getRoomNumber() : "N/A");

                        paymentData.put("paymentId", payment.getPaymentId());
                        paymentData.put("amount", payment.getAmount());
                        paymentData.put("status", payment.getStatus());
                        paymentData.put("date", payment.getPaymentDate());

                        paymentHistory.add(paymentData);
                    }
                }
                // Sort by date or ID if needed, for now just latest first effectively if DB
                // returns so, or reverse
                java.util.Collections.reverse(paymentHistory);
                model.addAttribute("paymentHistory", paymentHistory);

                return "dashboard_admin"; // Admin dashboard
            } else {
                // update change notifi show
                if (changesServices.isNewUpdateForUser(user.getId())) {
                    model.addAttribute("newUpdate", "New Update!");
                }

                MyRoom myRoom = myRoomRepo.findMyRoomByUser(user);
                if (myRoom != null) {
                    user.setRoomType(myRoom.getRoomType());
                    user.setRoomNumber(myRoom.getRoomNumber());
                    model.addAttribute("bookingId", myRoom.getBookingId());
                    model.addAttribute("myRoom", myRoom);
                } else {
                    model.addAttribute("bookingId", "NULL");
                }
                model.addAttribute("user", user);
                return "dashboard"; // Normal user dashboard
            }
        }

        System.out.println("Error::::::::::::::");
        return "redirect:/index";
    }

}
