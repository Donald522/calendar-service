package calendar.dao;

import calendar.service.model.Meeting;
import calendar.service.model.MeetingId;
import calendar.service.model.Recurrence;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@RequiredArgsConstructor
public class MeetingPersisterDispatcher implements MeetingPersister {

  private final MeetingPersister singleMeetingPersister;
  private final MeetingPersister dailyMeetingPersister;

  @Override
  public Collection<MeetingId> persist(Meeting meeting) {
    if (Recurrence.NONE.equals(meeting.getRecurrence())) {
      return singleMeetingPersister.persist(meeting);
    } else if (Recurrence.DAILY.equals(meeting.getRecurrence())) {
      return dailyMeetingPersister.persist(meeting);
    } else {
      throw new UnsupportedOperationException(String.format(
          "%s recurrence not yet supported", meeting.getRecurrence()));
    }
  }
}
