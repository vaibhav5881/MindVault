package net.engineeringdigest.journalApp.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.engineeringdigest.journalApp.enums.Sentiment;

@Data
@NoArgsConstructor
public class JournalEntryDto {
    private String title;
    private String content;
    private Sentiment sentiment;
}
