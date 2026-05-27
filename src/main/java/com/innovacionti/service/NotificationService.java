package com.innovacionti.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NotificationService {
    private final Map<String, List<String>> userNotifications = new ConcurrentHashMap<>();
    private final Map<Long, List<String>> restaurantNotifications = new ConcurrentHashMap<>();

    public void pushToUser(String email, String message) {
        if (email == null) {
            return;
        }
        userNotifications.computeIfAbsent(email.toLowerCase(), key -> Collections.synchronizedList(new ArrayList<>())).add(message);
    }

    public void pushToRestaurant(long restaurantId, String message) {
        restaurantNotifications.computeIfAbsent(restaurantId, key -> Collections.synchronizedList(new ArrayList<>())).add(message);
    }

    public List<String> findUserNotifications(String email) {
        if (email == null) {
            return List.of();
        }
        return new ArrayList<>(userNotifications.getOrDefault(email.toLowerCase(), List.of()));
    }

    public List<String> findRestaurantNotifications(long restaurantId) {
        return new ArrayList<>(restaurantNotifications.getOrDefault(restaurantId, List.of()));
    }
}
