package calendar.api.controller;

import calendar.api.CalendarApi;
import calendar.api.dto.MeetingDto;
import calendar.service.CalendarService;
import calendar.service.converter.MeetingConverter;
import calendar.service.model.Meeting;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class CalendarController implements CalendarApi {

  private final MeetingConverter meetingConverter;
  private final CalendarService calendarService;

  @Override
  public ResponseEntity<Long> createMeeting(MeetingDto meetingDto) {
    long id = calendarService.createMeeting(meetingConverter.toModel(meetingDto));
    return ResponseEntity.created(URI.create(String.format("/calendar/%s", id))).build();
  }

  @Override
  public ResponseEntity<MeetingDto> getMeetingDetails(Long meetingId) {
    Meeting meeting = calendarService.getMeeting(meetingId);
    return ResponseEntity.ok(meetingConverter.toDto(meeting));
  }
}
