package calendar.api.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserDto {

  String name;
  String surname;
  String email;
  String password;

  @JsonCreator
  public UserDto(@JsonProperty(value = "name", required = true) String name,
                 @JsonProperty(value = "surname", required = true) String surname,
                 @JsonProperty(value = "email", required = true) String email,
                 @JsonProperty(value = "password", required = true) String password) {
    this.name = name;
    this.surname = surname;
    this.email = email;
    this.password = password;
  }
}
