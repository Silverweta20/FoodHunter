package com.innovacionti.service.filter;

import com.innovacionti.domain.Restaurant;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CuisineFilterStrategy implements RestaurantFilterStrategy {
    @Override
    public List<Restaurant> apply(List<Restaurant> restaurants, FilterCriteria criteria) {
        if (criteria == null || criteria.getCuisine() == null || criteria.getCuisine().isBlank() || "all".equalsIgnoreCase(criteria.getCuisine())) {
            return restaurants;
        }
        String cuisine = criteria.getCuisine().toLowerCase();
        return restaurants.stream()
                .filter(r -> r.getCuisine() != null && r.getCuisine().toLowerCase().contains(cuisine))
                .toList();
    }
}
