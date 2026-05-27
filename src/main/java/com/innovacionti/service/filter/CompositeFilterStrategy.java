package com.innovacionti.service.filter;

import com.innovacionti.domain.Restaurant;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CompositeFilterStrategy {
    private final PriceFilterStrategy priceFilterStrategy;
    private final DistanceFilterStrategy distanceFilterStrategy;
    private final CuisineFilterStrategy cuisineFilterStrategy;
    private final AvailabilityFilterStrategy availabilityFilterStrategy;

    public CompositeFilterStrategy(PriceFilterStrategy priceFilterStrategy,
                                   DistanceFilterStrategy distanceFilterStrategy,
                                   CuisineFilterStrategy cuisineFilterStrategy,
                                   AvailabilityFilterStrategy availabilityFilterStrategy) {
        this.priceFilterStrategy = priceFilterStrategy;
        this.distanceFilterStrategy = distanceFilterStrategy;
        this.cuisineFilterStrategy = cuisineFilterStrategy;
        this.availabilityFilterStrategy = availabilityFilterStrategy;
    }

    public List<Restaurant> applyAll(List<Restaurant> restaurants, FilterCriteria criteria) {
        List<Restaurant> current = restaurants;
        current = priceFilterStrategy.apply(current, criteria);
        current = distanceFilterStrategy.apply(current, criteria);
        current = cuisineFilterStrategy.apply(current, criteria);
        current = availabilityFilterStrategy.apply(current, criteria);
        return current;
    }
}
