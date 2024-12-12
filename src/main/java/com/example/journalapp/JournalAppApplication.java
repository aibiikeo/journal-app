package com.example.journalapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class JournalAppApplication {

    @GetMapping
    public String getAllJournalEntriesByUser() {
        return "It is journal app.";
    }

    public static void main(String[] args) {
        SpringApplication.run(JournalAppApplication.class, args);
    }

}
