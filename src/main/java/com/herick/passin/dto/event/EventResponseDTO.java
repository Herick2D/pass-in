package com.herick.passin.dto.event;

import com.herick.passin.domain.event.Event;
import lombok.Getter;

@Getter
public class EventResponseDTO {
  EventDetailDTO event;

  public EventResponseDTO(Event event, Integer numberOFAttendees) {
    this.event = new EventDetailDTO(
      event.getId(),
      event.getTitle(),
      event.getDetails(),
      event.getSlug(),
      event.getMaximumAttendees(),
      numberOFAttendees
    );
  }
}
