package com.example.journalapp.service;

import com.example.journalapp.dto.JournalEntryDto;
import com.example.journalapp.entity.JournalEntry;
import com.example.journalapp.entity.User;
import com.example.journalapp.exception.ApiException;
import com.example.journalapp.mapper.JournalEntryMapper;
import com.example.journalapp.repository.JournalEntryRepository;
import com.example.journalapp.repository.UserRepository;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;

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

    public JournalEntry createJournalEntryByUser(Long userId, JournalEntryDto journalEntryDto) {
        Optional<User> user = userRepository.findById(userId);

        if (user.isPresent()) {
            // Map DTO to Entity
            JournalEntry journalEntry = journalEntryMapper.journalEntryDtoToEntity(journalEntryDto);
            journalEntry.setUser(user.get());  // Set the user on the journal entry

            // Set the entry date if not provided in DTO
            if (journalEntry.getEntryDate() == null) {
                journalEntry.setEntryDate(LocalDate.now());
            }

            // Save the journal entry in the repository
            JournalEntry savedEntry = journalEntryRepository.save(journalEntry);

            // Return the saved journal entry entity
            return savedEntry;
        }

        // Return null if user is not found
        return null;
    }

    public JournalEntry updateJournalEntryByUser(Long userId, Long id, JournalEntryDto journalEntryDto) {
        // Find the journal entry by userId and id
        Optional<JournalEntry> journalEntryOptional = journalEntryRepository.findByUserIdAndId(userId, id);

        if (journalEntryOptional.isPresent()) {
            // Get the existing journal entry
            JournalEntry journalEntry = journalEntryOptional.get();

            // Only update the fields that are not null in the DTO
            if (journalEntryDto.getTitle() != null) {
                journalEntry.setTitle(journalEntryDto.getTitle());
            }

            if (journalEntryDto.getContent() != null) {
                journalEntry.setContent(journalEntryDto.getContent());
            }

            if (journalEntryDto.getEntryDate() != null) {
                journalEntry.setEntryDate(journalEntryDto.getEntryDate());
            }

            // Save the updated journal entry
            return journalEntryRepository.save(journalEntry);
        }

        // Return null if the journal entry is not found
        return null;
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
