package com.example.journalapp.mapper;

import com.example.journalapp.dto.JournalEntryDto;
import com.example.journalapp.entity.JournalEntry;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface JournalEntryMapper {
    JournalEntry journalEntryDtoToEntity(JournalEntryDto journalEntryDto);
    JournalEntryDto journalEntryToDto(JournalEntry journalEntry);
}
