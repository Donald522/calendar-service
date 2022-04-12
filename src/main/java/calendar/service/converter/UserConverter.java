package calendar.service.converter;

import calendar.api.dto.UserDto;
import calendar.service.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserConverter {

  public User toModel(UserDto dto) {
    return User.builder()
        .name(dto.getName())
        .surname(dto.getSurname())
        .email(dto.getEmail())
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
