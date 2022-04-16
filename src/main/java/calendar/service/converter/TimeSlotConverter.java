package calendar.service.converter;

import calendar.api.dto.TimeSlotDto;
import calendar.service.model.TimeSlot;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TimeSlotConverter {

  private final DateTimeConverter dateTimeConverter;

  public TimeSlotDto toDto(TimeSlot model) {
    return TimeSlotDto.builder()
        .from(dateTimeConverter.formatDate(model.getFrom()))
        .to(dateTimeConverter.formatDate(model.getTo()))
        .build();
  }
}
