package com.example.hostal.controllers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.hostal.entities.Changes;
import com.example.hostal.entities.Complaint;
import com.example.hostal.entities.User;
import com.example.hostal.repo.ChangesRepo;
import com.example.hostal.repo.ComplaintRepo;
import com.example.hostal.repo.UserRepo;

@Controller
public class ComplaintController {

    @Autowired
    private ComplaintRepo complaintRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ChangesRepo changesRepo;

    @PostMapping("/submit_complaint")
    public String submitComplaint(
            @RequestParam("cmpType") String cmpType,
            @RequestParam("description") String description,
            @RequestParam("email")String email,
            Model model, Authentication authentication, RedirectAttributes redirectAttributes) {
                try {
                    User user = userRepo.findUserByUserEmail(email);
                    if (user == null) {
                        redirectAttributes.addFlashAttribute("message", "Error: User not found!");
                        return "redirect:/complaint";
                    }

                    System.out.println("Complaint controller run.......................");
                    Complaint complaint = new Complaint();
                    complaint.setCmpMess(description);
                    complaint.setCmpType(cmpType);
                    complaint.setDate(currDate());
                    complaint.setStatus("Pending");
                    complaint.setUser(user);
                    complaintRepo.save(complaint);
    
                    //update changes
                    Changes changes = new Changes();
                    changes.setDate(currDate());
                    changes.setIsnew(true);
                    changes.setType("ADMIN");
                    String hostelInfo = String.valueOf(user.getHostelNumber());
                    changes.setNotify("New Complaint!, Hostel: " + hostelInfo);
                    changes.setUserId(user.getId());
                    changesRepo.save(changes);
    
                    // Add success message to the model
                    System.out.println("Complaint success........");
                    redirectAttributes.addFlashAttribute("message", "Complaint submitted successfully!");

                } catch (Exception e) {
                    System.out.println("Error submitting complaint: " + e.getMessage());
                    redirectAttributes.addFlashAttribute("message", "Error submitting complaint: " + e.getMessage());
                }
        return "redirect:/complaint";
    }

    public String currDate(){
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedDate = currentDate.format(formatter);
        return formattedDate;
    }
}

