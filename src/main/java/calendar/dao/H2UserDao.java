package calendar.dao;

import calendar.service.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class H2UserDao implements UserDao {

  private final JdbcTemplate jdbcTemplate;

  @Override
  public void create(User user) {
    String sql = "insert into USERS (name, surname, email) values (?, ?, ?)";
    jdbcTemplate.update(sql, user.getName(), user.getSurname(), user.getEmail());
  }

  @Override
  public User findOne(String email) {
    String sql = "select * from USERS where email = ?";
    return jdbcTemplate.queryForObject(sql,
        (rs, rowNum) -> User.builder()
            .name(rs.getString("name"))
            .surname(rs.getString("surname"))
            .email(rs.getString("email"))
            .build(),
        email);
  }
}
