package com.example.journalapp.repository;

import com.example.journalapp.entity.JournalEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JournalEntryRepository extends JpaRepository<JournalEntry, Long> {
    List<JournalEntry> findByUserId(Long userId);
    Optional<JournalEntry> findByUserIdAndId(Long userId, Long id);
}
