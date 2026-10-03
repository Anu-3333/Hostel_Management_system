package com.example.hostal.controllers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.hostal.entities.Changes;
import com.example.hostal.entities.Checkout;
import com.example.hostal.entities.MyRoom;
import com.example.hostal.entities.User;
import com.example.hostal.repo.ChangesRepo;
import com.example.hostal.repo.CheckoutRepo;
import com.example.hostal.repo.MyRoomRepo;
import com.example.hostal.repo.UserRepo;

@Controller
public class CheckOutController {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private MyRoomRepo myRoomRepo;

    @Autowired
    private CheckoutRepo checkoutRepo;

    @Autowired
    private ChangesRepo changesRepo;

    @PostMapping("/checkoutrequest")
    public String checkOutRequest(@RequestParam("userEmail") String email,
            @RequestParam("checkoutDate") String checkoutDate,
            @RequestParam("roomNumber") String roomNumber, @RequestParam("reason") String reason,
            RedirectAttributes redirectAttributes) {
        User user = userRepo.findUserByUserEmail(email);
        if (user == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Email Not Match!");
            return "redirect:/checkout";
        }
        MyRoom myRoom = myRoomRepo.findRoomByRoomNumber(roomNumber);
        if (myRoom == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Room Number Incorrect!");
            return "redirect:/checkout";
        }

        // Check if checkout request already exists for this user
        Checkout existingCheckout = checkoutRepo.findByUser(user);
        if (existingCheckout != null) {
            // Update existing request instead of creating new one
            existingCheckout.setCheckoutRequestDate(checkoutDate);
            existingCheckout.setReason(reason);
            existingCheckout.setRoom(myRoom);
            existingCheckout.setStatus("Pending");
            checkoutRepo.save(existingCheckout);
            
            // Optionally update notification or log it
             redirectAttributes.addFlashAttribute("successMessage", "Checkout Request Updated Successfully!");
             return "redirect:/checkout";
        }

        Checkout checkout = new Checkout();
        checkout.setCheckoutRequestDate(checkoutDate);
        checkout.setReason(reason);
        checkout.setUser(user);
        checkout.setRoom(myRoom);
        checkout.setStatus("Pending");
        checkoutRepo.save(checkout);

        // update changes
        Changes changes = new Changes();
        changes.setDate(getCurrentDate());
        changes.setIsnew(true);
        changes.setNotify("Request for leaving hostel");
        changes.setType("Admin");
        changes.setUserId(user.getId());
        changesRepo.save(changes);

        redirectAttributes.addFlashAttribute("successMessage", "Your Request is sent to our staff!");
        return "redirect:/checkout";
    }

    @PostMapping("/approve_checkout")
    public String approveCheckout(@RequestParam("email") String email, @RequestParam("roomNo") String roomNo,
            RedirectAttributes redirectAttribute) {
        User user = userRepo.findUserByUserEmail(email);
        MyRoom myRoom = myRoomRepo.findMyRoomByUser(user);
        Checkout checkout = checkoutRepo.findByUser(user);

        try {
            // Check availability or other conditions if needed
            if (user.getMyRoom() != null) {
                // Use the room associated with the user if the initial repo fetch failed to find it via inverse lookup
                if (myRoom == null) {
                    myRoom = user.getMyRoom();
                }

                // Delete the checkout record first because it references MyRoom
                if (checkout != null) {
                    checkoutRepo.delete(checkout);
                }

                // Free the room
                // Ensure myRoom is definitely not null before accessing
                if (myRoom != null) {
                    myRoom.setUser(null);
                    myRoomRepo.save(myRoom);
                    myRoomRepo.deleteById(myRoom.getId());
                }
                
                // Update user to remove room assignment
                user.setMyRoom(null);
                user.setRoomNumber("N/A");
                user.setRoomType("N/A");
                userRepo.save(user);
            } else {
                // If room is ALREADY null (weird state), still update/delete checkout
                 checkout.setStatus("Approved");
                 checkoutRepo.save(checkout);
            }
            
            redirectAttribute.addFlashAttribute("successMessage", "Checkout approved successfully!");

        } catch (Exception e) {
            System.out.println("error message: " + e.getLocalizedMessage());
            e.printStackTrace();
            redirectAttribute.addFlashAttribute("errorMessage", "Something went wrong: " + e.getMessage());
        }
        return "redirect:/admin_checkout";
    }

    public String getCurrentDate() {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = currentDate.format(formatter);
        return formattedDate;
    }

}
