package calendar.service;

import calendar.dao.UserDao;
import calendar.service.exception.AlreadyExistsException;
import calendar.service.exception.InternalServiceException;
import calendar.service.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Log4j2
@Component
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserDao userDao;

  @Override
  @Transactional
  public long create(User user) {
    Optional<User> one;
    log.info("Creating new User: {}", user);
    try {
      one = userDao.findOne(user.getEmail());
    } catch (Exception e) {
      log.error("Failed to create user {}", user, e);
      throw new InternalServiceException(String.format(
          "Failed to create user %s", user.getEmail()), e);
    }
    if (one.isPresent()) {
      log.info("User {} already exists", user);
      throw new AlreadyExistsException(String.format("Login %s already exists", user.getEmail()));
    }
    try {
      long id = userDao.create(user);
      log.info("User {} created with id = {}", user, id);
      return id;
    } catch (Exception e) {
      log.error("Failed to create user {}", user, e);
      throw new InternalServiceException(String.format(
          "Failed to create user %s", user.getEmail()), e);
    }
  }
}
