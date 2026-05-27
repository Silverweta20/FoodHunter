package com.innovacionti.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Restaurant {
    private long id;
    private String name;
    private String cuisine;
    private String location;
    private String description;
    private String heroImageUrl;
    private String menuBoardImageUrl;
    private LocalDateTime menuBoardPublishedAt;
    private double distanceKm;
    private double rating;
    private int rankingPosition;
    private int executiveLunchPrice;
    private boolean available;
    private String openHours;
    private String managerEmail;
    private double latitude;  // NUEVA
    private double longitude; // NUEVA
    private List<MenuItem> menuItems = new ArrayList<>();
    private List<Review> reviews = new ArrayList<>();

    public Restaurant() {
    }

    public Restaurant(long id, String name, String cuisine, String location, String description,
                      String heroImageUrl, String menuBoardImageUrl, LocalDateTime menuBoardPublishedAt,
                      double distanceKm, double rating, int rankingPosition, int executiveLunchPrice,
                      boolean available, String openHours, String managerEmail, double latitude, double longitude) {
        this.id = id;
        this.name = name;
        this.cuisine = cuisine;
        this.location = location;
        this.description = description;
        this.heroImageUrl = heroImageUrl;
        this.menuBoardImageUrl = menuBoardImageUrl;
        this.menuBoardPublishedAt = menuBoardPublishedAt;
        this.distanceKm = distanceKm;
        this.rating = rating;
        this.rankingPosition = rankingPosition;
        this.executiveLunchPrice = executiveLunchPrice;
        this.available = available;
        this.openHours = openHours;
        this.managerEmail = managerEmail;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCuisine() {
        return cuisine;
    }

    public void setCuisine(String cuisine) {
        this.cuisine = cuisine;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHeroImageUrl() {
        return heroImageUrl;
    }

    public void setHeroImageUrl(String heroImageUrl) {
        this.heroImageUrl = heroImageUrl;
    }

    public String getMenuBoardImageUrl() {
        return menuBoardImageUrl;
    }

    public void setMenuBoardImageUrl(String menuBoardImageUrl) {
        this.menuBoardImageUrl = menuBoardImageUrl;
    }

    public LocalDateTime getMenuBoardPublishedAt() {
        return menuBoardPublishedAt;
    }

    public void setMenuBoardPublishedAt(LocalDateTime menuBoardPublishedAt) {
        this.menuBoardPublishedAt = menuBoardPublishedAt;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getRankingPosition() {
        return rankingPosition;
    }

    public void setRankingPosition(int rankingPosition) {
        this.rankingPosition = rankingPosition;
    }

    public int getExecutiveLunchPrice() {
        return executiveLunchPrice;
    }

    public void setExecutiveLunchPrice(int executiveLunchPrice) {
        this.executiveLunchPrice = executiveLunchPrice;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getOpenHours() {
        return openHours;
    }

    public void setOpenHours(String openHours) {
        this.openHours = openHours;
    }

    public String getManagerEmail() {
        return managerEmail;
    }

    public void setManagerEmail(String managerEmail) {
        this.managerEmail = managerEmail;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public List<MenuItem> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(List<MenuItem> menuItems) {
        this.menuItems = menuItems != null ? menuItems : new ArrayList<>();
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews != null ? reviews : new ArrayList<>();
    }
}