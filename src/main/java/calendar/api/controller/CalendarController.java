package calendar.api.controller;

import calendar.api.CalendarApi;
import calendar.api.dto.MeetingDto;
import calendar.api.dto.MeetingSummaryDto;
import calendar.service.CalendarService;
import calendar.service.converter.DateTimeConverter;
import calendar.service.converter.MeetingConverter;
import calendar.service.converter.MeetingSummaryConverter;
import calendar.service.model.Meeting;
import calendar.service.model.MeetingSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Collection;

@RestController
@RequiredArgsConstructor
public class CalendarController implements CalendarApi {

  private final DateTimeConverter dateTimeConverter;
  private final MeetingConverter meetingConverter;
  private final MeetingSummaryConverter meetingSummaryConverter;
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

  @Override
  public ResponseEntity<Collection<MeetingSummaryDto>> getCalendarForUser(String user, String fromTime, String toTime) {
    Collection<MeetingSummary> calendarForUser = calendarService.getCalendarForUser(
        user,
        dateTimeConverter.parseDate(fromTime),
        dateTimeConverter.parseDate(toTime));

    return ResponseEntity.ok(meetingSummaryConverter.toDto(calendarForUser));
  }
}
