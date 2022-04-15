package calendar.api.controller;

import calendar.api.CalendarApi;
import calendar.api.dto.MeetingDto;
import calendar.api.dto.MeetingResponseDto;
import calendar.api.dto.MeetingSummaryDto;
import calendar.service.CalendarService;
import calendar.service.converter.DateTimeConverter;
import calendar.service.converter.MeetingConverter;
import calendar.service.converter.MeetingResponseConverter;
import calendar.service.converter.MeetingSummaryConverter;
import calendar.service.model.Meeting;
import calendar.service.model.MeetingSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
public class CalendarController implements CalendarApi {

  private final DateTimeConverter dateTimeConverter;
  private final MeetingConverter meetingConverter;
  private final MeetingSummaryConverter meetingSummaryConverter;
  private final MeetingResponseConverter meetingResponseConverter;
  private final CalendarService calendarService;

  @Override
  public ResponseEntity<Long> createMeeting(MeetingDto meetingDto) {
    long id = calendarService.createMeeting(meetingConverter.toModel(meetingDto));
    return ResponseEntity.created(URI.create(String.format("/meetings/%s", id))).build();
  }

  @Override
  public ResponseEntity<MeetingDto> getMeetingDetails(Long meetingId) {
    Meeting meeting = calendarService.getMeeting(meetingId);
    return ResponseEntity.ok(meetingConverter.toDto(meeting));
  }

  @Override
  public ResponseEntity<Collection<MeetingSummaryDto>> getCalendarForUser(String user, String fromTime, String toTime) {
    LocalDateTime from = parseOrDefault(fromTime, LocalDate.now().atTime(LocalTime.MIN));
    LocalDateTime to = parseOrDefault(toTime, LocalDate.now().atTime(LocalTime.MAX));

    Collection<MeetingSummary> calendarForUser = calendarService.getCalendarForUser(user.trim(), from, to);

    return ResponseEntity.ok(meetingSummaryConverter.toDto(calendarForUser));
  }

  @Override
  public ResponseEntity<Object> respondToMeeting(MeetingResponseDto responseDto) {
    calendarService.respondToMeeting(meetingResponseConverter.fromDto(responseDto));
    return ResponseEntity.ok().build();
  }

  private LocalDateTime parseOrDefault(String date, LocalDateTime otherwise) {
    return Objects.nonNull(date)
        ? dateTimeConverter.parseDate(date)
        : otherwise;
  }
}
