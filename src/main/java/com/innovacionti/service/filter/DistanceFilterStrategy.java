package com.innovacionti.service.filter;

import com.innovacionti.domain.Restaurant;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DistanceFilterStrategy implements RestaurantFilterStrategy {
    @Override
    public List<Restaurant> apply(List<Restaurant> restaurants, FilterCriteria criteria) {
        if (criteria == null || criteria.getMaxDistanceKm() == null) {
            return restaurants;
        }
        return restaurants.stream()
                .filter(r -> r.getDistanceKm() <= criteria.getMaxDistanceKm())
                .toList();
    }
}
