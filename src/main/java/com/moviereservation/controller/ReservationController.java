package com.moviereservation.controller;

import com.moviereservation.dto.ReservationDto;
import com.moviereservation.dto.UserDto;
import com.moviereservation.service.ReservationService;
import com.moviereservation.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private UserService userService;

    @GetMapping("/my")
    public List<ReservationDto> getMyReservations() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UserDto user = userService.getUserByUsername(username);
        Long userId = user.getId();
        return reservationService.getReservationsByUserId(userId);
    }

    @GetMapping("/my/upcoming")
    public List<ReservationDto> getMyUpcomingReservations() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UserDto user = userService.getUserByUsername(username);
        Long userId = user.getId();
        return reservationService.getUpcomingReservationsByUserId(userId);
    }

    @PostMapping
    public ResponseEntity<?> createReservation(@RequestBody Map<String, Object> reservationRequest) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            UserDto user = userService.getUserByUsername(username);
            Long userId = user.getId();
            Long showtimeId = Long.valueOf(reservationRequest.get("showtimeId").toString());

            @SuppressWarnings("unchecked")
            List<Long> seatIds = (List<Long>) reservationRequest.get("seatIds");

            ReservationDto reservation = reservationService.createReservation(userId, showtimeId, seatIds);
            return ResponseEntity.ok(reservation);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "An unexpected error occurred: " + e.getMessage()));
        }
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancelReservation(@PathVariable Long id) {
        try {
            ReservationDto cancelledReservation = reservationService.cancelReservation(id);
            return ResponseEntity.ok(cancelledReservation);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "An unexpected error occurred: " + e.getMessage()));
        }
    }

    // Admin endpoints

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public List<ReservationDto> getAllReservations() {
        return reservationService.getAllReservations();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getReservationById(@PathVariable Long id) {
        try {
            ReservationDto reservation = reservationService.getReservationById(id);
            return ResponseEntity.ok(reservation);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "An unexpected error occurred: " + e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<ReservationDto> getReservationsByUserId(@PathVariable Long userId) {
        return reservationService.getReservationsByUserId(userId);
    }
}
