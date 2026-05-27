package com.innovacionti.service.filter;

import com.innovacionti.domain.Restaurant;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AvailabilityFilterStrategy implements RestaurantFilterStrategy {
    @Override
    public List<Restaurant> apply(List<Restaurant> restaurants, FilterCriteria criteria) {
        if (criteria == null || !criteria.isAvailableOnly()) {
            return restaurants;
        }
        return restaurants.stream()
                .filter(Restaurant::isAvailable)
                .toList();
    }
}
