package com.innovacionti.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Review {
    private long id;
    private long restaurantId;
    private String userAlias;
    private int stars;
    private String comment;
    private List<String> photoUrls = new ArrayList<>();
    private LocalDateTime createdAt;

    public Review() {
    }

    public Review(long id, long restaurantId, String userAlias, int stars, String comment,
                  List<String> photoUrls, LocalDateTime createdAt) {
        this.id = id;
        this.restaurantId = restaurantId;
        this.userAlias = userAlias;
        this.stars = stars;
        this.comment = comment;
        if (photoUrls != null) {
            this.photoUrls = photoUrls;
        }
        this.createdAt = createdAt;
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

    public String getUserAlias() {
        return userAlias;
    }

    public void setUserAlias(String userAlias) {
        this.userAlias = userAlias;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<String> getPhotoUrls() {
        return photoUrls;
    }

    public void setPhotoUrls(List<String> photoUrls) {
        this.photoUrls = photoUrls != null ? photoUrls : new ArrayList<>();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
