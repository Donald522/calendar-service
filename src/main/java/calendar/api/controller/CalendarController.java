package calendar.api.controller;

import calendar.api.CalendarApi;
import calendar.api.dto.*;
import calendar.service.CalendarService;
import calendar.service.converter.*;
import calendar.service.model.Meeting;
import calendar.service.model.MeetingId;
import calendar.service.model.MeetingSummary;
import calendar.service.model.TimeSlot;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

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
  private final MeetingSlotRequestConverter meetingSlotRequestConverter;
  private final TimeSlotConverter timeSlotConverter;
  private final CalendarService calendarService;

  @Override
  public ResponseEntity<Collection<MeetingId>> createMeeting(MeetingDto meetingDto) {
    Collection<MeetingId> ids = calendarService.createMeeting(meetingConverter.toModel(meetingDto));
    return ResponseEntity.ok(ids);
  }

  @Override
  public ResponseEntity<MeetingDto> getMeetingDetails(Long meetingId, Long meetingSubId) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    Meeting meeting = calendarService.getMeeting(authentication.getName(), meetingId, meetingSubId);
    return ResponseEntity.ok(meetingConverter.toDto(meeting));
  }

  @Override
  public ResponseEntity<Collection<MeetingSummaryDto>> getCalendarForUser(String user, String fromTime, String toTime) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    LocalDateTime from = parseOrDefault(fromTime, LocalDate.now().atTime(LocalTime.MIN));
    LocalDateTime to = parseOrDefault(toTime, LocalDate.now().atTime(LocalTime.MAX));

    Collection<MeetingSummary> calendarForUser = calendarService.getCalendarForUser(
        authentication.getName(), user.trim(), from, to);

    return ResponseEntity.ok(meetingSummaryConverter.toDto(calendarForUser));
  }

  @Override
  public ResponseEntity<Object> respondToMeeting(MeetingResponseDto responseDto) {
    calendarService.respondToMeeting(meetingResponseConverter.fromDto(responseDto));
    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<TimeSlotDto> suggestMeetingSlot(MeetingSlotRequestDto meetingSlotRequestDto) {
    TimeSlot timeSlot = calendarService.suggestMeetingSlot(meetingSlotRequestConverter.fromDto(meetingSlotRequestDto));
    return ResponseEntity.ok(timeSlotConverter.toDto(timeSlot));
  }

  private LocalDateTime parseOrDefault(String date, LocalDateTime otherwise) {
    return Objects.nonNull(date)
        ? dateTimeConverter.parseDate(date)
        : otherwise;
  }
}
