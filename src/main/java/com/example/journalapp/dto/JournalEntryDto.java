package com.example.journalapp.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JournalEntryDto {
    Long userId;
    String title;
    String content;
    LocalDate entryDate;
//    String photoUrl;
//    Long userId;
}
