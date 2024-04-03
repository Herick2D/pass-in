package com.herick.passin.dto.attendee;

public record AttendeeBadgeDTO(
    String name,
    String email,
    String checkUrl,
    String eventId
) {
}
