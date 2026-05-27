package com.innovacionti.domain;

import java.time.LocalDateTime;

public class Reservation {
    private long id;
    private long restaurantId;
    private String restaurantName;
    private long menuItemId;
    private String dishName;
    private String userEmail;
    private String userAlias;
    private int dishPrice;
    private LocalDateTime pickupAt;
    private LocalDateTime createdAt;
    private ReservationStatus status;
    private String reason;

    public Reservation() {
    }

    public Reservation(long id, long restaurantId, String restaurantName, long menuItemId, String dishName,
                       String userEmail, String userAlias, int dishPrice, LocalDateTime pickupAt,
                       LocalDateTime createdAt, ReservationStatus status) {
        this.id = id;
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
        this.menuItemId = menuItemId;
        this.dishName = dishName;
        this.userEmail = userEmail;
        this.userAlias = userAlias;
        this.dishPrice = dishPrice;
        this.pickupAt = pickupAt;
        this.createdAt = createdAt;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public long getMenuItemId() {
        return menuItemId;
    }

    public void setMenuItemId(long menuItemId) {
        this.menuItemId = menuItemId;
    }

    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserAlias() {
        return userAlias;
    }

    public void setUserAlias(String userAlias) {
        this.userAlias = userAlias;
    }

    public int getDishPrice() {
        return dishPrice;
    }

    public void setDishPrice(int dishPrice) {
        this.dishPrice = dishPrice;
    }

    public LocalDateTime getPickupAt() {
        return pickupAt;
    }

    public void setPickupAt(LocalDateTime pickupAt) {
        this.pickupAt = pickupAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
