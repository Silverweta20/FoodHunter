package com.innovacionti.controller;

import com.innovacionti.domain.Reservation;
import com.innovacionti.domain.Restaurant;
import com.innovacionti.domain.User;
import com.innovacionti.service.NotificationService;
import com.innovacionti.service.ReservationService;
import com.innovacionti.service.RestaurantService;
import com.innovacionti.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class ProfileController {
    private final UserService userService;
    private final ReservationService reservationService;
    private final RestaurantService restaurantService;
    private final NotificationService notificationService;

    public ProfileController(UserService userService,
                             ReservationService reservationService,
                             RestaurantService restaurantService,
                             NotificationService notificationService) {
        this.userService = userService;
        this.reservationService = reservationService;
        this.restaurantService = restaurantService;
        this.notificationService = notificationService;
    }

    @GetMapping("/profile")
    public String profile(@RequestParam(value = "status", required = false) String status,
                          @RequestParam(value = "maxPrice", required = false) Integer maxPrice,
                          @RequestParam(value = "msg", required = false) String msg,
                          HttpSession session,
                          Model model) {
        User user = userService.currentUser(session).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        List<Reservation> reservations;
        Restaurant managedRestaurant = null;

        if ("RESTAURANT".equalsIgnoreCase(user.getRole())) {
            managedRestaurant = restaurantService.findByManagerEmail(user.getEmail()).orElse(null);
            reservations = managedRestaurant == null ? List.of() : reservationService.findByRestaurantId(managedRestaurant.getId());
        } else {
            reservations = reservationService.findByUserEmail(user.getEmail());
        }

        if (status != null && !status.isBlank()) {
            String lower = status.toLowerCase();
            reservations = reservations.stream()
                    .filter(r -> r.getStatus().name().toLowerCase().equals(lower))
                    .toList();
        }

        if (maxPrice != null) {
            reservations = reservations.stream()
                    .filter(r -> r.getDishPrice() <= maxPrice)
                    .toList();
        }

        model.addAttribute("user", user);
        model.addAttribute("reservations", reservations);
        model.addAttribute("notifications", notificationService.findUserNotifications(user.getEmail()));
        model.addAttribute("managedRestaurant", managedRestaurant);
        model.addAttribute("managedReservations", managedRestaurant == null ? List.of() : reservationService.findByRestaurantId(managedRestaurant.getId()));
        model.addAttribute("msg", msg);
        model.addAttribute("status", status);
        model.addAttribute("maxPrice", maxPrice);
        return "profile";
    }
}
