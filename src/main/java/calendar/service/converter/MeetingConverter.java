package calendar.service.converter;

import calendar.api.dto.MeetingDto;
import calendar.service.model.Meeting;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class MeetingConverter {

  private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

  public Meeting toModel(MeetingDto dto) {
    return Meeting.builder()
        .title(dto.getTitle())
        .organizer(dto.getOrganizer())
        .location(dto.getLocation())
        .fromTime(LocalDateTime.parse(dto.getFromTime(), formatter))
        .toTime(LocalDateTime.parse(dto.getToTime(), formatter))
        .message(dto.getMessage())
        .participants(dto.getParticipants())
        .build();
  }

  public MeetingDto toDto(Meeting model) {
    return MeetingDto.builder()
        .title(model.getTitle())
        .organizer(model.getOrganizer())
        .location(model.getLocation())
        .fromTime(model.getFromTime().format(formatter))
        .toTime(model.getToTime().format(formatter))
        .message(model.getMessage())
        .participants(model.getParticipants())
        .build();
  }
}
