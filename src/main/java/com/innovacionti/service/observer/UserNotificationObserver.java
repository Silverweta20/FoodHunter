package com.innovacionti.service.observer;

import com.innovacionti.domain.Reservation;
import com.innovacionti.service.NotificationService;
import org.springframework.stereotype.Component;

@Component
public class UserNotificationObserver implements ReservationObserver {
    private final NotificationService notificationService;

    public UserNotificationObserver(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    public void onReservationUpdated(Reservation reservation, String message) {
        notificationService.pushToUser(reservation.getUserEmail(), message + " - #" + reservation.getId());
    }
}
