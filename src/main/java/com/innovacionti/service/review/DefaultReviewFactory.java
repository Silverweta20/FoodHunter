package com.innovacionti.service.review;

import com.innovacionti.domain.Restaurant;
import com.innovacionti.domain.Review;
import com.innovacionti.domain.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class DefaultReviewFactory implements ReviewFactory {
    private static final Pattern INSTITUTIONAL_EMAIL = Pattern.compile("^[A-Za-z0-9._%+-]+@([A-Za-z0-9-]+\\.)?(edu\\.co|edu)$");

    @Override
    public Review create(User user, Restaurant restaurant, int stars, String comment, List<String> photoUrls) {
        if (user == null || !user.isRegistered()) {
            throw new IllegalArgumentException("Solo usuarios registrados pueden dejar reseñas.");
        }
        if (user.getEmail() == null || !INSTITUTIONAL_EMAIL.matcher(user.getEmail().toLowerCase()).matches()) {
            throw new IllegalArgumentException("La reseña solo está permitida para correo institucional.");
        }
        if (stars < 1 || stars > 5) {
            throw new IllegalArgumentException("La calificación debe estar entre 1 y 5 estrellas.");
        }

        Review review = new Review();
        review.setUserAlias(user.getAlias() == null || user.getAlias().isBlank() ? user.getEmail() : user.getAlias());
        review.setRestaurantId(restaurant.getId());
        review.setStars(stars);
        review.setComment(comment);
        review.setPhotoUrls(photoUrls);
        review.setCreatedAt(LocalDateTime.now());
        return review;
    }
}
