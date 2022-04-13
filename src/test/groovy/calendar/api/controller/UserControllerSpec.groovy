package calendar.api.controller

import calendar.api.dto.UserDto
import calendar.api.handler.UserControllerHandler
import calendar.service.UserService
import calendar.service.converter.UserConverter
import calendar.service.exception.AlreadyExistsException
import calendar.service.exception.InternalServiceException
import calendar.service.model.User
import groovy.json.JsonBuilder
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification
import spock.lang.Subject

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@Subject(UserController)
class UserControllerSpec extends Specification {

  def "Should respond with 200 when new user created"() {
    given:
    def converter = new UserConverter()
    def service = Mock(UserService)
    def controller = new UserController(converter, service)
    def server = MockMvcBuilders
        .standaloneSetup(controller)
        .setControllerAdvice(UserControllerHandler)
        .build()

    and:
    def userDto = UserDto.builder()
        .name('A')
        .surname('T')
        .email('e.mail@ex.ru')
        .build()

    when:
    def request = post('/users')
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .content(new JsonBuilder(userDto).toString())

    and:
    server.perform(request)
        .andExpect(status().isCreated())

    then:
    interaction {
      def user = User.builder()
          .name(userDto.getName())
          .surname(userDto.getSurname())
          .email(userDto.getEmail())
          .build()
      1 * service.create(user)
    }
  }

  def "Should respond with 409 when user already exists"() {
    given:
    def converter = new UserConverter()
    def service = Mock(UserService)
    def controller = new UserController(converter, service)
    def server = MockMvcBuilders
        .standaloneSetup(controller)
        .setControllerAdvice(UserControllerHandler)
        .build()

    and:
    def userDto = UserDto.builder()
        .name('A')
        .surname('T')
        .email('e.mail@ex.ru')
        .build()

    when:
    def request = post('/users')
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .content(new JsonBuilder(userDto).toString())

    and:
    server.perform(request)
        .andExpect(status().isConflict())

    then:
    interaction {
      1 * service.create(_ as User) >> { throw new AlreadyExistsException() }
    }
  }

  def "Should respond with 500 when internal error happened"() {
    given:
    def converter = new UserConverter()
    def service = Mock(UserService)
    def controller = new UserController(converter, service)
    def server = MockMvcBuilders
        .standaloneSetup(controller)
        .setControllerAdvice(UserControllerHandler)
        .build()

    and:
    def userDto = UserDto.builder()
        .name('A')
        .surname('T')
        .email('e.mail@ex.ru')
        .build()

    when:
    def request = post('/users')
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .content(new JsonBuilder(userDto).toString())

    and:
    server.perform(request)
        .andExpect(status().isInternalServerError())

    then:
    interaction {
      1 * service.create(_ as User) >> { throw new InternalServiceException('', new RuntimeException()) }
    }
  }
}