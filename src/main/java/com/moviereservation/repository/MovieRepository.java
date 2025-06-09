package com.moviereservation.repository;

import com.moviereservation.model.Movie;
import com.moviereservation.util.JsonFileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class MovieRepository {
    
    @Autowired
    private JsonFileUtil<Movie> movieJsonFileUtil;
    
    public List<Movie> findAll() {
        return movieJsonFileUtil.readFromFile();
    }
    
    public Optional<Movie> findById(Long id) {
        return findAll().stream()
                .filter(movie -> movie.getId().equals(id))
                .findFirst();
    }
    
    public List<Movie> findByGenre(String genre) {
        return findAll().stream()
                .filter(movie -> movie.getGenres().contains(genre))
                .collect(Collectors.toList());
    }
    
    public Movie save(Movie movie) {
        List<Movie> movies = findAll();
        
        // If new movie, generate ID
        if (movie.getId() == null) {
            Long maxId = movies.stream()
                    .mapToLong(Movie::getId)
                    .max()
                    .orElse(0L);
            movie.setId(maxId + 1);
            movies.add(movie);
        } else {
            // Update existing movie
            movies = movies.stream()
                    .map(existingMovie -> existingMovie.getId().equals(movie.getId()) ? movie : existingMovie)
                    .collect(Collectors.toList());
        }
        
        movieJsonFileUtil.writeToFile(movies);
        return movie;
    }
    
    public void deleteById(Long id) {
        List<Movie> movies = findAll();
        movies = movies.stream()
                .filter(movie -> !movie.getId().equals(id))
                .collect(Collectors.toList());
        movieJsonFileUtil.writeToFile(movies);
    }
}
