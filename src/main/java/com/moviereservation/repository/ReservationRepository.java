package com.moviereservation.repository;

import com.moviereservation.model.Reservation;
import com.moviereservation.util.JsonFileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ReservationRepository {
    
    @Autowired
    private JsonFileUtil<Reservation> reservationJsonFileUtil;
    
    public List<Reservation> findAll() {
        return reservationJsonFileUtil.readFromFile();
    }
    
    public Optional<Reservation> findById(Long id) {
        return findAll().stream()
                .filter(reservation -> reservation.getId().equals(id))
                .findFirst();
    }
    
    public List<Reservation> findByUserId(Long userId) {
        return findAll().stream()
                .filter(reservation -> reservation.getUserId().equals(userId))
                .collect(Collectors.toList());
    }
    
    public List<Reservation> findByShowtimeId(Long showtimeId) {
        return findAll().stream()
                .filter(reservation -> reservation.getShowtimeId().equals(showtimeId))
                .collect(Collectors.toList());
    }
    
    public List<Reservation> findUpcomingByUserId(Long userId, LocalDateTime currentTime) {
        return findAll().stream()
                .filter(reservation -> reservation.getUserId().equals(userId) && 
                                      !reservation.isCancelled())
                .collect(Collectors.toList());
    }
    
    public Reservation save(Reservation reservation) {
        List<Reservation> reservations = findAll();
        
        // If new reservation, generate ID
        if (reservation.getId() == null) {
            Long maxId = reservations.stream()
                    .mapToLong(Reservation::getId)
                    .max()
                    .orElse(0L);
            reservation.setId(maxId + 1);
            reservations.add(reservation);
        } else {
            // Update existing reservation
            reservations = reservations.stream()
                    .map(existingReservation -> existingReservation.getId().equals(reservation.getId()) ? 
                                               reservation : existingReservation)
                    .collect(Collectors.toList());
        }
        
        reservationJsonFileUtil.writeToFile(reservations);
        return reservation;
    }
    
    public void deleteById(Long id) {
        List<Reservation> reservations = findAll();
        reservations = reservations.stream()
                .filter(reservation -> !reservation.getId().equals(id))
                .collect(Collectors.toList());
        reservationJsonFileUtil.writeToFile(reservations);
    }
}
