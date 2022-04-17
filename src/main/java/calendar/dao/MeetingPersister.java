package calendar.dao;

import calendar.service.model.Meeting;
import calendar.service.model.MeetingId;

import java.util.Collection;

public interface MeetingPersister {

  Collection<MeetingId> persist(Meeting meeting);
}
