package calendar.service.converter;

import calendar.api.dto.UserDto;
import calendar.service.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserConverter {

  private final PasswordEncoder passwordEncoder;

  public User toModel(UserDto dto) {
    return User.builder()
        .name(dto.getName())
        .surname(dto.getSurname())
        .email(dto.getEmail())
        .password(passwordEncoder.encode(dto.getPassword()))
        .build();
  }

  public UserDto toDto(User model) {
    return UserDto.builder()
        .name(model.getName())
        .surname(model.getSurname())
        .email(model.getEmail())
        .build();
  }
}
