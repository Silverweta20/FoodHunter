package com.innovacionti.service.observer;

import com.innovacionti.domain.Reservation;

public interface ReservationObserver {
    void onReservationUpdated(Reservation reservation, String message);
}
