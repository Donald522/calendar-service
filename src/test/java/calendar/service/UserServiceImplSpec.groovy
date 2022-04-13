package calendar.service

import calendar.dao.UserDao
import calendar.service.exception.AlreadyExistsException
import calendar.service.exception.InternalServiceException
import calendar.service.model.User
import spock.lang.Specification
import spock.lang.Subject

@Subject(UserServiceImpl)
class UserServiceImplSpec extends Specification {

  def "Should return nothing for successfully created user"() {
    given:
    def dao = Stub(UserDao) {
      findOne(_) >> Optional.empty()
    }
    def service = new UserServiceImpl(dao)

    def user = User.builder()
        .name('A')
        .surname('T')
        .email('A.T@Inc.com')
        .build()

    when:
    service.create(user)

    then:
    noExceptionThrown()
  }

  def "Should throw AlreadyExistsException if user already exists"() {
    given:
    def user = User.builder()
        .name('A')
        .surname('T')
        .email('A.T@Inc.com')
        .build()

    def dao = Stub(UserDao) {
      findOne(user.email) >> Optional.of(user)
    }
    def service = new UserServiceImpl(dao)

    when:
    service.create(user)

    then:
    thrown(AlreadyExistsException)
  }

  def "Should wrap any exception into InternalServiceException"() {
    given:
    def user = User.builder()
        .name('A')
        .surname('T')
        .email('A.T@Inc.com')
        .build()

    def dao = Stub(UserDao) {
      findOne(user.email) >> { throw new RuntimeException('DB is down') }
    }
    def service = new UserServiceImpl(dao)

    when:
    service.create(user)

    then:
    thrown(InternalServiceException)
  }
}
