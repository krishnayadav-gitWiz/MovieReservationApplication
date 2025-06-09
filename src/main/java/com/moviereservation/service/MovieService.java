package com.moviereservation.service;

import com.moviereservation.dto.MovieDto;
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
import java.util.stream.Collectors;

@Service
public class MovieService {
    
    @Autowired
    private MovieRepository movieRepository;
    
    @Autowired
    private ShowtimeRepository showtimeRepository;
    
    @Autowired
    private SeatRepository seatRepository;
    
    public List<MovieDto> getAllMovies() {
        return movieRepository.findAll().stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }
    
    public MovieDto getMovieById(Long id) {
        Movie movie = movieRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Movie not found"));
            
        return convertToDto(movie);
    }
    
    public List<MovieDto> getMoviesByGenre(String genre) {
        return movieRepository.findByGenre(genre).stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }
    
    public MovieDto addMovie(Movie movie) {
        return convertToDto(movieRepository.save(movie));
    }
    
    public MovieDto updateMovie(Long id, Movie movieDetails) {
        Movie movie = movieRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Movie not found"));
            
        movie.setTitle(movieDetails.getTitle());
        movie.setDescription(movieDetails.getDescription());
        movie.setPosterUrl(movieDetails.getPosterUrl());
        movie.setGenres(movieDetails.getGenres());
        movie.setPrice(movieDetails.getPrice());
        movie.setDurationMinutes(movieDetails.getDurationMinutes());
        
        return convertToDto(movieRepository.save(movie));
    }
    
    public void deleteMovie(Long id) {
        // First, check if movie exists
        if (!movieRepository.findById(id).isPresent()) {
            throw new IllegalArgumentException("Movie not found");
        }
        
        // Delete all showtimes for this movie
        List<Showtime> showtimes = showtimeRepository.findByMovieId(id);
        for (Showtime showtime : showtimes) {
            // Delete all seats for this showtime
            List<Seat> seats = seatRepository.findByShowtimeId(showtime.getId());
            for (Seat seat : seats) {
                seatRepository.deleteById(seat.getId());
            }
            showtimeRepository.deleteById(showtime.getId());
        }
        
        // Delete the movie
        movieRepository.deleteById(id);
    }
    
    public List<MovieDto> getMoviesShowingOnDate(LocalDate date) {
        List<Showtime> showtimesOnDate = showtimeRepository.findByDate(date);
        List<Long> movieIds = showtimesOnDate.stream()
            .map(Showtime::getMovieId)
            .distinct()
            .collect(Collectors.toList());
            
        List<MovieDto> movies = new ArrayList<>();
        for (Long movieId : movieIds) {
            movieRepository.findById(movieId).ifPresent(movie -> {
                movies.add(convertToDto(movie));
            });
        }
        
        return movies;
    }
    
    private MovieDto convertToDto(Movie movie) {
        MovieDto dto = new MovieDto();
        dto.setId(movie.getId());
        dto.setTitle(movie.getTitle());
        dto.setDescription(movie.getDescription());
        dto.setPosterUrl(movie.getPosterUrl());
        dto.setGenres(movie.getGenres());
        dto.setPrice(movie.getPrice());
        dto.setDurationMinutes(movie.getDurationMinutes());
        return dto;
    }
}
