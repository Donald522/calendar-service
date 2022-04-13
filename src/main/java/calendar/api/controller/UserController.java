package calendar.api.controller;

import calendar.api.UserApi;
import calendar.api.dto.UserDto;
import calendar.service.UserService;
import calendar.service.converter.UserConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class UserController implements UserApi {

  private final UserConverter converter;
  private final UserService userService;

  @Override
  public ResponseEntity<Object> createUser(UserDto userDto) {
    long id = userService.create(converter.toModel(userDto));
    return ResponseEntity.created(URI.create(String.format("/users/%s", id))).build();
  }
}
