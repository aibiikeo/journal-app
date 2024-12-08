package com.example.journalapp.service;

import com.example.journalapp.dto.JournalEntryDto;
import com.example.journalapp.entity.JournalEntry;
import com.example.journalapp.entity.User;
import com.example.journalapp.exception.ApiException;
import com.example.journalapp.mapper.JournalEntryMapper;
import com.example.journalapp.repository.ImageRepository;
import com.example.journalapp.repository.JournalEntryRepository;
import com.example.journalapp.repository.UserRepository;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Data
@Slf4j
public class JournalEntryService {
    @Autowired
    private JournalEntryRepository journalEntryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JournalEntryMapper journalEntryMapper;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private ImageService imageService;

    public List<JournalEntry> getAllJournalEntriesByUser(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            return journalEntryRepository.findByUserId(userId);
        }
        throw new ApiException("User with ID " + userId + " not found.", HttpStatusCode.valueOf(404));
    }

    public JournalEntry getJournalEntryByUserAndId(Long userId, Long id) {
        Optional<User> user = userRepository.findById(userId);
        if (!user.isPresent()) {
            throw new ApiException("User with ID " + userId + " not found.", HttpStatusCode.valueOf(404));
        }
        return journalEntryRepository.findByUserIdAndId(userId, id)
                .orElseThrow(() -> new ApiException("Journal entry not found for user " + userId + " with id " + id, HttpStatusCode.valueOf(404)));
    }

    public JournalEntry createJournalEntryByUser(Long userId, JournalEntryDto journalEntryDto, List<MultipartFile> images) throws IOException {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new ApiException("User with id " + userId + " not found.", HttpStatusCode.valueOf(404));
        }

        // Map DTO to Entity
        JournalEntry journalEntry = journalEntryMapper.journalEntryDtoToEntity(journalEntryDto);
        journalEntry.setUser(user.get());  // Set the user on the journal entry

        // Set the entry date if not provided in DTO
        if (journalEntry.getEntryDate() == null) {
            journalEntry.setEntryDate(LocalDate.now());
        }

        if (images != null && !images.isEmpty()) {
            journalEntry.setImages(imageService.uploadImages(images));
            log.info("images uploaded");
        }

        // Return journal entry entity
        return journalEntryRepository.save(journalEntry);
    }

//    @Transactional
    public JournalEntry updateJournalEntryByUser(Long userId, Long id, JournalEntryDto journalEntryDto, List<MultipartFile> images) throws IOException {
        JournalEntry journalEntry = journalEntryRepository.findByUserIdAndId(userId, id)
                .orElseThrow(() -> new ApiException("Journal entry not found for userId: " + userId + " and entry id: " + id, HttpStatus.NOT_FOUND));

        // Update fields selectively
        if (journalEntryDto.getTitle() != null) {
            journalEntry.setTitle(journalEntryDto.getTitle());
        }

        if (journalEntryDto.getContent() != null) {
            journalEntry.setContent(journalEntryDto.getContent());
        }

        if (journalEntryDto.getEntryDate() != null) {
            journalEntry.setEntryDate(journalEntryDto.getEntryDate());
        }

        if (journalEntry.getImages() != null && !journalEntry.getImages().isEmpty()) {
//            imageService.deleteImages(journalEntry.getImages());
            journalEntry.setImages(imageService.uploadImages(images));
        }
        
        return journalEntryRepository.save(journalEntry);
    }


    public boolean deleteJournalEntryByUser(Long userId, Long id) {
        // Find the journal entry by userId and id
        Optional<JournalEntry> journalEntryOptional = journalEntryRepository.findByUserIdAndId(userId, id);

        if (journalEntryOptional.isPresent()) {
            // Delete the journal entry if found
            journalEntryRepository.delete(journalEntryOptional.get());
            return true;
        }

        // Return false if the journal entry is not found
        return false;
    }

    public String getEmailByUserId(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new ApiException("User not found", HttpStatusCode.valueOf(404)))
                .getEmail();
    }
}
