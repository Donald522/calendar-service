package calendar.dao;

import calendar.service.model.MeetingSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collection;

@Component
@RequiredArgsConstructor
public class RestrictedCalendarProvider implements UserCalendarProvider {

  private final JdbcTemplate calendarJdbcTemplate;

  @Override
  public Collection<MeetingSummary> getUserCalendar(String user, LocalDateTime from, LocalDateTime to) {
    String selectUSerCalendarSql =
        "select \n" +
            "          case \n" +
            "             when m.visibility='PUBLIC' then c.meeting_id ELSE -1\n" +
            "          end  as meeting_id, \n" +
            "          case \n" +
            "             when m.visibility='PUBLIC' then m.meeting_title ELSE ''\n" +
            "          end as meeting_title, \n" +
            "          case \n" +
            "             when m.visibility='PUBLIC' then m.organizer ELSE ''\n" +
            "          end as organizer, \n" +
            "          m.from_time,\n" +
            "          m.to_time\n" +
            "from calendar c, meetings m\n" +
            "where 1=1\n" +
            "and c.meeting_id = m.id\n" +
            "and c.user_email = ?\n" +
            "and m.from_time < ?\n" +
            "and m.to_time > ?\n" +
            "and not exists (select 1 from calendar cc\n" +
            "                              where cc.meeting_id = c.meeting_id\n" +
            "                                and cc.user_email = c.user_email\n" +
            "                               and cc.response = 'DECLINED')\n" +
            "group by c.meeting_id,\n" +
            "               m.meeting_title,\n" +
            "               m.organizer,\n" +
            "               m.from_time,\n" +
            "               m.to_time;";


    return calendarJdbcTemplate.query(selectUSerCalendarSql, meetingSummaryExtractor, user, to, from);
  }
}
