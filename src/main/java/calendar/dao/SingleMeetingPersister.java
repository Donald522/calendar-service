package calendar.dao;

import calendar.service.model.Meeting;
import calendar.service.model.MeetingId;
import com.google.common.collect.ImmutableList;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class SingleMeetingPersister implements MeetingPersister {

  private final JdbcTemplate calendarJdbcTemplate;

  @Override
  public Collection<MeetingId> persist(Meeting meeting) {
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

    long meetingSubId = 1;

    calendarJdbcTemplate.update(con -> {
      PreparedStatement ps = con.prepareStatement(insertMeetingSql);
      ps.setLong(1, meeting.getId());
      ps.setLong(2, meetingSubId);
      ps.setString(3, meeting.getTitle());
      ps.setTimestamp(4, Timestamp.valueOf(meeting.getFromTime()));
      ps.setTimestamp(5, Timestamp.valueOf(meeting.getToTime()));
      ps.setString(6, meeting.getLocation());
      ps.setString(7, meeting.getOrganizer());
      ps.setString(8, meeting.getVisibility().name());
      ps.setString(9, meeting.getRecurrence().name());
      ps.setString(10, meeting.getMessage());
      return ps;
    });

    storeCalendarEvents(meeting);

    return ImmutableList.of(
        MeetingId.builder()
            .id(meeting.getId())
            .subId(meetingSubId)
            .build());
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
