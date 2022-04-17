package calendar.dao;

import calendar.service.model.Meeting;
import calendar.service.model.MeetingId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Component
public class DailyMeetingPersister implements MeetingPersister {

  private final int recurrenceDuration;
  private final JdbcTemplate calendarJdbcTemplate;

  public DailyMeetingPersister(@Value("${calendar.recurrence.duration.days:180}") int recurrenceDuration,
                               JdbcTemplate calendarJdbcTemplate) {
    this.recurrenceDuration = recurrenceDuration;
    this.calendarJdbcTemplate = calendarJdbcTemplate;
  }

  @Override
  public Collection<MeetingId> persist(Meeting meeting) {
    LocalDateTime startFrom = meeting.getFromTime();
    LocalDateTime startTo = meeting.getToTime();

    List<Meeting> meetings = IntStream.range(0, recurrenceDuration)
        .mapToObj(i -> meeting
            .withSubId(i + 1)
            .withFromTime(startFrom.plusDays(i))
            .withToTime(startTo.plusDays(i)))
        .collect(toList());

    String insertMeetingSql = "insert into MEETINGS (" +
        "id, " +
        "sub_id, " +
        "meeting_title, " +
        "from_time, " +
        "to_time, " +
        "location, " +
        "organizer, " +
        "visibility, " +
        "recurrence, " +
        "message) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    calendarJdbcTemplate.batchUpdate(insertMeetingSql, new BatchPreparedStatementSetter() {
      @Override
      public void setValues(PreparedStatement ps, int i) throws SQLException {
        Meeting meeting = meetings.get(i);
        ps.setLong(1, meeting.getId());
        ps.setLong(2, meeting.getSubId());
        ps.setString(3, meeting.getTitle());
        ps.setTimestamp(4, Timestamp.valueOf(meeting.getFromTime()));
        ps.setTimestamp(5, Timestamp.valueOf(meeting.getToTime()));
        ps.setString(6, meeting.getLocation());
        ps.setString(7, meeting.getOrganizer());
        ps.setString(8, meeting.getVisibility().name());
        ps.setString(9, meeting.getRecurrence().name());
        ps.setString(10, meeting.getMessage());
      }

      @Override
      public int getBatchSize() {
        return meetings.size();
      }
    });

    storeCalendarEvents(meeting);

    return meetings.stream()
        .map(m -> new MeetingId(m.getId(), m.getSubId()))
        .collect(toList());
  }

  private void storeCalendarEvents(Meeting meeting) {
    String insertCalendarSql = "insert into CALENDAR (meeting_id, user_email) values (?, ?)";

    List<String> attendees = Stream.concat(
        meeting.getParticipants().stream(), Stream.of(meeting.getOrganizer()))
        .collect(Collectors.toList());

    calendarJdbcTemplate.batchUpdate(insertCalendarSql, new BatchPreparedStatementSetter() {
      @Override
      public void setValues(PreparedStatement ps, int i) throws SQLException {
        String participant = attendees.get(i);
        ps.setLong(1, meeting.getId());
        ps.setString(2, participant);
      }

      @Override
      public int getBatchSize() {
        return attendees.size();
      }
    });
  }
}
