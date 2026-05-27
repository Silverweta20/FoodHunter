package com.innovacionti.service.observer;

import com.innovacionti.domain.Reservation;
import com.innovacionti.service.NotificationService;
import org.springframework.stereotype.Component;

@Component
public class RestaurantNotificationObserver implements ReservationObserver {
    private final NotificationService notificationService;

    public RestaurantNotificationObserver(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    public void onReservationUpdated(Reservation reservation, String message) {
        notificationService.pushToRestaurant(reservation.getRestaurantId(), message + " - #" + reservation.getId());
    }
}
