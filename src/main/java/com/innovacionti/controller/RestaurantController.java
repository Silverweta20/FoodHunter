package com.innovacionti.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.innovacionti.domain.ReservationStatus;
import com.innovacionti.domain.Restaurant;
import com.innovacionti.domain.User;
import com.innovacionti.service.NotificationService;
import com.innovacionti.service.ReservationService;
import com.innovacionti.service.RestaurantService;
import com.innovacionti.service.UserService;
import com.innovacionti.service.filter.FilterCriteria;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class RestaurantController {
    private final RestaurantService restaurantService;
    private final ReservationService reservationService;
    private final UserService userService;
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${google.maps.api.key:demo-key-fallback}")
    private String googleMapsApiKey;

    public RestaurantController(RestaurantService restaurantService,
                                ReservationService reservationService,
                                UserService userService,
                                NotificationService notificationService) {
        this.restaurantService = restaurantService;
        this.reservationService = reservationService;
        this.userService = userService;
        this.notificationService = notificationService;
    }

    @GetMapping("/home")
    public String home(@RequestParam(value = "q", required = false) String query,
                       @RequestParam(value = "maxPrice", required = false) Integer maxPrice,
                       @RequestParam(value = "cuisine", required = false) String cuisine,
                       @RequestParam(value = "maxDistanceKm", required = false) Double maxDistanceKm,
                       @RequestParam(value = "availableOnly", required = false, defaultValue = "false") boolean availableOnly,
                       HttpSession session,
                       Model model) {
        FilterCriteria criteria = new FilterCriteria(query, maxPrice, cuisine, maxDistanceKm, availableOnly);
        List<Restaurant> restaurants = restaurantService.search(criteria);
        model.addAttribute("restaurants", restaurants);
        model.addAttribute("criteria", criteria);
        model.addAttribute("user", userService.currentUser(session).orElse(null));
        return "home";
    }

    @ResponseBody
    @GetMapping("/api/restaurants/search")
    public List<RestaurantCardDto> apiSearch(@RequestParam(value = "q", required = false) String query,
                                             @RequestParam(value = "maxPrice", required = false) Integer maxPrice,
                                             @RequestParam(value = "cuisine", required = false) String cuisine,
                                             @RequestParam(value = "maxDistanceKm", required = false) Double maxDistanceKm,
                                             @RequestParam(value = "availableOnly", required = false, defaultValue = "false") boolean availableOnly) {
        FilterCriteria criteria = new FilterCriteria(query, maxPrice, cuisine, maxDistanceKm, availableOnly);
        return restaurantService.search(criteria).stream().map(RestaurantCardDto::from).toList();
    }

    @GetMapping("/restaurants/{id}")
    public String detail(@PathVariable long id, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        try {
            Restaurant restaurant = restaurantService.requireById(id);
            User currentUser = userService.currentUser(session).orElse(null);
            boolean canReview = currentUser != null && currentUser.isRegistered() && currentUser.isInstitutional();
            boolean canManage = currentUser != null
                    && "RESTAURANT".equalsIgnoreCase(currentUser.getRole())
                    && currentUser.getEmail().equalsIgnoreCase(restaurant.getManagerEmail());

            model.addAttribute("restaurant", restaurant);
            model.addAttribute("reservations", reservationService.findByRestaurantId(id));
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("canReview", canReview);
            model.addAttribute("canManage", canManage);
            model.addAttribute("restaurantNotifications", notificationService.findRestaurantNotifications(id));
            return "restaurant-detail";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "El restaurante no fue encontrado. ID: " + id);
            return "redirect:/home";
        }
    }

    @GetMapping("/map")
    public String map(Model model, HttpSession session) {
        List<Restaurant> nearby = restaurantService.nearby();
        model.addAttribute("restaurants", nearby);
        model.addAttribute("user", userService.currentUser(session).orElse(null));

        // Pasar API key desde propiedades
        model.addAttribute("googleMapsApiKey", googleMapsApiKey);

        // Pasar JSON de restaurantes para el JS
        String restaurantsJson = "[]";
        try {
            restaurantsJson = objectMapper.writeValueAsString(nearby);
        } catch (JsonProcessingException e) {
            // si falla, dejamos el arreglo vacío
        }
        model.addAttribute("restaurantsJson", restaurantsJson);

        return "map";
    }

    @ResponseBody
    @GetMapping("/api/restaurants/visited")
    public List<RestaurantCardDto> getVisitedRestaurants(
            @RequestParam(value = "userLat", required = false, defaultValue = "4.7110") double userLat,
            @RequestParam(value = "userLng", required = false, defaultValue = "-74.0721") double userLng,
            @RequestParam(value = "radius", required = false, defaultValue = "5") double radiusKm) {
        return restaurantService.getVisitedNearby(userLat, userLng, radiusKm)
                .stream()
                .map(RestaurantCardDto::from)
                .toList();
    }

    @GetMapping("/restaurants/{id}/status/{status}")
    public String statusView(@PathVariable long id,
                             @PathVariable String status,
                             Model model,
                             HttpSession session) {
        ReservationStatus reservationStatus = ReservationStatus.valueOf(status.toUpperCase());
        model.addAttribute("restaurant", restaurantService.requireById(id));
        model.addAttribute("reservations", reservationService.findByRestaurantIdAndStatus(id, reservationStatus));
        model.addAttribute("currentUser", userService.currentUser(session).orElse(null));
        return "restaurant-detail";
    }

    public record RestaurantCardDto(long id, String name, String cuisine, String location, double rating,
                                    int rankingPosition, int executiveLunchPrice, double distanceKm,
                                    boolean available, String heroImageUrl, double latitude, double longitude) {
        public static RestaurantCardDto from(Restaurant restaurant) {
            return new RestaurantCardDto(
                    restaurant.getId(),
                    restaurant.getName(),
                    restaurant.getCuisine(),
                    restaurant.getLocation(),
                    restaurant.getRating(),
                    restaurant.getRankingPosition(),
                    restaurant.getExecutiveLunchPrice(),
                    restaurant.getDistanceKm(),
                    restaurant.isAvailable(),
                    restaurant.getHeroImageUrl(),
                    restaurant.getLatitude(),
                    restaurant.getLongitude()
            );
        }
    }
}