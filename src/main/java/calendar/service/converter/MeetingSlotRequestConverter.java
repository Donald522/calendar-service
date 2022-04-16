package calendar.service.converter;

import calendar.api.dto.MeetingSlotRequestDto;
import calendar.service.model.MeetingSlotRequest;
import org.springframework.stereotype.Component;

@Component
public class MeetingSlotRequestConverter {

  public MeetingSlotRequest fromDto(MeetingSlotRequestDto dto) {
    return MeetingSlotRequest.builder()
        .durationMin(dto.getDurationMin())
        .participants(dto.getParticipants())
        .build();
  }
}
