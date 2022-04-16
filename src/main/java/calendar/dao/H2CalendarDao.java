package calendar.dao;

import calendar.service.model.Meeting;
import calendar.service.model.MeetingResponse;
import calendar.service.model.Visibility;
import com.google.common.collect.ImmutableList;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
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
        "visibility, " +
        "message) values (meetings_seq.nextval, ?, ?, ?, ?, ?, ?, ?)";

    GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

    String id_column = "id";

    calendarJdbcTemplate.update(con -> {
      PreparedStatement ps = con.prepareStatement(insertMeetingSql, new String[]{id_column});
      ps.setString(1, meeting.getTitle());
      ps.setTimestamp(2, Timestamp.valueOf(meeting.getFromTime()));
      ps.setTimestamp(3, Timestamp.valueOf(meeting.getToTime()));
      ps.setString(4, meeting.getLocation());
      ps.setString(5, meeting.getOrganizer());
      ps.setString(6, meeting.getVisibility().name());
      ps.setString(7, meeting.getMessage());
      return ps;
    }, keyHolder);

    Long meetingId = (Long) keyHolder.getKeys().get(id_column);

    storeCalendarEvents(meeting.withId(meetingId));

    return meetingId;
  }

  @Override
  public boolean isPermitted(String user, long meetingId) {
    String sql =
        "select count(*) as cnt\n" +
            "from calendar c, meetings m\n" +
            "where 1=1\n" +
            "and c.meeting_id = m.id\n" +
            "and m.id = ?\n" +
            "and (c.user_email = ? or m.visibility = 'PUBLIC')";
    Long count = calendarJdbcTemplate.queryForObject(sql,
        (rs, rowNum) -> rs.getLong("cnt"),
        meetingId, user);
    return count != null && count > 0;
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
  public Optional<Meeting> getMeetingDetails(long meetingId) {
    String selectMeetingSql =
        "select distinct m.*, c.user_email\n" +
            "from meetings m, calendar c\n" +
            "where 1=1\n" +
            "  and c.meeting_id = m.id\n" +
            "  and not exists (select 1 from calendar cc " +
            "                   where cc.meeting_id=m.id " +
            "                     and cc.user_email = c.user_email" +
            "                     and cc.response = 'DECLINED')\n" +
            "and m.id = ?;";

    return calendarJdbcTemplate.query(selectMeetingSql, rs -> {
      ImmutableList.Builder<String> participantListBuilder = ImmutableList.builder();
      Meeting.MeetingBuilder meetingBuilder = Meeting.builder();
      if (!rs.next()) {
        return Optional.empty();
      } else {
        meetingBuilder.id(rs.getLong("id"));
        meetingBuilder.title(rs.getString("meeting_title"));
        meetingBuilder.organizer(rs.getString("organizer"));
        meetingBuilder.location(rs.getString("location"));
        meetingBuilder.fromTime(rs.getTimestamp("from_time").toLocalDateTime());
        meetingBuilder.toTime(rs.getTimestamp("to_time").toLocalDateTime());
        meetingBuilder.visibility(Visibility.valueOf(rs.getString("visibility")));
        meetingBuilder.message(rs.getString("message"));
        do {
          participantListBuilder.add(rs.getString("user_email"));
        }
        while (rs.next());
        return Optional.of(meetingBuilder
            .participants(participantListBuilder.build())
            .build());
      }
    }, meetingId);
  }

  @Override
  public void respondToMeeting(MeetingResponse meetingResponse) {
    String insertResponseSql = "insert into CALENDAR (" +
        "meeting_id, " +
        "user_email, " +
        "response) values (?, ?, ?)";

    calendarJdbcTemplate.update(insertResponseSql,
        meetingResponse.getMeetingId(),
        meetingResponse.getUser(),
        meetingResponse.getResponse().name());
  }
}
