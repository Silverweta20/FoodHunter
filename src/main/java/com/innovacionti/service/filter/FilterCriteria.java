package com.innovacionti.service.filter;

public class FilterCriteria {
    private String query;
    private Integer maxPrice;
    private String cuisine;
    private Double maxDistanceKm;
    private boolean availableOnly;

    public FilterCriteria() {
    }

    public FilterCriteria(String query, Integer maxPrice, String cuisine, Double maxDistanceKm, boolean availableOnly) {
        this.query = query;
        this.maxPrice = maxPrice;
        this.cuisine = cuisine;
        this.maxDistanceKm = maxDistanceKm;
        this.availableOnly = availableOnly;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Integer getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Integer maxPrice) {
        this.maxPrice = maxPrice;
    }

    public String getCuisine() {
        return cuisine;
    }

    public void setCuisine(String cuisine) {
        this.cuisine = cuisine;
    }

    public Double getMaxDistanceKm() {
        return maxDistanceKm;
    }

    public void setMaxDistanceKm(Double maxDistanceKm) {
        this.maxDistanceKm = maxDistanceKm;
    }

    public boolean isAvailableOnly() {
        return availableOnly;
    }

    public void setAvailableOnly(boolean availableOnly) {
        this.availableOnly = availableOnly;
    }
}
