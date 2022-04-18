package calendar.dao;

import calendar.service.meeting.MeetingTransformer;
import calendar.service.model.*;
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
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Component
@RequiredArgsConstructor
public class H2CalendarDao implements CalendarDao {

  private final JdbcTemplate calendarJdbcTemplate;
  private final MeetingTransformer meetingTransformerDispatcher;

  @Override
  public Collection<MeetingId> createMeeting(Meeting meeting) {
    Long meetingId = calendarJdbcTemplate.queryForObject("SELECT meetings_seq.nextval as id from dual",
        (rs, rowNum) -> rs.getLong("id"));

    if (meetingId == null) {
      throw new NullPointerException(String.format(
          "Cannot generate ID for meeting: %s", meeting.getTitle()));
    }

    Meeting meetingWithId = meeting.withId(meetingId);

    List<Meeting> meetings = meetingTransformerDispatcher.transform(meetingWithId);

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

    storeCalendarEvents(meetingWithId);

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

  @Override
  public boolean isPermitted(String user, long meetingId, long meetingSubId) {
    String sql =
        "select count(*) as cnt\n" +
            "from calendar c, meetings m\n" +
            "where 1=1\n" +
            "and c.meeting_id = m.id\n" +
            "and (m.sub_id = c.meeting_sub_id or c.meeting_sub_id < 0)\n" +
            "and m.id = ?\n" +
            "and m.sub_id = ?\n" +
            "and (c.user_email = ? or m.visibility = 'PUBLIC')";

    Long count = calendarJdbcTemplate.queryForObject(sql,
        (rs, rowNum) -> rs.getLong("cnt"),
        meetingId, meetingSubId, user);
    return count != null && count > 0;
  }

  @Override
  public Optional<Meeting> getMeetingDetails(long meetingId, long meetingSubId) {
    String selectMeetingSql =
        "select distinct m.*, c.user_email\n" +
            "from meetings m, calendar c\n" +
            "where 1=1\n" +
            "  and c.meeting_id = m.id\n" +
            "  and (m.sub_id = c.meeting_sub_id or c.meeting_sub_id < 0)\n" +
            "  and not exists (select 1 from calendar cc " +
            "                   where cc.meeting_id = m.id " +
            "                     and (cc.meeting_sub_id = m.sub_id or cc.meeting_sub_id < 0)\n" +
            "                     and cc.user_email = c.user_email" +
            "                     and cc.response = 'DECLINED')\n" +
            "and m.id = ?" +
            "and m.sub_id = ?;";

    return calendarJdbcTemplate.query(selectMeetingSql, rs -> {
      ImmutableList.Builder<String> participantListBuilder = ImmutableList.builder();
      Meeting.MeetingBuilder meetingBuilder = Meeting.builder();
      if (!rs.next()) {
        return Optional.empty();
      } else {
        meetingBuilder.id(rs.getLong("id"));
        meetingBuilder.subId(rs.getLong("sub_id"));
        meetingBuilder.title(rs.getString("meeting_title"));
        meetingBuilder.organizer(rs.getString("organizer"));
        meetingBuilder.location(rs.getString("location"));
        meetingBuilder.fromTime(rs.getTimestamp("from_time").toLocalDateTime());
        meetingBuilder.toTime(rs.getTimestamp("to_time").toLocalDateTime());
        meetingBuilder.visibility(Visibility.valueOf(rs.getString("visibility")));
        meetingBuilder.recurrence(Recurrence.valueOf(rs.getString("recurrence")));
        meetingBuilder.message(rs.getString("message"));
        do {
          participantListBuilder.add(rs.getString("user_email"));
        }
        while (rs.next());
        return Optional.of(meetingBuilder
            .participants(participantListBuilder.build())
            .build());
      }
    }, meetingId, meetingSubId);
  }

  @Override
  public void respondToMeeting(MeetingResponse meetingResponse) {
    String insertResponseSql = "insert into CALENDAR (" +
        "meeting_id, " +
        "meeting_sub_id, " +
        "user_email, " +
        "response) values (?, ?, ?, ?)";

    calendarJdbcTemplate.update(insertResponseSql,
        meetingResponse.getMeetingId(),
        meetingResponse.getMeetingSubId(),
        meetingResponse.getUser(),
        meetingResponse.getResponse().name());
  }
}
