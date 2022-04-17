package calendar.service.converter;

import calendar.api.dto.MeetingResponseDto;
import calendar.service.exception.BadRequestException;
import calendar.service.model.MeetingResponse;
import calendar.service.model.Response;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Log4j2
@Component
public class MeetingResponseConverter {

  public MeetingResponse fromDto(MeetingResponseDto dto) {
    try {
      return MeetingResponse.builder()
          .meetingId(dto.getMeetingId())
          .meetingSubId(dto.getMeetingSubId())
          .user(dto.getUser())
          .response(Response.valueOf(dto.getResponse().toUpperCase()))
          .build();
    } catch (IllegalArgumentException e) {
      log.error("Unknown response type: {}. " +
          "Should be one of [{}]", dto.getResponse(), Response.values());
      throw new BadRequestException(String.format(
          "Unknown response type: %s. " +
              "Should be one of %s", dto.getResponse(), Arrays.toString(Response.values())
      ));
    }
  }
}
