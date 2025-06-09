package com.moviereservation.dto;

public class ReportDto {
    private Long movieId;
    private String movieTitle;
    private int totalReservations;
    private int totalSeatsReserved;
    private double totalRevenue;
    private double occupancyRate; // percentage
    
    // Getters and Setters
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

    public int getTotalReservations() {
        return totalReservations;
    }

    public void setTotalReservations(int totalReservations) {
        this.totalReservations = totalReservations;
    }

    public int getTotalSeatsReserved() {
        return totalSeatsReserved;
    }

    public void setTotalSeatsReserved(int totalSeatsReserved) {
        this.totalSeatsReserved = totalSeatsReserved;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public double getOccupancyRate() {
        return occupancyRate;
    }

    public void setOccupancyRate(double occupancyRate) {
        this.occupancyRate = occupancyRate;
    }
}
