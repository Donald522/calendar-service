package calendar.dao;

import calendar.service.model.Meeting;
import calendar.service.model.MeetingSummary;
import com.google.common.collect.ImmutableList;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class H2CalendarDao implements CalendarDao {

  private final JdbcTemplate calendarJdbcTemplate;

  @Override
  public long createMeeting(Meeting meeting) {
    String insertMeetingSql = "insert into MEETINGS (" +
        "id, " +
        "meeting_title, " +
        "from_time, " +
        "to_time, " +
        "location, " +
        "organizer, " +
        "message) values (meetings_seq.nextval, ?, ?, ?, ?, ?, ?)";

    GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

    String id_column = "id";

    calendarJdbcTemplate.update(con -> {
      PreparedStatement ps = con.prepareStatement(insertMeetingSql, new String[]{id_column});
      ps.setString(1, meeting.getTitle());
      ps.setTimestamp(2, Timestamp.valueOf(meeting.getFromTime()));
      ps.setTimestamp(3, Timestamp.valueOf(meeting.getToTime()));
      ps.setString(4, meeting.getLocation());
      ps.setString(5, meeting.getOrganizer());
      ps.setString(6, meeting.getMessage());
      return ps;
    }, keyHolder);

    Long meetingId = (Long) keyHolder.getKeys().get(id_column);

    storeCalendarEvents(meeting.withId(meetingId));

    return meetingId;
  }

  private void storeCalendarEvents(Meeting meeting) {
    String insertCalendarSql = "insert into CALENDAR (" +
        "meeting_id, " +
        "user_email, " +
        "meeting_title, " +
        "from_time, " +
        "to_time) values (?, ?, ?, ?, ?)";

    List<String> attendees = Stream.concat(
        meeting.getParticipants().stream(), Stream.of(meeting.getOrganizer()))
        .collect(Collectors.toList());

    calendarJdbcTemplate.batchUpdate(insertCalendarSql, new BatchPreparedStatementSetter() {
      @Override
      public void setValues(PreparedStatement ps, int i) throws SQLException {
        String participant = attendees.get(i);
        ps.setLong(1, meeting.getId());
        ps.setString(2, participant);
        ps.setString(3, meeting.getTitle());
        ps.setTimestamp(4, Timestamp.valueOf(meeting.getFromTime()));
        ps.setTimestamp(5, Timestamp.valueOf(meeting.getToTime()));
      }

      @Override
      public int getBatchSize() {
        return attendees.size();
      }
    });
  }

  @Override
  public Collection<MeetingSummary> getUserCalendar(String user, LocalDateTime from, LocalDateTime to) {
    String selectUSerCalendarSql =
        "select " +
        "  meeting_id, " +
        "  meeting_title, " +
        "  from_time, " +
        "  to_time " +
        "from calendar " +
        "where user_email = ? " +
          "and from_time < ? " +
          "and to_time > ?";

    return calendarJdbcTemplate.query(selectUSerCalendarSql, rs -> {
      ImmutableList.Builder<MeetingSummary> meetingSummaryBuilder = ImmutableList.builder();
      while (rs.next()) {
        MeetingSummary meetingSummary = MeetingSummary.builder()
            .meetingId(rs.getLong("meeting_id"))
            .title(rs.getString("meeting_title"))
            .fromTime(rs.getTimestamp("from_time").toLocalDateTime())
            .toTime(rs.getTimestamp("to_time").toLocalDateTime())
            .build();
        meetingSummaryBuilder.add(meetingSummary);
      }
      return meetingSummaryBuilder.build();
    }, user, to, from);
  }

  @Override
  public Meeting getMeetingDetails(long meetingId) {
    String selectMeetingSql =
        "select " +
        "m.*, c.user_email\n" +
        "from meetings m, calendar c\n" +
        "where 1=1\n" +
        "and c.meeting_id = m.id\n" +
        "and m.id = ?;";

    return calendarJdbcTemplate.query(selectMeetingSql, rs -> {
      ImmutableList.Builder<String> participantListBuilder = ImmutableList.builder();
      Meeting.MeetingBuilder meetingBuilder = Meeting.builder();
      while (rs.next()) {
        participantListBuilder.add(rs.getString("user_email"));
        meetingBuilder.id(rs.getLong("id"));
        meetingBuilder.title(rs.getString("meeting_title"));
        meetingBuilder.organizer(rs.getString("organizer"));
        meetingBuilder.location(rs.getString("location"));
        meetingBuilder.fromTime(rs.getTimestamp("from_time").toLocalDateTime());
        meetingBuilder.toTime(rs.getTimestamp("to_time").toLocalDateTime());
        meetingBuilder.message(rs.getString("message"));
      }
      return meetingBuilder
          .participants(participantListBuilder.build())
          .build();
    }, meetingId);
  }

  @Override
  public void acceptMeeting(String user, long meetingId) {

  }

  @Override
  public void rejectMeeting(String user, long meetingId) {

  }
}
