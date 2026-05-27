package com.innovacionti.service;

import com.innovacionti.domain.MenuItem;
import com.innovacionti.domain.Reservation;
import com.innovacionti.domain.ReservationStatus;
import com.innovacionti.domain.Restaurant;
import com.innovacionti.domain.User;
import com.innovacionti.service.observer.ReservationObserver;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ReservationService {
    private final ConcurrentHashMap<Long, Reservation> reservations = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(1);
    private final List<ReservationObserver> observers;
    private final RestaurantService restaurantService;

    public ReservationService(List<ReservationObserver> observers, RestaurantService restaurantService) {
        this.observers = observers;
        this.restaurantService = restaurantService;
    }

    public Reservation reserve(User user, Restaurant restaurant, MenuItem dish, LocalDateTime pickupAt) {
        if (user == null) {
            throw new IllegalArgumentException("Debes iniciar sesión.");
        }
        if (pickupAt == null) {
            throw new IllegalArgumentException("Selecciona una hora de retiro.");
        }
        if (pickupAt.isBefore(LocalDateTime.now().plusMinutes(30))) {
            throw new IllegalArgumentException("La reserva debe hacerse con al menos 30 minutos de anticipación.");
        }
        if (!restaurant.isAvailable()) {
            throw new IllegalArgumentException("El restaurante no está disponible.");
        }
        restaurantService.decreaseMenuItemStock(restaurant.getId(), dish.getId());

        Reservation reservation = new Reservation(
                sequence.getAndIncrement(),
                restaurant.getId(),
                restaurant.getName(),
                dish.getId(),
                dish.getName(),
                user.getEmail(),
                user.getAlias(),
                dish.getPrice(),
                pickupAt,
                LocalDateTime.now(),
                ReservationStatus.PENDING
        );
        reservations.put(reservation.getId(), reservation);
        notifyObservers(reservation, "Nueva reserva recibida");
        return reservation;
    }

    public Reservation confirm(long reservationId) {
        Reservation reservation = findOrThrow(reservationId);
        reservation.setStatus(ReservationStatus.CONFIRMED);
        notifyObservers(reservation, "Reserva confirmada");
        return reservation;
    }

    public Reservation reject(long reservationId, String reason) {
        Reservation reservation = findOrThrow(reservationId);
        reservation.setStatus(ReservationStatus.REJECTED);
        reservation.setReason(reason);
        restaurantService.increaseMenuItemStock(reservation.getRestaurantId(), reservation.getMenuItemId());
        notifyObservers(reservation, "Reserva rechazada: " + reason);
        return reservation;
    }

    public Reservation outOfStock(long reservationId) {
        Reservation reservation = findOrThrow(reservationId);
        reservation.setStatus(ReservationStatus.OUT_OF_STOCK);
        reservation.setReason("El plato se agotó antes de tu llegada");
        restaurantService.increaseMenuItemStock(reservation.getRestaurantId(), reservation.getMenuItemId());
        notifyObservers(reservation, "Tu plato se agotó antes de tu llegada");
        return reservation;
    }

    public Reservation findById(long id) {
        return findOrThrow(id);
    }

    public List<Reservation> findAll() {
        return new ArrayList<>(reservations.values());
    }

    public List<Reservation> findByUserEmail(String email) {
        return reservations.values().stream()
                .filter(r -> r.getUserEmail() != null && r.getUserEmail().equalsIgnoreCase(email))
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .toList();
    }

    public List<Reservation> findByRestaurantId(long restaurantId) {
        return reservations.values().stream()
                .filter(r -> r.getRestaurantId() == restaurantId)
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .toList();
    }

    public List<Reservation> findByRestaurantIdAndStatus(long restaurantId, ReservationStatus status) {
        return reservations.values().stream()
                .filter(r -> r.getRestaurantId() == restaurantId)
                .filter(r -> status == null || r.getStatus() == status)
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .toList();
    }

    private Reservation findOrThrow(long id) {
        Reservation reservation = reservations.get(id);
        if (reservation == null) {
            throw new NoSuchElementException("Reserva no encontrada.");
        }
        return reservation;
    }

    private void notifyObservers(Reservation reservation, String message) {
        for (ReservationObserver observer : observers) {
            observer.onReservationUpdated(reservation, message);
        }
    }
}
