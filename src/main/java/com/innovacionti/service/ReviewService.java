package com.innovacionti.service;

import com.innovacionti.domain.Restaurant;
import com.innovacionti.domain.Review;
import com.innovacionti.domain.User;
import com.innovacionti.service.review.ReviewFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReviewService {
    private final ReviewFactory reviewFactory;
    private final RestaurantService restaurantService;

    public ReviewService(ReviewFactory reviewFactory, RestaurantService restaurantService) {
        this.reviewFactory = reviewFactory;
        this.restaurantService = restaurantService;
    }

    public Review addReview(User user, long restaurantId, int stars, String comment, List<String> photoUrls) {
        Restaurant restaurant = restaurantService.requireById(restaurantId);
        Review review = reviewFactory.create(user, restaurant, stars, comment, photoUrls);
        restaurantService.attachReview(restaurantId, review);
        return review;
    }

    public List<Review> findByRestaurantId(long restaurantId) {
        Restaurant restaurant = restaurantService.requireById(restaurantId);
        return new ArrayList<>(restaurant.getReviews());
    }
}
