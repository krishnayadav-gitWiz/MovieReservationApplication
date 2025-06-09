package com.moviereservation.repository;

import com.moviereservation.model.Seat;
import com.moviereservation.util.JsonFileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class SeatRepository {
    
    @Autowired
    private JsonFileUtil<Seat> seatJsonFileUtil;
    
    public List<Seat> findAll() {
        return seatJsonFileUtil.readFromFile();
    }
    
    public Optional<Seat> findById(Long id) {
        return findAll().stream()
                .filter(seat -> seat.getId().equals(id))
                .findFirst();
    }
    
    public List<Seat> findByShowtimeId(Long showtimeId) {
        return findAll().stream()
                .filter(seat -> seat.getShowtimeId().equals(showtimeId))
                .collect(Collectors.toList());
    }
    
    public List<Seat> findAvailableSeatsByShowtimeId(Long showtimeId) {
        return findAll().stream()
                .filter(seat -> seat.getShowtimeId().equals(showtimeId) && !seat.isReserved())
                .collect(Collectors.toList());
    }
    
    public Seat save(Seat seat) {
        List<Seat> seats = findAll();
        
        // If new seat, generate ID
        if (seat.getId() == null) {
            Long maxId = seats.stream()
                    .mapToLong(Seat::getId)
                    .max()
                    .orElse(0L);
            seat.setId(maxId + 1);
            seats.add(seat);
        } else {
            // Update existing seat
            seats = seats.stream()
                    .map(existingSeat -> existingSeat.getId().equals(seat.getId()) ? seat : existingSeat)
                    .collect(Collectors.toList());
        }
        
        seatJsonFileUtil.writeToFile(seats);
        return seat;
    }
    
    public List<Seat> saveAll(List<Seat> seatsToSave) {
        List<Seat> allSeats = findAll();
        
        for (Seat seat : seatsToSave) {
            // If new seat, generate ID
            if (seat.getId() == null) {
                Long maxId = allSeats.stream()
                        .mapToLong(Seat::getId)
                        .max()
                        .orElse(0L);
                seat.setId(maxId + 1);
                allSeats.add(seat);
            } else {
                // Update existing seat
                allSeats = allSeats.stream()
                        .map(existingSeat -> existingSeat.getId().equals(seat.getId()) ? seat : existingSeat)
                        .collect(Collectors.toList());
            }
        }
        
        seatJsonFileUtil.writeToFile(allSeats);
        return seatsToSave;
    }
    
    public void deleteById(Long id) {
        List<Seat> seats = findAll();
        seats = seats.stream()
                .filter(seat -> !seat.getId().equals(id))
                .collect(Collectors.toList());
        seatJsonFileUtil.writeToFile(seats);
    }
}
