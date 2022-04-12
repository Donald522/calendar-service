package calendar.dao;

import calendar.service.model.User;

public interface UserDao {

  void create(User user);

  User findOne(String email);
}
