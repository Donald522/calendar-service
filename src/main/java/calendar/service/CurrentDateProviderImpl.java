package calendar.service;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CurrentDateProviderImpl implements CurrentDateProvider {

  @Override
  public LocalDateTime getNow() {
    return LocalDateTime.now();
  }
}
