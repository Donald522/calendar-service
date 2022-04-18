package calendar.service.meeting;

import calendar.service.model.Meeting;

import java.util.List;

public interface MeetingTransformer {

  List<Meeting> transform(Meeting meeting);
}
