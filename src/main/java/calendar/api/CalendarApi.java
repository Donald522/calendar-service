package calendar.api;

import calendar.api.dto.MeetingDto;
import calendar.api.dto.MeetingSummaryDto;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RequestMapping("/calendar")
public interface CalendarApi {

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<Long> createMeeting(@RequestBody MeetingDto meetingDto);

  @GetMapping(value = "/{meetingId}", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<MeetingDto> getMeetingDetails(@PathVariable Long meetingId);

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<Collection<MeetingSummaryDto>> getCalendarForUser(@RequestParam String user,
                                                                   @RequestParam String fromTime,
                                                                   @RequestParam String toTime);
}
