package com.moviereservation.service;

import com.moviereservation.dto.ReportDto;
import com.moviereservation.dto.UserDto;
import com.moviereservation.model.Reservation;
import com.moviereservation.model.Showtime;
import com.moviereservation.model.User;
import com.moviereservation.repository.MovieRepository;
import com.moviereservation.repository.ReservationRepository;
import com.moviereservation.repository.ShowtimeRepository;
import com.moviereservation.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private MovieRepository movieRepository;
    
    @Autowired
    private ShowtimeRepository showtimeRepository;
    
    @Autowired
    private ReservationRepository reservationRepository;
    
    public UserDto promoteToAdmin(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
            
        user.setRole("ADMIN");
        User updatedUser = userRepository.save(user);
        
        return convertToDto(updatedUser);
    }
    
    public List<ReportDto> generateRevenueReports() {
        List<ReportDto> reports = new ArrayList<>();
        
        // Get all movies
        movieRepository.findAll().forEach(movie -> {
            ReportDto report = new ReportDto();
            report.setMovieId(movie.getId());
            report.setMovieTitle(movie.getTitle());
            
            // Initialize counters
            int totalReservations = 0;
            int totalSeatsReserved = 0;
            double totalRevenue = 0.0;
            int totalSeatsAvailable = 0;
            
            // Get all showtimes for this movie
            List<Showtime> showtimes = showtimeRepository.findByMovieId(movie.getId());
            
            for (Showtime showtime : showtimes) {
                // Add total seats available for occupancy calculation
                totalSeatsAvailable += showtime.getTotalSeats();
                
                // Get all non-cancelled reservations for this showtime
                List<Reservation> reservations = reservationRepository.findByShowtimeId(showtime.getId())
                    .stream()
                    .filter(reservation -> !reservation.isCancelled())
                    .collect(Collectors.toList());
                
                // Update counters
                totalReservations += reservations.size();
                
                for (Reservation reservation : reservations) {
                    totalSeatsReserved += reservation.getSeatIds().size();
                    totalRevenue += reservation.getTotalPrice();
                }
            }
            
            // Set report values
            report.setTotalReservations(totalReservations);
            report.setTotalSeatsReserved(totalSeatsReserved);
            report.setTotalRevenue(totalRevenue);
            
            // Calculate occupancy rate
            double occupancyRate = totalSeatsAvailable > 0 ? 
                    ((double) totalSeatsReserved / totalSeatsAvailable) * 100 : 0.0;
            report.setOccupancyRate(occupancyRate);
            
            reports.add(report);
        });
        
        return reports;
    }
    
    public Map<String, Integer> generateOccupancyByTheatre() {
        Map<String, Integer> occupancyByTheatre = new HashMap<>();
        
        // Get all showtimes
        List<Showtime> showtimes = showtimeRepository.findAll();
        
        for (Showtime showtime : showtimes) {
            String theatre = showtime.getTheatreHall();
            
            // Get all non-cancelled reservations for this showtime
            List<Reservation> reservations = reservationRepository.findByShowtimeId(showtime.getId())
                .stream()
                .filter(reservation -> !reservation.isCancelled())
                .collect(Collectors.toList());
                
            int seatsReserved = 0;
            for (Reservation reservation : reservations) {
                seatsReserved += reservation.getSeatIds().size();
            }
            
            // Update theatre occupancy
            occupancyByTheatre.put(theatre, 
                occupancyByTheatre.getOrDefault(theatre, 0) + seatsReserved);
        }
        
        return occupancyByTheatre;
    }
    
    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        return dto;
    }
}
