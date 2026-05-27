package com.innovacionti.service.filter;

import com.innovacionti.domain.Restaurant;

import java.util.List;

public interface RestaurantFilterStrategy {
    List<Restaurant> apply(List<Restaurant> restaurants, FilterCriteria criteria);
}
