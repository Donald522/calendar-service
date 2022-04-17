package calendar.dao;

import calendar.service.model.*;
import com.google.common.collect.ImmutableList;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class H2CalendarDao implements CalendarDao {

  private final JdbcTemplate calendarJdbcTemplate;
  private final MeetingPersister meetingPersisterDispatcher;

  @Override
  public Collection<MeetingId> createMeeting(Meeting meeting) {
    Long meetingId = calendarJdbcTemplate.queryForObject("SELECT meetings_seq.nextval as id from dual",
        (rs, rowNum) -> rs.getLong("id"));

    if (meetingId == null) {
      throw new NullPointerException(String.format(
          "Cannot generate ID for meeting: %s", meeting.getTitle()));
    }
    return meetingPersisterDispatcher.persist(meeting.withId(meetingId));
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
