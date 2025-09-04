package net.engineeringdigest.journalApp.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String userName;
    private String password;
    private String email;
    private boolean sentimentAnalysis;

}

