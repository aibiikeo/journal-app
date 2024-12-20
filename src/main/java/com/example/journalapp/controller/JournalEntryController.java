package com.example.journalapp.controller;

import com.example.journalapp.dto.JournalEntryDto;
import com.example.journalapp.entity.Image;
import com.example.journalapp.entity.JournalEntry;
import com.example.journalapp.exception.ApiException;
import com.example.journalapp.repository.ImageRepository;
import com.example.journalapp.security.JwtTokenProvider;
import com.example.journalapp.service.ImageService;
import com.example.journalapp.service.JournalEntryService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/journal-entries")
@Data
@Slf4j
public class JournalEntryController {
    @Autowired
    private JournalEntryService journalEntryService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @GetMapping("/{userId}")
    public ResponseEntity<List<JournalEntry>> getAllJournalEntriesByUser(
            @PathVariable Long userId,
            @RequestHeader("Authorization") String token) {
        if (!isAuthorized(token, userId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN); // Return 403 if unauthorized
        }
        List<JournalEntry> journalEntries = journalEntryService.getAllJournalEntriesByUser(userId);
        return new ResponseEntity<>(journalEntries, HttpStatus.OK);
    }

    @GetMapping("/{userId}/{id}")
    public ResponseEntity<JournalEntry> getJournalEntryByUserById(
            @PathVariable Long userId,
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        if (!isAuthorized(token, userId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN); // Return 403 if unauthorized
        }
        JournalEntry journalEntry = journalEntryService.getJournalEntryByUserAndId(userId, id);
        if (journalEntry != null) {
            return new ResponseEntity<>(journalEntry, HttpStatus.OK);
        }
        return new ResponseEntity<>(new ApiException("journal entry with id " + id + " not found", HttpStatusCode.valueOf(404)).getStatusCode());  // Handle entry not found
    }

    @PostMapping(value = "/{userId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<JournalEntry> createJournalEntryByUser(
            @PathVariable Long userId,
            @RequestHeader("Authorization") String token,
            @RequestPart JournalEntryDto journalEntryDto,
            @RequestParam(required = false) List<MultipartFile> images) throws IOException {
        if (!isAuthorized(token, userId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN); // Return 403 if unauthorized
        }
        JournalEntry createdEntry = journalEntryService.createJournalEntryByUser(userId, journalEntryDto, images);
        if (createdEntry != null) {
            return new ResponseEntity<>(createdEntry, HttpStatus.CREATED); // Return 201 (Created)
        }
        return new ResponseEntity<>(new ApiException("error while creating journal entry", HttpStatusCode.valueOf(404)).getStatusCode()); // Handle errors
    }

    @PutMapping("/{userId}/{id}")
    public ResponseEntity<JournalEntry> updateJournalEntryByUser(
            @PathVariable Long userId,
            @PathVariable Long id,
            @RequestHeader("Authorization") String token,
            @RequestPart JournalEntryDto journalEntryDto,
            @RequestParam(required = false) List<MultipartFile> images) throws IOException {
        if (!isAuthorized(token, userId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN); // Return 403 if unauthorized
        }
        JournalEntry updatedEntry = journalEntryService.updateJournalEntryByUser(userId, id, journalEntryDto, images);
        if (updatedEntry != null) {
            return new ResponseEntity<>(updatedEntry, HttpStatus.OK); // Return 200 (OK)
        }
        return new ResponseEntity<>(new ApiException("error while updating journal entry", HttpStatusCode.valueOf(404)).getStatusCode()); // Handle entry or user not found
    }

    @DeleteMapping("/{userId}/{id}")
    public ResponseEntity<Void> deleteJournalEntry(
            @PathVariable Long userId,
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        if (!isAuthorized(token, userId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN); // Return 403 if unauthorized
        }
        boolean isDeleted = journalEntryService.deleteJournalEntryByUser(userId, id);
        if (isDeleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Return 204 (No Content)
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Handle entry or user not found
    }

    @DeleteMapping("/{userId}/{id}/{imageId}")
    public ResponseEntity<Void> deleteImageFromJournalEntry(
            @PathVariable Long userId,
            @PathVariable Long id,
            @PathVariable Long imageId,
            @RequestHeader("Authorization") String token){
        if (!isAuthorized(token, userId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN); // Return 403 if unauthorized
        }
        boolean isDeleted = journalEntryService.deleteImageFromJournalEntry(userId, id, imageId);
        if (isDeleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Return 204 if the image was successfully deleted
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Return 404 if the journal entry or image was not found
    }

    private boolean isAuthorized(String token, Long userId) {
        String jwtToken = token.replace("Bearer ", "");
        if (!jwtTokenProvider.validateToken(jwtToken)) {
            return false; // Invalid token
        }
        String emailFromToken = jwtTokenProvider.getEmailFromToken(jwtToken);
        String emailFromDb = journalEntryService.getEmailByUserId(userId);
        return emailFromToken.equals(emailFromDb); // Check if email matches
    }

    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private ImageService imageService;
    @PostMapping(value = "/images", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public List<String> uploadImage(@RequestParam List<MultipartFile> images) throws IOException {
        List<Image> uploadedImages = imageService.uploadImages(images);
        return uploadedImages.stream().map(Image::getName).collect(Collectors.toList());
    }
    @GetMapping("/images/{fileName}")
    public ResponseEntity<?> downloadImage(@PathVariable String fileName, @RequestParam(required = false) Integer width, @RequestParam(required = false) Integer height) {
        try {
            byte[] originalImageData = imageService.downloadImage(fileName);

            // If resizing is requested
            if (width != null && height != null) {
                // Convert byte array to BufferedImage
                InputStream in = new ByteArrayInputStream(originalImageData);
                BufferedImage originalImage = ImageIO.read(in);

                // Resize the image
                BufferedImage resizedImage = Thumbnails.of(originalImage)
                        .size(width, height)
                        .asBufferedImage();

                // Convert resized BufferedImage to byte array
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                ImageIO.write(resizedImage, "png", out);
                byte[] resizedImageData = out.toByteArray();

                return ResponseEntity.status(HttpStatus.OK)
                        .contentType(MediaType.valueOf("image/png"))
                        .body(resizedImageData);
            }

            // Return original image if no resizing parameters are provided
            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.valueOf("image/png"))
                    .body(originalImageData);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing the image.");
        }
    }

}
