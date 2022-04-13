package calendar.api;

import calendar.api.dto.UserDto;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/users")
public interface UserApi {

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<Object> createUser(@RequestBody UserDto userDto);
}
