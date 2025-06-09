package com.moviereservation.dto;

import java.time.LocalDateTime;

public class ShowtimeDto {
    private Long id;
    private Long movieId;
    private String movieTitle;
    private LocalDateTime startTime;
    private int totalSeats;
    private int availableSeats;
    private String theatreHall;
    
    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMovieId() {
        return movieId;
    }

    public void setMovieId(Long movieId) {
        this.movieId = movieId;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public String getTheatreHall() {
        return theatreHall;
    }

    public void setTheatreHall(String theatreHall) {
        this.theatreHall = theatreHall;
    }
}
