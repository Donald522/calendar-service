package calendar.service.model;

import lombok.Builder;
import lombok.Value;
import lombok.With;

@With
@Value
@Builder
public class User {

  String name;
  String surname;
  String email;
}
