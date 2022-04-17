package calendar.service.converter;

import calendar.api.dto.MeetingSummaryDto;
import calendar.service.model.MeetingSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MeetingSummaryConverter {

  private final DateTimeConverter dateTimeConverter;

  public MeetingSummaryDto toDto(MeetingSummary model) {
    return MeetingSummaryDto.builder()
        .meetingId(model.getMeetingId())
        .meetingSubId(model.getMeetingSubId())
        .title(model.getTitle())
        .organizer(model.getOrganizer())
        .fromTime(dateTimeConverter.formatDate(model.getFromTime()))
        .toTime(dateTimeConverter.formatDate(model.getToTime()))
        .build();
  }

  public Collection<MeetingSummaryDto> toDto(Collection<MeetingSummary> models) {
    return models.stream().map(this::toDto).collect(Collectors.toList());
  }
}
