package calendar.api;

import calendar.api.dto.*;
import calendar.service.model.MeetingId;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RequestMapping("/meetings")
public interface CalendarApi {

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<Collection<MeetingId>> createMeeting(@RequestBody MeetingDto meetingDto);

  @GetMapping(value = "/{meetingId}/{meetingSubId}", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<MeetingDto> getMeetingDetails(@PathVariable Long meetingId, @PathVariable Long meetingSubId);

  @GetMapping(value = "/calendar/{user}", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<Collection<MeetingSummaryDto>> getCalendarForUser(@PathVariable String user,
                                                                   @RequestParam(required = false) String fromTime,
                                                                   @RequestParam(required = false) String toTime);

  @PostMapping(value = "/response", consumes = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<Object> respondToMeeting(@RequestBody MeetingResponseDto responseDto);

  @GetMapping(value = "/suggestion",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<TimeSlotDto> suggestMeetingSlot(@RequestBody MeetingSlotRequestDto meetingSlotRequestDto);
}
