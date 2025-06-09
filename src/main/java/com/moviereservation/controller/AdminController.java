package com.moviereservation.controller;

import com.moviereservation.dto.ReportDto;
import com.moviereservation.dto.UserDto;
import com.moviereservation.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    
    @Autowired
    private AdminService adminService;
    
    @PostMapping("/users/{userId}/promote")
    public ResponseEntity<?> promoteUserToAdmin(@PathVariable Long userId) {
        try {
            UserDto promotedUser = adminService.promoteToAdmin(userId);
            return ResponseEntity.ok(promotedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/reports/revenue")
    public List<ReportDto> generateRevenueReports() {
        return adminService.generateRevenueReports();
    }
    
    @GetMapping("/reports/occupancy-by-theatre")
    public Map<String, Integer> generateOccupancyByTheatre() {
        return adminService.generateOccupancyByTheatre();
    }
}
