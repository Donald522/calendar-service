package calendar.dao;

import calendar.service.model.User;

import java.util.Optional;

public interface UserDao {

  long create(User user);

  Optional<User> findOne(String email);
}
