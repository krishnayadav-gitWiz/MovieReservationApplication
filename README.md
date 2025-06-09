https://roadmap.sh/projects/movie-reservation-system

# Movie Reservation System

A Spring Boot application for movie ticket reservations. Users can browse movies, reserve seats, and manage their reservations. Admins can manage movies, showtimes, and view reports.

## Features

- User Authentication and Authorization (Admin/User roles)
- Movie and Showtime Management
- Seat Reservation System
- Reservation Management
- Admin Reporting

## Setup and Installation

1. Clone the repository
2. Run `mvn clean install` to build the application
3. Run `mvn spring-boot:run` to start the application
4. Access the application at http://localhost:8080

## Default Admin Credentials
- Username: admin
- Password: admin

## API Endpoints

### Authentication
- POST /api/auth/register - Register a new user
- POST /api/auth/login - Login and get JWT token

### Movies (Public)
- GET /api/movies/public/all - Get all movies
- GET /api/movies/public/{id} - Get movie by ID
- GET /api/movies/public/genre/{genre} - Get movies by genre
- GET /api/movies/public/date/{date} - Get movies showing on a specific date

### Movies (Authenticated)
- GET /api/movies/all - Get all movies
- GET /api/movies/{id} - Get movie by ID
- GET /api/movies/genre/{genre} - Get movies by genre
- GET /api/movies/date/{date} - Get movies showing on a specific date
- GET /api/movies/{movieId}/showtimes - Get showtimes for a movie
- GET /api/movies/showtimes/{showtimeId}/seats - Get all seats for a showtime
- GET /api/movies/showtimes/{showtimeId}/available-seats - Get available seats for a showtime

### Movies (Admin)
- POST /api/movies - Add a new movie
- PUT /api/movies/{id} - Update a movie
- DELETE /api/movies/{id} - Delete a movie
- POST /api/movies/showtimes - Add a new showtime
- PUT /api/movies/showtimes/{id} - Update a showtime
- DELETE /api/movies/showtimes/{id} - Delete a showtime

### Reservations
- GET /api/reservations/my - Get current user's reservations
- GET /api/reservations/my/upcoming - Get current user's upcoming reservations
- POST /api/reservations - Create a new reservation
- POST /api/reservations/{id}/cancel - Cancel a reservation

### Reservations (Admin)
- GET /api/reservations/all - Get all reservations
- GET /api/reservations/{id} - Get reservation by ID
- GET /api/reservations/user/{userId} - Get reservations by user ID

### Admin
- POST /api/admin/users/{userId}/promote - Promote user to admin
- GET /api/admin/reports/revenue - Generate revenue reports
- GET /api/admin/reports/occupancy-by-theatre - Generate occupancy by theatre reports

## Data Storage

This application uses JSON files for data storage:
- users.json - User accounts
- movies.json - Movie information
- showtimes.json - Showtime information
- seats.json - Seat information
- reservations.json - Reservation information

## Technologies
- Java 17
- Spring Boot
- Spring Security
- JWT Authentication