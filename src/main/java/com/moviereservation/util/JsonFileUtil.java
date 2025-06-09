package com.moviereservation.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

// Removed @Component to prevent Spring from creating a generic bean automatically
public class JsonFileUtil<T> {
    private final String filePath;
    private final Class<T> type;
    private final ObjectMapper objectMapper;
    
    public JsonFileUtil(String fileName, Class<T> type) {
        this.filePath = "data/" + fileName;
        this.type = type;
        
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        
        // Ensure the data directory exists
        try {
            Path dataDir = Paths.get("src/main/resources/data");
            if (!Files.exists(dataDir)) {
                Files.createDirectories(dataDir);
            }
            
            // Create the file if it doesn't exist
            Path file = Paths.get("src/main/resources/" + filePath);
            if (!Files.exists(file)) {
                Files.createFile(file);
                writeToFile(new ArrayList<>());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize data directory or file", e);
        }
    }
    
    public List<T> readFromFile() {
        try {
            Resource resource = new ClassPathResource(filePath);
            File file = resource.getFile();
            System.out.println("Reading file: " + file.getAbsolutePath());
            if (file.length() == 0) {
                return new ArrayList<>();
            }
            
            CollectionType listType = objectMapper.getTypeFactory()
                    .constructCollectionType(List.class, type);
            return objectMapper.readValue(file, listType);
        } catch (IOException e) {
            // If file doesn't exist or is empty, return empty list
            System.err.println("Error reading file: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    public void writeToFile(List<T> data) {
        try {
            Resource resource = new ClassPathResource(filePath);
            File file = resource.getFile();
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, data);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write to file: " + filePath, e);
        }
    }
}
