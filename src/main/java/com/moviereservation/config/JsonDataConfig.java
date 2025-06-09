package com.moviereservation.config;

import com.moviereservation.model.*;
import com.moviereservation.util.JsonFileUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class JsonDataConfig {

    @Bean
    public JsonFileUtil<User> userJsonFileUtil() {
        return new JsonFileUtil<>("users.json", User.class);
    }

    @Bean
    public JsonFileUtil<Movie> movieJsonFileUtil() {
        return new JsonFileUtil<>("movies.json", Movie.class);
    }

    @Bean
    public JsonFileUtil<Showtime> showtimeJsonFileUtil() {
        return new JsonFileUtil<>("showtimes.json", Showtime.class);
    }

    @Bean
    public JsonFileUtil<Seat> seatJsonFileUtil() {
        return new JsonFileUtil<>("seats.json", Seat.class);
    }

    @Bean
    public JsonFileUtil<Reservation> reservationJsonFileUtil() {
        return new JsonFileUtil<>("reservations.json", Reservation.class);
    }

    @Bean
    public void initializeData() {
        // Initialize with admin user if no users exist
        JsonFileUtil<User> userUtil = userJsonFileUtil();
        List<User> users = userUtil.readFromFile();
        if (users.isEmpty()) {
            User admin = new User();
            admin.setId(1L);
            admin.setUsername("admin");
            admin.setPassword("$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG"); // password: admin
            admin.setEmail("admin@example.com");
            admin.setRole("ADMIN");
            
            users = new ArrayList<>();
            users.add(admin);
            userUtil.writeToFile(users);
        }
    }
}
