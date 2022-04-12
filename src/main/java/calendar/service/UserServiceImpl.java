package calendar.service;

import calendar.dao.UserDao;
import calendar.service.exception.AlreadyExistsException;
import calendar.service.exception.InternalServiceException;
import calendar.service.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Log4j2
@Component
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserDao userDao;

  @Override
  public void create(User user) {
    User one;
    try {
      one = userDao.findOne(user.getEmail());
    } catch (Exception e) {
      throw new InternalServiceException(String.format(
          "Failed to create user %s", user.getEmail()), e);
    }
    if (Objects.nonNull(one)) {
      throw new AlreadyExistsException(String.format("Login %s already exists", user.getEmail()));
    }
    try {
      userDao.create(user);
    } catch (Exception e) {
      throw new InternalServiceException(String.format(
          "Failed to create user %s", user.getEmail()), e);
    }
  }
}
