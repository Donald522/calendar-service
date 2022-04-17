package calendar.service.converter;

import calendar.api.dto.MeetingDto;
import calendar.service.exception.BadRequestException;
import calendar.service.model.Meeting;
import calendar.service.model.Recurrence;
import calendar.service.model.Visibility;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Log4j2
@Component
@RequiredArgsConstructor
public class MeetingConverter {

  private final DateTimeConverter dateTimeConverter;

  public Meeting toModel(MeetingDto dto) {
    try {
      return Meeting.builder()
          .title(dto.getTitle())
          .organizer(dto.getOrganizer())
          .location(dto.getLocation())
          .fromTime(dateTimeConverter.parseDate(dto.getFromTime()))
          .toTime(dateTimeConverter.parseDate(dto.getToTime()))
          .message(dto.getMessage())
          .visibility(Visibility.valueOf(dto.getVisibility()))
          .recurrence(Recurrence.valueOf(dto.getRecurrence()))
          .participants(dto.getParticipants())
          .build();
    } catch (IllegalArgumentException e) {
      log.error("Unknown visibility setting: {}. " +
          "Should be one of [{}]", dto.getVisibility(), Visibility.values());
      throw new BadRequestException(String.format(
          "Unknown visibility setting: %s. " +
              "Should be one of %s", dto.getVisibility(), Arrays.toString(Visibility.values())
      ));
    }
  }

  public MeetingDto toDto(Meeting model) {
    return MeetingDto.builder()
        .title(model.getTitle())
        .organizer(model.getOrganizer())
        .location(model.getLocation())
        .fromTime(dateTimeConverter.formatDate(model.getFromTime()))
        .toTime(dateTimeConverter.formatDate(model.getToTime()))
        .message(model.getMessage())
        .visibility(model.getVisibility().name())
        .recurrence(model.getRecurrence().name())
        .participants(model.getParticipants())
        .build();
  }
}
