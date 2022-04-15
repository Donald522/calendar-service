package calendar.api.security;

import calendar.dao.UserDao;
import calendar.service.exception.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SimpleUserDetailService implements UserDetailsService {

  private final UserDao userDao;

  @Override
  public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
    Optional<calendar.service.model.User> user = userDao.findOne(login);
    if (user.isEmpty()) {
      throw new AuthException("Email or password incorrect");
    }
    GrantedAuthority authority = new SimpleGrantedAuthority("USER");
    return new User(login, user.get().getPassword(), Collections.singletonList(authority));
  }
}
