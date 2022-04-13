package calendar.dao;

import calendar.service.model.User;

import java.util.Optional;

public interface UserDao {

  void create(User user);

  Optional<User> findOne(String email);
}
