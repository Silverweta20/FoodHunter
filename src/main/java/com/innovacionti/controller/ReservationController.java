package com.innovacionti.controller;

import com.innovacionti.domain.MenuItem;
import com.innovacionti.domain.Reservation;
import com.innovacionti.domain.Restaurant;
import com.innovacionti.domain.User;
import com.innovacionti.service.ReservationService;
import com.innovacionti.service.RestaurantService;
import com.innovacionti.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

@Controller
public class ReservationController {
    private final ReservationService reservationService;
    private final RestaurantService restaurantService;
    private final UserService userService;

    public ReservationController(ReservationService reservationService, RestaurantService restaurantService, UserService userService) {
        this.reservationService = reservationService;
        this.restaurantService = restaurantService;
        this.userService = userService;
    }

    @GetMapping("/restaurants/{restaurantId}/reserve")
    public String reserveDishPage(@PathVariable long restaurantId, HttpSession session, Model model) {
        User user = userService.currentUser(session).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }
        Restaurant restaurant = restaurantService.requireById(restaurantId);
        model.addAttribute("restaurant", restaurant);
        model.addAttribute("menuItems", restaurant.getMenuItems());
        model.addAttribute("currentUser", user);
        model.addAttribute("pickupAt", LocalDateTime.now().plusHours(1).withMinute(0).withSecond(0).withNano(0));
        return "reserve-dish";
    }

    @PostMapping("/reservations")
    public String createReservation(@RequestParam long restaurantId,
                                    @RequestParam long menuItemId,
                                    @RequestParam String pickupAt,
                                    HttpSession session,
                                    Model model) {
        User user = userService.currentUser(session).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        try {
            LocalDateTime pickup = LocalDateTime.parse(pickupAt);
            Restaurant restaurant = restaurantService.requireById(restaurantId);
            MenuItem menuItem = restaurantService.findMenuItem(restaurantId, menuItemId);
            Reservation reservation = reservationService.reserve(user, restaurant, menuItem, pickup);
            return "redirect:/profile?msg=Reserva%20creada%20%23" + reservation.getId();
        } catch (DateTimeParseException ex) {
            model.addAttribute("message", "Formato de fecha inválido.");
        } catch (IllegalArgumentException ex) {
            model.addAttribute("message", ex.getMessage());
        }

        Restaurant restaurant = restaurantService.requireById(restaurantId);
        model.addAttribute("restaurant", restaurant);
        model.addAttribute("menuItems", restaurant.getMenuItems());
        model.addAttribute("currentUser", user);
        return "reserve-dish";
    }

    @PostMapping("/reservations/{reservationId}/confirm")
    public String confirm(@PathVariable long reservationId) {
        Reservation reservation = reservationService.confirm(reservationId);
        return "redirect:/restaurants/" + reservation.getRestaurantId();
    }

    @PostMapping("/reservations/{reservationId}/reject")
    public String reject(@PathVariable long reservationId, @RequestParam(required = false, defaultValue = "No disponible") String reason) {
        Reservation reservation = reservationService.reject(reservationId, reason);
        return "redirect:/restaurants/" + reservation.getRestaurantId();
    }

    @PostMapping("/reservations/{reservationId}/out-of-stock")
    public String outOfStock(@PathVariable long reservationId) {
        Reservation reservation = reservationService.outOfStock(reservationId);
        return "redirect:/restaurants/" + reservation.getRestaurantId();
    }
}
