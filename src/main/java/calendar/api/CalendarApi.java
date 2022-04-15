package calendar.api;

import calendar.api.dto.MeetingDto;
import calendar.api.dto.MeetingResponseDto;
import calendar.api.dto.MeetingSummaryDto;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RequestMapping("/meetings")
public interface CalendarApi {

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<Long> createMeeting(@RequestBody MeetingDto meetingDto);

  @GetMapping(value = "/{meetingId}", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<MeetingDto> getMeetingDetails(@PathVariable Long meetingId);

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<Collection<MeetingSummaryDto>> getCalendarForUser(@RequestParam String user,
                                                                   @RequestParam(required = false) String fromTime,
                                                                   @RequestParam(required = false) String toTime);

  @PutMapping(value = "/response", consumes = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<Object> respondToMeeting(@RequestBody MeetingResponseDto responseDto);
}
