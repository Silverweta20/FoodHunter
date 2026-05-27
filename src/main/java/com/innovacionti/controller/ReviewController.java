package com.innovacionti.controller;

import com.innovacionti.domain.User;
import com.innovacionti.service.ReviewService;
import com.innovacionti.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Controller
public class ReviewController {
    private final ReviewService reviewService;
    private final UserService userService;

    public ReviewController(ReviewService reviewService, UserService userService) {
        this.reviewService = reviewService;
        this.userService = userService;
    }

    @PostMapping("/restaurants/{restaurantId}/reviews")
    public String createReview(@PathVariable long restaurantId,
                               @RequestParam int stars,
                               @RequestParam String comment,
                               @RequestParam(required = false, defaultValue = "") String photos,
                               HttpSession session) {
        User user = userService.currentUser(session).orElseThrow(() -> new IllegalArgumentException("Debes iniciar sesión."));
        List<String> photoUrls = Arrays.stream(photos.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .toList();

        reviewService.addReview(user, restaurantId, stars, comment, photoUrls);
        return "redirect:/restaurants/" + restaurantId;
    }
}
