package com.example.hostal.controllers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.hostal.entities.MyRoom;
import com.example.hostal.entities.User;
import com.example.hostal.repo.MyRoomRepo;
import com.example.hostal.repo.UserRepo;
import com.example.hostal.services.MyRoomService;



@Controller
public class MyRoomController {

    @Autowired
    private MyRoomService myRoomService;

    @Autowired
    private MyRoomRepo myRoomRepo;

    @Autowired
    private UserRepo userRepo;

    @GetMapping("/myroom")
    public String myRoom(Model model, Authentication authentication) {
        // Fetch the user's room data (assuming there's a method to get user's room
        // info)
        String email = authentication.getName();
        User user = userRepo.findUserByUserEmail(email);
        MyRoom room = myRoomRepo.findMyRoomByUser(user); // Replace 1L with actual user-specific room ID

        System.out.println("\n room: " + room);
        System.out.println("myroom controller*******************************************");

        // delete all un allocated room
        if (room != null && room.getStatus().equals("This room is not available")) {
            myRoomService.deleteRoom(room.getId());
            model.addAttribute("error", "room_not_found");
            model.addAttribute("room", null);
            user.setRoomNumber("N/A");
            user.setRoomType("N/A");
            userRepo.save(user);
            return "myroom";
        }
        System.out.println("enter in this state********");
        model.addAttribute("room", room);
        model.addAttribute("gender", user.getGender());
        return "myroom";
    }

    @PostMapping("/apply-room")
    public String applyRoom(@RequestParam("hostelNumber") String hostelNumber,
            @RequestParam("gender")String gender,
            Authentication authentication, RedirectAttributes redirectAttributes) {
        User user = userRepo.findUserByUserEmail(authentication.getName());
       
        MyRoom room = myRoomRepo.findMyRoomByUser(user);

        
        if (room == null) {
            room = new MyRoom();
            room.setUser(user);
        }else{
            System.out.println("Room not null**************************");
            redirectAttributes.addFlashAttribute("warning", "Warning! Your can't apply for more then one room.");
            return "redirect:/myroom";
        }
        //set last hostel
        char c = hostelNumber.charAt(hostelNumber.length() - 1);
        int hostelNo = c - '0';
        int roomNumber = myRoomService.lastRoomThroughHostel(hostelNo);
        System.out.println("hostel Number: "+hostelNo);
        System.out.println("last room no + 1: "+roomNumber);
        //set bookingId
        Random random = new Random();
        int x = random.nextInt(10, 100);
        int y = random.nextInt(101, 200);
        String bookingId = "BKNo" + x + "Id" + y;
        
        room.setRoomNumber(roomNumber+"");
        room.setDate(currDate());
        room.setBookingId(bookingId);
        room.setRoomType("Single");
        room.setStatus("Pending");
        user.setGender(gender);
        room.setUser(user);
        myRoomRepo.save(room);

        // update rom number in user
        user.setRoomNumber(roomNumber+"");
        user.setMyRoom(room);
        userRepo.save(user);
        redirectAttributes.addFlashAttribute("room", room);
        return "redirect:/myroom";
    }


    @Autowired
    private com.example.hostal.repo.CheckoutRepo checkoutRepo;

    //remove my booking
    @PostMapping("/removemybooking")
    public String removeMyBooking(Authentication authentication, RedirectAttributes redirectAttribute){
        System.out.println("Remove my booking in My room........................");
        System.out.println("email is: "+authentication.getName());
        User user = userRepo.findUserByUserEmail(authentication.getName());
        MyRoom myRoom = myRoomRepo.findMyRoomByUser(user);

        if (myRoom == null) {
            myRoom = user.getMyRoom();
        }

        if (myRoom == null) {
             redirectAttribute.addFlashAttribute("errorMessage", "No booking found to remove!");
             return "redirect:/myroom";
        }

        if(myRoom.getStatus().equalsIgnoreCase("occupied")){
           redirectAttribute.addFlashAttribute("errorMessage", "You can cancel the booking only while the status is pending!");
        
        }else{
            try{
                // Delete associated checkout request if exists (to prevent FK violation)
                com.example.hostal.entities.Checkout checkout = checkoutRepo.findByUser(user);
                if (checkout != null && checkout.getRoom().getId() == myRoom.getId()) {
                    checkoutRepo.delete(checkout);
                }

                //set room number deleted
                user.setRoomNumber("---");
                user.setMyRoom(null);
                user.setRoomType("N/A"); // Reset room type as well
                userRepo.save(user);
                // delete room
                myRoomRepo.deleteById(myRoom.getId());
                redirectAttribute.addFlashAttribute("successMessage", "Booking Delete Successful!");
            }catch(Exception e){
                System.out.println("Error removing booking: " + e.getMessage());
                redirectAttribute.addFlashAttribute("errorMessage","Something went wrong: " + e.getMessage());
            }
        }
        
        return "redirect:/myroom";
    }

    public String currDate() {
        LocalDate currentDate = LocalDate.now();

        // Define the desired date format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Format the current date
        String formattedDate = currentDate.format(formatter);
        return formattedDate;

    }

}
