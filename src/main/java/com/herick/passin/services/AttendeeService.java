package com.herick.passin.services;

import com.herick.passin.domain.attendee.Attendee;
import com.herick.passin.domain.attendee.exceptions.AttendeeAlreadyExistException;
import com.herick.passin.domain.attendee.exceptions.AttendeeNotFoundException;
import com.herick.passin.domain.checkin.CheckIn;
import com.herick.passin.dto.attendee.AttendeeBadgeResponseDTO;
import com.herick.passin.dto.attendee.AttendeeDetails;
import com.herick.passin.dto.attendee.AttendeesListResponseDTO;
import com.herick.passin.dto.attendee.AttendeeBadgeDTO;
import com.herick.passin.repositories.AttendeeRepository;
import com.herick.passin.repositories.CheckinRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AttendeeService {
  private final AttendeeRepository attendeeRepository;
  private final CheckinRepository checkinRepository;

  public List<Attendee> getAllAttendeesFromEvent(String eventId) {
    List<Attendee> attendeesList =  this.attendeeRepository.findByEventId(eventId);

    return attendeesList;
  }

  public AttendeesListResponseDTO getEventsAttendee(String eventId) {
    List<Attendee> attendeeList = this.getAllAttendeesFromEvent(eventId);

    List<AttendeeDetails> attendeeDetailsList = attendeeList.stream()
        .map(attendee -> {
          Optional<CheckIn> checkIn = this.checkinRepository.findByAttendeeId(attendee.getId());
          LocalDateTime checkedInAt = checkIn.<LocalDateTime>map(CheckIn::getCreatedAt).orElse(null);
          return new AttendeeDetails(
              attendee.getId(),
              attendee.getName(),
              attendee.getEmail(),
              attendee.getCreatedAt(),
              checkedInAt
          );
        }).toList();

    return new AttendeesListResponseDTO(attendeeDetailsList);
  }

  public void verifyAttendeesubscription(String email, String eventId) {
    Optional<Attendee> isAttendeeRegistered = this.attendeeRepository.findByEventIdAndEmail(eventId, email);
    if (isAttendeeRegistered.isPresent()) {
      throw new AttendeeAlreadyExistException("Attendee already registered");
    }
  }

  public Attendee registerAttendee(Attendee newAttendee) {
    this.attendeeRepository.save(newAttendee);
    return newAttendee;
  }

  public AttendeeBadgeResponseDTO getAttendeeBadge(String attendeeId, UriComponentsBuilder uriComponentsBuilder) {
    Attendee attendee = this.attendeeRepository.findById(attendeeId).orElseThrow(() -> new AttendeeNotFoundException("Attendee not found with ID " + attendeeId));

    var uri = uriComponentsBuilder.path("/attendees/{attendeeid}/check-in").buildAndExpand(attendeeId).toUri().toString();

    AttendeeBadgeDTO attendeeBadgeDTO = new AttendeeBadgeDTO(
        attendee.getName(),
        attendee.getEmail(),
        uri,
        attendee.getEvent().getId()
    );
    return new AttendeeBadgeResponseDTO(attendeeBadgeDTO);

  }
}
