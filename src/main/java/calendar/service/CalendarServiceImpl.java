package calendar.service;

import calendar.dao.CalendarDao;
import calendar.dao.UserDao;
import calendar.service.exception.BadRequestException;
import calendar.service.exception.InternalServiceException;
import calendar.service.exception.NotFoundException;
import calendar.service.exception.PermissionException;
import calendar.service.model.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.TreeMap;

@Log4j2
@Component
public class CalendarServiceImpl implements CalendarService {

  private final long minimalTimeSlot;
  private final CalendarDao calendarDao;
  private final UserDao userDao;
  private final UserCalendarAdapter userCalendarAdapter;
  private final CurrentDateProvider currentDateProvider;

  @Autowired
  public CalendarServiceImpl(@Value("${calendar.minimal.meeting.slot.minutes}") long minimalTimeSlot,
                             CalendarDao calendarDao, UserDao userDao,
                             UserCalendarAdapter userCalendarAdapter,
                             CurrentDateProvider currentDateProvider) {
    this.minimalTimeSlot = minimalTimeSlot;
    this.calendarDao = calendarDao;
    this.userDao = userDao;
    this.userCalendarAdapter = userCalendarAdapter;
    this.currentDateProvider = currentDateProvider;
  }

  @Override
  @Transactional
  public long createMeeting(Meeting meeting) {
    log.info("Creating new meeting [{}]", meeting.getTitle());
    long meetingDuration = Duration.between(meeting.getToTime(), meeting.getFromTime()).toMinutes();
    if (meetingDuration < minimalTimeSlot) {
      log.warn("Cannot create meeting with duration: [{}] minutes. It is less than minimal slot: [{}] minutes",
          meetingDuration, minimalTimeSlot);
      throw new BadRequestException(String.format(
          "Meeting cannot be less than %s minutes", minimalTimeSlot));
    }
    try {
      return calendarDao.createMeeting(meeting);
    } catch (Exception e) {
      log.error("Cannot create meeting {}", meeting.getTitle(), e);
      throw new InternalServiceException(String.format(
          "Cannot create meeting %s", meeting.getTitle()), e);
    } finally {
      log.info("Meeting [{}] created", meeting.getTitle());
    }
  }

  @Override
  @Transactional
  public Meeting getMeeting(String requestor, long meetingId) {
    log.info("Retrieving details for meeting [{}]", meetingId);
    Optional<Meeting> meeting;
    boolean permitted;
    try {
      permitted = calendarDao.isPermitted(requestor, meetingId);
    } catch (Exception e) {
      log.error("Cannot retrieve meeting with id = {}", meetingId, e);
      throw new InternalServiceException(String.format(
          "Cannot retrieve meeting with id = %s", meetingId), e);
    }
    if (!permitted) {
      throw new PermissionException(String.format(
          "User [%s] is not permitted to view meeting [%s]", requestor, meetingId
      ));
    }
    try {
      meeting = calendarDao.getMeetingDetails(meetingId);
    } catch (Exception e) {
      log.error("Cannot retrieve meeting with id = {}", meetingId, e);
      throw new InternalServiceException(String.format(
          "Cannot retrieve meeting with id = %s", meetingId), e);
    }
    return meeting.orElseThrow(() ->
        new NotFoundException(String.format(
            "Meeting with id = [%s] was not found", meetingId)));
  }

  @Override
  @Transactional
  public Collection<MeetingSummary> getCalendarForUser(String requestor, String user,
                                                       LocalDateTime fromTime, LocalDateTime toTime) {
    log.info("User [{}] requested calendar for User [{}] from [{}] to [{}]",
        requestor, user, fromTime, toTime);
    Optional<User> one;
    try {
      one = userDao.findOne(user);
    } catch (Exception e) {
      log.error("Cannot retrieve calendar for user {}", user, e);
      throw new InternalServiceException(String.format(
          "Cannot retrieve calendar for user %s", user), e);
    }
    if (one.isEmpty()) {
      log.warn("User [{}] not found", user);
      throw new NotFoundException(String.format("User %s not found", user));
    }
    try {
      if (user.equals(requestor)) {
        return userCalendarAdapter.getPersonalCalendar(user, fromTime, toTime);
      } else {
        return userCalendarAdapter.getRestrictedCalendar(user, fromTime, toTime);
      }
    } catch (Exception e) {
      log.error("Cannot retrieve calendar for user {}", user, e);
      throw new InternalServiceException(String.format(
          "Cannot retrieve calendar for user %s", user), e);
    }
  }

  @Override
  @Transactional
  public void respondToMeeting(MeetingResponse meetingResponse) {
    try {
      calendarDao.respondToMeeting(meetingResponse);
      // send notification to meeting organizer
    } catch (Exception e) {
      log.error("Cannot save response to meeting {}", meetingResponse, e);
      throw new InternalServiceException(String.format(
          "Cannot save response to meeting %s", meetingResponse), e);
    } finally {
      log.info("User [{}] respond to meeting [{}] with [{}]",
          meetingResponse.getUser(),
          meetingResponse.getMeetingId(), meetingResponse.getResponse());
    }
  }

  @Override
  public TimeSlot suggestMeetingSlot(MeetingSlotRequest slotRequest) {
    log.info("Start finding free [{}] minutes slot for participants {}", slotRequest.getDurationMin(),
        slotRequest.getParticipants());

    try {
      long slot = slotRequest.getDurationMin();
      LocalDateTime now = currentDateProvider.getNow();
      LocalDateTime from = now.truncatedTo(ChronoUnit.HOURS)
          .plusMinutes(minimalTimeSlot * (now.getMinute() / minimalTimeSlot));
      LocalDateTime to = LocalDate.now().atTime(LocalTime.MAX);

      while (true) {

        Collection<TreeMap<LocalDateTime, LocalDateTime>> participantsBusySlots = new ArrayList<>();

        for (String participant : slotRequest.getParticipants()) {
          TreeMap<LocalDateTime, LocalDateTime> participantBusySlots = new TreeMap<>();
          Collection<MeetingSummary> userCalendar = userCalendarAdapter.getRestrictedCalendar(participant, from, to);
          userCalendar.forEach(ms -> participantBusySlots.put(ms.getFromTime(), ms.getToTime()));
          participantsBusySlots.add(participantBusySlots);
        }

        while (from.isBefore(to)) {
          boolean found = true;
          for (TreeMap<LocalDateTime, LocalDateTime> busySlots : participantsBusySlots) {
            LocalDateTime prev = busySlots.floorKey(from);
            LocalDateTime next = busySlots.ceilingKey(from);

            if ((prev != null && from.isBefore(busySlots.get(prev)))
                || (next != null && next.isBefore(from.plusMinutes(slot)))) {
              found = false;
              from = from.plusMinutes(minimalTimeSlot);
              break;
            }
          }
          if (found) {
            return TimeSlot.builder()
                .from(from)
                .to(from.plusMinutes(slot))
                .build();
          }
        }
        to = to.plusDays(1);
      }
    } catch (Exception e) {
      throw new InternalServiceException(String.format(
          "Cannot find free %s minutes slot for participants %s",
          slotRequest.getDurationMin(), slotRequest.getParticipants()), e);
    }
  }
}
