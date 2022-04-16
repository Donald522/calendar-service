package calendar.service;

import java.time.LocalDateTime;

public interface CurrentDateProvider {

  LocalDateTime getNow();
}
