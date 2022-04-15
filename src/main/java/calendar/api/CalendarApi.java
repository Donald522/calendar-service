package calendar.api;

import calendar.api.dto.MeetingDto;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/calendar")
public interface CalendarApi {

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<Long> createMeeting(@RequestBody MeetingDto meetingDto);

  @GetMapping(value = "/{meetingId}", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<MeetingDto> getMeetingDetails(@PathVariable Long meetingId);
}
