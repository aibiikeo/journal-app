package com.example.journalapp.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthDto {
    @NotEmpty
    String email;
    @NotEmpty
    @Size(min = 8)
    String password;
}
