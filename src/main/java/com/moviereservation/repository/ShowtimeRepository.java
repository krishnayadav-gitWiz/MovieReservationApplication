package com.moviereservation.repository;

import com.moviereservation.model.Showtime;
import com.moviereservation.util.JsonFileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ShowtimeRepository {
    
    @Autowired
    private JsonFileUtil<Showtime> showtimeJsonFileUtil;
    
    public List<Showtime> findAll() {
        return showtimeJsonFileUtil.readFromFile();
    }
    
    public Optional<Showtime> findById(Long id) {
        return findAll().stream()
                .filter(showtime -> showtime.getId().equals(id))
                .findFirst();
    }
    
    public List<Showtime> findByMovieId(Long movieId) {
        return findAll().stream()
                .filter(showtime -> showtime.getMovieId().equals(movieId))
                .collect(Collectors.toList());
    }
    
    public List<Showtime> findByDate(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay().minusNanos(1);
        
        return findAll().stream()
                .filter(showtime -> !showtime.getStartTime().isBefore(startOfDay) && 
                                    !showtime.getStartTime().isAfter(endOfDay))
                .collect(Collectors.toList());
    }
    
    public Showtime save(Showtime showtime) {
        List<Showtime> showtimes = findAll();
        
        // If new showtime, generate ID
        if (showtime.getId() == null) {
            Long maxId = showtimes.stream()
                    .mapToLong(Showtime::getId)
                    .max()
                    .orElse(0L);
            showtime.setId(maxId + 1);
            showtimes.add(showtime);
        } else {
            // Update existing showtime
            showtimes = showtimes.stream()
                    .map(existingShowtime -> existingShowtime.getId().equals(showtime.getId()) ? showtime : existingShowtime)
                    .collect(Collectors.toList());
        }
        
        showtimeJsonFileUtil.writeToFile(showtimes);
        return showtime;
    }
    
    public void deleteById(Long id) {
        List<Showtime> showtimes = findAll();
        showtimes = showtimes.stream()
                .filter(showtime -> !showtime.getId().equals(id))
                .collect(Collectors.toList());
        showtimeJsonFileUtil.writeToFile(showtimes);
    }
}
