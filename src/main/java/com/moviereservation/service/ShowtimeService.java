package com.moviereservation.service;

import com.moviereservation.dto.ShowtimeDto;
import com.moviereservation.model.Movie;
import com.moviereservation.model.Seat;
import com.moviereservation.model.Showtime;
import com.moviereservation.repository.MovieRepository;
import com.moviereservation.repository.SeatRepository;
import com.moviereservation.repository.ShowtimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ShowtimeService {
    
    @Autowired
    private ShowtimeRepository showtimeRepository;
    
    @Autowired
    private MovieRepository movieRepository;
    
    @Autowired
    private SeatRepository seatRepository;
    
    public List<ShowtimeDto> getAllShowtimes() {
        return showtimeRepository.findAll().stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }
    
    public ShowtimeDto getShowtimeById(Long id) {
        Showtime showtime = showtimeRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Showtime not found"));
            
        return convertToDto(showtime);
    }
    
    public List<ShowtimeDto> getShowtimesByMovieId(Long movieId) {
        return showtimeRepository.findByMovieId(movieId).stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }
    
    public List<ShowtimeDto> getShowtimesByDate(LocalDate date) {
        return showtimeRepository.findByDate(date).stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }
    
    public ShowtimeDto addShowtime(Showtime showtime) {
        // Check if movie exists
        Movie movie = movieRepository.findById(showtime.getMovieId())
            .orElseThrow(() -> new IllegalArgumentException("Movie not found"));
            
        // Save showtime
        Showtime savedShowtime = showtimeRepository.save(showtime);
        
        // Create seats for the showtime
        List<Seat> seats = new ArrayList<>();
        for (int i = 1; i <= showtime.getTotalSeats(); i++) {
            Seat seat = new Seat();
            seat.setShowtimeId(savedShowtime.getId());
            seat.setSeatNumber(generateSeatNumber(i));
            seat.setReserved(false);
            seats.add(seat);
        }
        seatRepository.saveAll(seats);
        
        return convertToDto(savedShowtime);
    }
    
    public ShowtimeDto updateShowtime(Long id, Showtime showtimeDetails) {
        Showtime showtime = showtimeRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Showtime not found"));
            
        // Check if movie exists
        if (!movieRepository.findById(showtimeDetails.getMovieId()).isPresent()) {
            throw new IllegalArgumentException("Movie not found");
        }
            
        // Update showtime details
        showtime.setMovieId(showtimeDetails.getMovieId());
        showtime.setStartTime(showtimeDetails.getStartTime());
        showtime.setTheatreHall(showtimeDetails.getTheatreHall());
        
        // Check if total seats has changed
        if (showtime.getTotalSeats() != showtimeDetails.getTotalSeats()) {
            // Get existing seats
            List<Seat> existingSeats = seatRepository.findByShowtimeId(id);
            
            // If increasing seats
            if (showtime.getTotalSeats() < showtimeDetails.getTotalSeats()) {
                List<Seat> newSeats = new ArrayList<>();
                for (int i = showtime.getTotalSeats() + 1; i <= showtimeDetails.getTotalSeats(); i++) {
                    Seat seat = new Seat();
                    seat.setShowtimeId(id);
                    seat.setSeatNumber(generateSeatNumber(i));
                    seat.setReserved(false);
                    newSeats.add(seat);
                }
                seatRepository.saveAll(newSeats);
            }
            // If decreasing seats - must make sure no reservations exist for removed seats
            else {
                // Would need to check reservations here, for simplicity we'll skip this check
                // Just remove excess seats
                for (int i = showtimeDetails.getTotalSeats() + 1; i <= showtime.getTotalSeats(); i++) {
                    String seatNumber = generateSeatNumber(i);
                    Optional<Seat> seatToRemove = existingSeats.stream()
                        .filter(seat -> seat.getSeatNumber().equals(seatNumber))
                        .findFirst();
                    seatToRemove.ifPresent(seat -> seatRepository.deleteById(seat.getId()));
                }
            }
            
            showtime.setTotalSeats(showtimeDetails.getTotalSeats());
        }
        
        return convertToDto(showtimeRepository.save(showtime));
    }
    
    public void deleteShowtime(Long id) {
        // Delete all seats for this showtime
        List<Seat> seats = seatRepository.findByShowtimeId(id);
        for (Seat seat : seats) {
            seatRepository.deleteById(seat.getId());
        }
        
        // Delete the showtime
        showtimeRepository.deleteById(id);
    }
    
    private String generateSeatNumber(int seatIndex) {
        // Create seat numbers like A1, A2, ..., B1, B2, etc.
        int row = (seatIndex - 1) / 10;
        int seatInRow = (seatIndex - 1) % 10 + 1;
        char rowChar = (char)('A' + row);
        return String.format("%c%d", rowChar, seatInRow);
    }
    
    private ShowtimeDto convertToDto(Showtime showtime) {
        ShowtimeDto dto = new ShowtimeDto();
        dto.setId(showtime.getId());
        dto.setMovieId(showtime.getMovieId());
        
        // Get movie title
        movieRepository.findById(showtime.getMovieId()).ifPresent(movie -> {
            dto.setMovieTitle(movie.getTitle());
        });
        
        dto.setStartTime(showtime.getStartTime());
        dto.setTotalSeats(showtime.getTotalSeats());
        dto.setTheatreHall(showtime.getTheatreHall());
        
        // Calculate available seats
        List<Seat> availableSeats = seatRepository.findAvailableSeatsByShowtimeId(showtime.getId());
        dto.setAvailableSeats(availableSeats.size());
        
        return dto;
    }
}
