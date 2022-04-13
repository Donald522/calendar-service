package calendar.dao;

import calendar.service.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class H2UserDao implements UserDao {

  private final JdbcTemplate calendarJdbcTemplate;

  @Override
  public void create(User user) {
    String sql = "insert into USERS (name, surname, email) values (?, ?, ?)";
    calendarJdbcTemplate.update(sql, user.getName(), user.getSurname(), user.getEmail());
  }

  @Override
  public Optional<User> findOne(String email) {
    String sql = "select * from USERS where email = ?";
    try {
      return calendarJdbcTemplate.queryForObject(sql,
          (rs, rowNum) -> Optional.of(mapUserResult(rs)),
          email);
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  private User mapUserResult(ResultSet rs) throws SQLException {
    return User.builder()
        .name(rs.getString("name"))
        .surname(rs.getString("surname"))
        .email(rs.getString("email"))
        .build();
  }
}
