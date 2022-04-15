package calendar.dao;

import calendar.service.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class H2UserDao implements UserDao {

  private final JdbcTemplate calendarJdbcTemplate;

  @Override
  public long create(User user) {
    String insertSql = "insert into USERS (id, name, surname, email, password) values (users_seq.nextval, ?, ?, ?, ?)";

    GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

    String id_column = "id";

    calendarJdbcTemplate.update(con -> {
      PreparedStatement ps = con.prepareStatement(insertSql, new String[]{id_column});
      ps.setString(1, user.getName());
      ps.setString(2, user.getSurname());
      ps.setString(3, user.getEmail());
      ps.setString(4, user.getPassword());
      return ps;
    }, keyHolder);

    Integer id = (Integer) keyHolder.getKeys().get(id_column);
    return id.longValue();
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
        .password(rs.getString("password"))
        .build();
  }
}
