package com.innovacionti.service;

import com.innovacionti.domain.MenuItem;
import com.innovacionti.domain.Restaurant;
import com.innovacionti.domain.Review;
import com.innovacionti.service.filter.CompositeFilterStrategy;
import com.innovacionti.service.filter.FilterCriteria;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class RestaurantService {
    private final ConcurrentHashMap<Long, Restaurant> restaurants = new ConcurrentHashMap<>();
    private final AtomicLong restaurantSequence = new AtomicLong(1);
    private final AtomicLong menuItemSequence = new AtomicLong(1);
    private final AtomicLong reviewSequence = new AtomicLong(1);
    private final CompositeFilterStrategy filterStrategy;

    public RestaurantService(CompositeFilterStrategy filterStrategy) {
        this.filterStrategy = filterStrategy;
    }

    public Restaurant save(Restaurant restaurant) {
        if (restaurant.getId() <= 0) {
            restaurant.setId(restaurantSequence.getAndIncrement());
        }
        for (MenuItem item : restaurant.getMenuItems()) {
            if (item.getId() <= 0) {
                item.setId(menuItemSequence.getAndIncrement());
            }
            item.setRestaurantId(restaurant.getId());
        }
        for (Review review : restaurant.getReviews()) {
            if (review.getId() <= 0) {
                review.setId(reviewSequence.getAndIncrement());
            }
            review.setRestaurantId(restaurant.getId());
        }
        recalculateRating(restaurant);
        restaurants.put(restaurant.getId(), restaurant);
        return restaurant;
    }

    public List<Restaurant> findAll() {
        return restaurants.values().stream()
                .sorted(Comparator.comparingInt(Restaurant::getRankingPosition))
                .toList();
    }

    public Optional<Restaurant> findById(long id) {
        return Optional.ofNullable(restaurants.get(id));
    }

    public Restaurant requireById(long id) {
        return findById(id).orElseThrow(() -> new IllegalArgumentException("Restaurante no encontrado."));
    }

    public List<Restaurant> search(FilterCriteria criteria) {
        String query = criteria != null ? criteria.getQuery() : null;
        List<Restaurant> current = new ArrayList<>(findAll());

        if (query != null && !query.isBlank()) {
            String q = query.toLowerCase(Locale.ROOT);
            current = current.stream()
                    .filter(r -> contains(r.getName(), q) || contains(r.getCuisine(), q) || contains(r.getLocation(), q) || contains(r.getDescription(), q))
                    .toList();
        }

        current = filterStrategy.applyAll(current, criteria);
        return current.stream()
                .sorted(Comparator.comparingDouble(Restaurant::getRating).reversed().thenComparing(Restaurant::getRankingPosition))
                .toList();
    }

    public List<Restaurant> featured() {
        return findAll().stream().limit(4).toList();
    }

    public List<Restaurant> nearby() {
        return findAll().stream().sorted(Comparator.comparingDouble(Restaurant::getDistanceKm)).toList();
    }

    public Optional<Restaurant> findByManagerEmail(String managerEmail) {
        if (managerEmail == null) {
            return Optional.empty();
        }
        return restaurants.values().stream()
                .filter(r -> managerEmail.equalsIgnoreCase(r.getManagerEmail()))
                .findFirst();
    }

    public MenuItem findMenuItem(long restaurantId, long menuItemId) {
        Restaurant restaurant = requireById(restaurantId);
        return restaurant.getMenuItems().stream()
                .filter(item -> item.getId() == menuItemId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Plato no encontrado."));
    }

    public void decreaseMenuItemStock(long restaurantId, long menuItemId) {
        MenuItem item = findMenuItem(restaurantId, menuItemId);
        if (item.getAvailableUnits() <= 0) {
            throw new IllegalArgumentException("El plato está agotado.");
        }
        item.setAvailableUnits(item.getAvailableUnits() - 1);
    }

    public void increaseMenuItemStock(long restaurantId, long menuItemId) {
        MenuItem item = findMenuItem(restaurantId, menuItemId);
        item.setAvailableUnits(item.getAvailableUnits() + 1);
    }

    public void attachReview(long restaurantId, Review review) {
        Restaurant restaurant = requireById(restaurantId);
        review.setRestaurantId(restaurantId);
        review.setId(reviewSequence.getAndIncrement());
        restaurant.getReviews().add(review);
        recalculateRating(restaurant);
    }

    public void recalculateRating(Restaurant restaurant) {
        if (restaurant.getReviews().isEmpty()) {
            restaurant.setRating(0.0);
            return;
        }
        double avg = restaurant.getReviews().stream().mapToInt(Review::getStars).average().orElse(0.0);
        restaurant.setRating(Math.round(avg * 10.0) / 10.0);
    }

    private boolean contains(String value, String q) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(q);
    }

    /**
     * Obtener restaurantes visitados cercanos al usuario
     * Simula geoubicación y distancia
     */
    public List<Restaurant> getVisitedNearby(double userLat, double userLng, double radiusKm) {
        return findAll().stream()
                .filter(r -> calculateDistance(userLat, userLng,
                        r.getDistanceKm() > 0 ? userLat + (Math.random() * 0.05) : userLat,
                        r.getDistanceKm() > 0 ? userLng + (Math.random() * 0.05) : userLng) <= radiusKm)
                .sorted(Comparator.comparingDouble(Restaurant::getDistanceKm))
                .toList();
    }

    /**
     * Calcular distancia entre dos puntos geográficos (Fórmula de Haversine simplificada)
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radio de la Tierra en km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
