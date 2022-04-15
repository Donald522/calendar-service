package calendar.service.converter;

import calendar.api.dto.MeetingDto;
import calendar.service.model.Meeting;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MeetingConverter {

  private final DateTimeConverter dateTimeConverter;

  public Meeting toModel(MeetingDto dto) {
    return Meeting.builder()
        .title(dto.getTitle())
        .organizer(dto.getOrganizer())
        .location(dto.getLocation())
        .fromTime(dateTimeConverter.parseDate(dto.getFromTime()))
        .toTime(dateTimeConverter.parseDate(dto.getToTime()))
        .message(dto.getMessage())
        .participants(dto.getParticipants())
        .build();
  }

  public MeetingDto toDto(Meeting model) {
    return MeetingDto.builder()
        .title(model.getTitle())
        .organizer(model.getOrganizer())
        .location(model.getLocation())
        .fromTime(dateTimeConverter.formatDate(model.getFromTime()))
        .toTime(dateTimeConverter.formatDate(model.getToTime()))
        .message(model.getMessage())
        .participants(model.getParticipants())
        .build();
  }
}
