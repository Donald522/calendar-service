package calendar.dao;

import calendar.service.model.MeetingSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collection;

@Component
@RequiredArgsConstructor
public class PersonalCalendarProvider implements UserCalendarProvider {

  private final JdbcTemplate calendarJdbcTemplate;

  @Override
  public Collection<MeetingSummary> getUserCalendar(String user, LocalDateTime from, LocalDateTime to) {
    String selectUSerCalendarSql =
        "select c.meeting_id, \n" +
            "   m.meeting_title, \n" +
            "   m.organizer,\n" +
            "   m.from_time,\n" +
            "   m.to_time,\n" +
            "   count(*)\n" +
            "from calendar c, meetings m\n" +
            "where 1=1\n" +
            "  and c.user_email = ?\n" +
            "  and from_time < ?\n" +
            "  and to_time > ?\n" +
            "  and not exists (select 1 from calendar cc\n" +
            "                  where cc.meeting_id = c.meeting_id\n" +
            "                    and cc.user_email = c.user_email\n" +
            "                    and cc.response = 'DECLINED')\n" +
            "  and c.meeting_id = m.id\n" +
            "group by c.meeting_id, \n" +
            "         m.meeting_title, \n" +
            "         m.organizer,\n" +
            "         m.from_time,\n" +
            "         m.to_time\n;";

    return calendarJdbcTemplate.query(selectUSerCalendarSql, meetingSummaryExtractor, user, to, from);
  }
}
