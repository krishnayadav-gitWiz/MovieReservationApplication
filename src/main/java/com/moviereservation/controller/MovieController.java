package com.moviereservation.controller;

import com.moviereservation.dto.MovieDto;
import com.moviereservation.dto.ShowtimeDto;
import com.moviereservation.model.Movie;
import com.moviereservation.model.Seat;
import com.moviereservation.model.Showtime;
import com.moviereservation.repository.SeatRepository;
import com.moviereservation.service.MovieService;
import com.moviereservation.service.ShowtimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/movies")
public class MovieController {
    
    @Autowired
    private MovieService movieService;
    
    @Autowired
    private ShowtimeService showtimeService;
    
    @Autowired
    private SeatRepository seatRepository;
    
    // Public endpoints that don't require authentication
    
    @GetMapping("/public/all")
    public List<MovieDto> getAllMoviesPublic() {
        return movieService.getAllMovies();
    }
    
    @GetMapping("/public/{id}")
    public ResponseEntity<?> getMovieByIdPublic(@PathVariable Long id) {
        try {
            MovieDto movie = movieService.getMovieById(id);
            return ResponseEntity.ok(movie);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/public/genre/{genre}")
    public List<MovieDto> getMoviesByGenrePublic(@PathVariable String genre) {
        return movieService.getMoviesByGenre(genre);
    }
    
    @GetMapping("/public/date/{date}")
    public List<MovieDto> getMoviesShowingOnDatePublic(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return movieService.getMoviesShowingOnDate(date);
    }
    
    // Authenticated endpoints
    
    @GetMapping("/all")
    public List<MovieDto> getAllMovies() {
        return movieService.getAllMovies();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getMovieById(@PathVariable Long id) {
        try {
            MovieDto movie = movieService.getMovieById(id);
            return ResponseEntity.ok(movie);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/genre/{genre}")
    public List<MovieDto> getMoviesByGenre(@PathVariable String genre) {
        return movieService.getMoviesByGenre(genre);
    }
    
    @GetMapping("/date/{date}")
    public List<MovieDto> getMoviesShowingOnDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return movieService.getMoviesShowingOnDate(date);
    }
    
    @GetMapping("/{movieId}/showtimes")
    public List<ShowtimeDto> getShowtimesByMovieId(@PathVariable Long movieId) {
        return showtimeService.getShowtimesByMovieId(movieId);
    }
    
    @GetMapping("/showtimes/{showtimeId}/seats")
    public List<Seat> getSeatsByShowtimeId(@PathVariable Long showtimeId) {
        return seatRepository.findByShowtimeId(showtimeId);
    }
    
    @GetMapping("/showtimes/{showtimeId}/available-seats")
    public List<Seat> getAvailableSeatsByShowtimeId(@PathVariable Long showtimeId) {
        return seatRepository.findAvailableSeatsByShowtimeId(showtimeId);
    }
    
    // Admin endpoints
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addMovie(@RequestBody Movie movie) {
        try {
            MovieDto addedMovie = movieService.addMovie(movie);
            return ResponseEntity.ok(addedMovie);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateMovie(@PathVariable Long id, @RequestBody Movie movie) {
        try {
            MovieDto updatedMovie = movieService.updateMovie(id, movie);
            return ResponseEntity.ok(updatedMovie);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteMovie(@PathVariable Long id) {
        try {
            movieService.deleteMovie(id);
            return ResponseEntity.ok(Map.of("message", "Movie deleted successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/showtimes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addShowtime(@RequestBody Showtime showtime) {
        try {
            ShowtimeDto addedShowtime = showtimeService.addShowtime(showtime);
            return ResponseEntity.ok(addedShowtime);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/showtimes/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateShowtime(@PathVariable Long id, @RequestBody Showtime showtime) {
        try {
            ShowtimeDto updatedShowtime = showtimeService.updateShowtime(id, showtime);
            return ResponseEntity.ok(updatedShowtime);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @DeleteMapping("/showtimes/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteShowtime(@PathVariable Long id) {
        try {
            showtimeService.deleteShowtime(id);
            return ResponseEntity.ok(Map.of("message", "Showtime deleted successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
