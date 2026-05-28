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
import java.util.Arrays;
import java.util.List;

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

    @GetMapping("/restaurants/{restaurantId}/checkout")
    public String checkoutPage(@PathVariable long restaurantId,
                               @RequestParam long menuItemId,
                               @RequestParam String pickupAt,
                               @RequestParam(required = false, defaultValue = "") String selectedIngredients,
                               HttpSession session,
                               Model model) {
        User user = userService.currentUser(session).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        Restaurant restaurant = restaurantService.requireById(restaurantId);
        MenuItem selectedMenuItem = restaurantService.findMenuItem(restaurantId, menuItemId);

        List<String> selectedIngredientNames = Arrays.stream(selectedIngredients.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        model.addAttribute("restaurant", restaurant);
        model.addAttribute("selectedMenuItem", selectedMenuItem);
        model.addAttribute("pickupAt", pickupAt);
        model.addAttribute("selectedIngredientNames", selectedIngredientNames);
        return "checkout-order";
    }

    @PostMapping("/reservations")
    public String createReservation(@RequestParam long restaurantId,
                                    @RequestParam long menuItemId,
                                    @RequestParam String pickupAt,
                                    @RequestParam(required = false, defaultValue = "1") int quantity,
                                    HttpSession session,
                                    Model model) {
        User user = userService.currentUser(session).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        int safeQuantity = Math.max(1, Math.min(quantity, 10));

        try {
            LocalDateTime pickup = LocalDateTime.parse(pickupAt);
            Restaurant restaurant = restaurantService.requireById(restaurantId);
            MenuItem menuItem = restaurantService.findMenuItem(restaurantId, menuItemId);

            Reservation lastReservation = null;
            for (int i = 0; i < safeQuantity; i++) {
                lastReservation = reservationService.reserve(user, restaurant, menuItem, pickup);
            }

            if (safeQuantity == 1 && lastReservation != null) {
                return "redirect:/profile?msg=Reserva%20creada%20%23" + lastReservation.getId();
            }
            return "redirect:/profile?msg=Se%20crearon%20" + safeQuantity + "%20reservas";
        } catch (DateTimeParseException ex) {
            model.addAttribute("message", "Formato de fecha invalido.");
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
