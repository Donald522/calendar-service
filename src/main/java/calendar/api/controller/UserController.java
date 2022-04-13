package calendar.api.controller;

import calendar.api.UserApi;
import calendar.api.dto.UserDto;
import calendar.service.UserService;
import calendar.service.converter.UserConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

import static java.util.Collections.emptyList;

@RestController
@RequiredArgsConstructor
public class UserController implements UserApi {

  private final UserConverter converter;
  private final UserService userService;

  @Override
  public ResponseEntity<Object> createUser(UserDto userDto) {
    userService.create(converter.toModel(userDto));
    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<Collection<UserDto>> getAllUsers() {
    return ResponseEntity.ok(emptyList());
  }
}
