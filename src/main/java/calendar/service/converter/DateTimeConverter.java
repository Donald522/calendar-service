package calendar.service.converter;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class DateTimeConverter {

  private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

  public LocalDateTime parseDate(String date) {
    return LocalDateTime.parse(date, formatter);
  }

  public String formatDate(LocalDateTime dateTime) {
    return dateTime.format(formatter);
  }
}
