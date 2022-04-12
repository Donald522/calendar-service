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

  @JsonCreator
  public UserDto(@JsonProperty(value = "name", required = true) String name,
                 @JsonProperty(value = "surname", required = true) String surname,
                 @JsonProperty(value = "email", required = true) String email) {
    this.name = name;
    this.surname = surname;
    this.email = email;
  }
}
