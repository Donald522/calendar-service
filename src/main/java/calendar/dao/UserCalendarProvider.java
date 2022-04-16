package calendar.dao;

import calendar.service.model.MeetingSummary;
import com.google.common.collect.ImmutableList;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.time.LocalDateTime;
import java.util.Collection;

public interface UserCalendarProvider {

  ResultSetExtractor<ImmutableList<MeetingSummary>> meetingSummaryExtractor = rs -> {
    ImmutableList.Builder<MeetingSummary> meetingSummaryBuilder = ImmutableList.builder();
    while (rs.next()) {
      MeetingSummary meetingSummary = MeetingSummary.builder()
          .meetingId(rs.getLong("meeting_id"))
          .title(rs.getString("meeting_title"))
          .organizer(rs.getString("organizer"))
          .fromTime(rs.getTimestamp("from_time").toLocalDateTime())
          .toTime(rs.getTimestamp("to_time").toLocalDateTime())
          .build();
      meetingSummaryBuilder.add(meetingSummary);
    }
    return meetingSummaryBuilder.build();
  };

  Collection<MeetingSummary> getUserCalendar(String user, LocalDateTime from, LocalDateTime to);
}
