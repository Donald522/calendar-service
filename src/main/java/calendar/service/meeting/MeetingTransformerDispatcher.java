package calendar.service.meeting;

import calendar.service.exception.BadRequestException;
import calendar.service.model.Meeting;
import calendar.service.model.Recurrence;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class MeetingTransformerDispatcher implements MeetingTransformer {

  private final Map<Recurrence, MeetingTransformer> transformerMap;

  @Override
  public List<Meeting> transform(Meeting meeting) {
    MeetingTransformer transformer = transformerMap.getOrDefault(meeting.getRecurrence(), meeting1 -> {
      throw new BadRequestException(String.format(
          "Unsupported type of recurrence: [%s]. Shold be one of [%s]",
          meeting.getRecurrence(), Arrays.toString(Recurrence.values())
      ));
    });

    return transformer.transform(meeting);
  }
}
