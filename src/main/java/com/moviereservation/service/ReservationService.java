package com.moviereservation.service;

import com.moviereservation.dto.ReservationDto;
import com.moviereservation.model.*;
import com.moviereservation.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationService {
    
    @Autowired
    private ReservationRepository reservationRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private MovieRepository movieRepository;
    
    @Autowired
    private ShowtimeRepository showtimeRepository;
    
    @Autowired
    private SeatRepository seatRepository;
    
    public List<ReservationDto> getAllReservations() {
        return reservationRepository.findAll().stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }
    
    public ReservationDto getReservationById(Long id) {
        Reservation reservation = reservationRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));
            
        return convertToDto(reservation);
    }
    
    public List<ReservationDto> getReservationsByUserId(Long userId) {
        return reservationRepository.findByUserId(userId).stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }
    
    public List<ReservationDto> getUpcomingReservationsByUserId(Long userId) {
        return reservationRepository.findUpcomingByUserId(userId, LocalDateTime.now()).stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }
    
    public ReservationDto createReservation(Long userId, Long showtimeId, List<Long> seatIds) {
        // Check if user exists
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
            
        // Check if showtime exists
        Showtime showtime = showtimeRepository.findById(showtimeId)
            .orElseThrow(() -> new IllegalArgumentException("Showtime not found"));
            
        // Check if showtime is in the future
        if (showtime.getStartTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Cannot book past showtimes");
        }
            
        // Check if all seats exist and are available for the showtime
        List<Seat> seats = new ArrayList<>();
        for (Long seatId : seatIds) {
            Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new IllegalArgumentException("Seat not found: " + seatId));
                
            if (!seat.getShowtimeId().equals(showtimeId)) {
                throw new IllegalArgumentException("Seat " + seat.getSeatNumber() + " does not belong to the selected showtime");
            }
            
            if (seat.isReserved()) {
                throw new IllegalArgumentException("Seat " + seat.getSeatNumber() + " is already reserved");
            }
            
            seats.add(seat);
        }
        
        // Get movie for price
        Movie movie = movieRepository.findById(showtime.getMovieId())
            .orElseThrow(() -> new IllegalArgumentException("Movie not found"));
            
        // Create reservation
        Reservation reservation = new Reservation();
        reservation.setUserId(userId);
        reservation.setShowtimeId(showtimeId);
        reservation.setSeatIds(seatIds);
        reservation.setReservationTime(LocalDateTime.now());
        reservation.setTotalPrice(movie.getPrice() * seatIds.size());
        reservation.setCancelled(false);
        
        Reservation savedReservation = reservationRepository.save(reservation);
        
        // Update seats to reserved
        for (Seat seat : seats) {
            seat.setReserved(true);
            seatRepository.save(seat);
        }
        
        return convertToDto(savedReservation);
    }
    
    public ReservationDto cancelReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));
            
        // Check if reservation is already cancelled
        if (reservation.isCancelled()) {
            throw new IllegalArgumentException("Reservation is already cancelled");
        }
        
        // Check if showtime is in the future
        Showtime showtime = showtimeRepository.findById(reservation.getShowtimeId())
            .orElseThrow(() -> new IllegalArgumentException("Showtime not found"));
            
        if (showtime.getStartTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Cannot cancel past reservations");
        }
        
        // Mark reservation as cancelled
        reservation.setCancelled(true);
        Reservation updatedReservation = reservationRepository.save(reservation);
        
        // Update seats to available
        for (Long seatId : reservation.getSeatIds()) {
            Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new IllegalArgumentException("Seat not found: " + seatId));
                
            seat.setReserved(false);
            seatRepository.save(seat);
        }
        
        return convertToDto(updatedReservation);
    }
    
    private ReservationDto convertToDto(Reservation reservation) {
        ReservationDto dto = new ReservationDto();
        dto.setId(reservation.getId());
        dto.setUserId(reservation.getUserId());
        dto.setShowtimeId(reservation.getShowtimeId());
        dto.setReservationTime(reservation.getReservationTime());
        dto.setTotalPrice(reservation.getTotalPrice());
        dto.setCancelled(reservation.isCancelled());
        
        // Get username
        userRepository.findById(reservation.getUserId()).ifPresent(user -> {
            dto.setUsername(user.getUsername());
        });
        
        // Get showtime and movie details
        showtimeRepository.findById(reservation.getShowtimeId()).ifPresent(showtime -> {
            dto.setShowtimeStartTime(showtime.getStartTime());
            
            movieRepository.findById(showtime.getMovieId()).ifPresent(movie -> {
                dto.setMovieTitle(movie.getTitle());
            });
        });
        
        // Get seat numbers
        List<String> seatNumbers = new ArrayList<>();
        for (Long seatId : reservation.getSeatIds()) {
            seatRepository.findById(seatId).ifPresent(seat -> {
                seatNumbers.add(seat.getSeatNumber());
            });
        }
        dto.setSeatNumbers(seatNumbers);
        
        return dto;
    }
}
