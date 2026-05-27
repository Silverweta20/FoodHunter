package com.innovacionti.service.review;

import com.innovacionti.domain.Restaurant;
import com.innovacionti.domain.Review;
import com.innovacionti.domain.User;

import java.util.List;

public interface ReviewFactory {
    Review create(User user, Restaurant restaurant, int stars, String comment, List<String> photoUrls);
}
