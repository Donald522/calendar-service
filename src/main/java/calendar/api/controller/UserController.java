package calendar.api.controller;

import calendar.api.UserApi;
import calendar.api.dto.UserDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

import static java.util.Collections.emptyList;

@Log4j2
@RestController
public class UserController implements UserApi {

    @Override
    public ResponseEntity<Object> createUser(UserDto userDto) {
        log.info("Creating new User: {}", userDto);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Collection<UserDto>> getAllUsers() {
        log.info("Reading all users");
        return ResponseEntity.ok(emptyList());
    }
}
