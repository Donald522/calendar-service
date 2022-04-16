package calendar.dao


import calendar.configuration.UserDaoTestConfig
import calendar.service.model.User
import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener
import com.github.springtestdbunit.annotation.DatabaseSetup
import groovy.sql.Sql
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener
import spock.lang.Specification
import spock.lang.Subject

import javax.sql.DataSource

@Subject(H2UserDao)
@ContextConfiguration(classes = [UserDaoTestConfig])
@TestExecutionListeners([TransactionDbUnitTestExecutionListener, DependencyInjectionTestExecutionListener])
class H2UserDaoSpec extends Specification {

  @Autowired
  private UserDao userDao

  @Autowired
  private DataSource dataSource

  private Sql db

  def setup() {
    db = Sql.newInstance(dataSource)
  }

  @DatabaseSetup(value = "classpath:database/clean_db.xml", connection = "dbUnitDatabaseConnection")
  def "Should create new user with generated id"() {
    given:
    def user = User.builder()
        .name('Anton')
        .surname('Ivanov')
        .email('anton.ivanov@test.tu')
        .password('qwerty')
        .build()

    when:
    def id = userDao.create(user)

    then:
    id > 0
    def res = db.rows("select * from users where id = ${id}")
    res[0]['ID'] == id
    res[0]['NAME'] == user.name
    res[0]['SURNAME'] == user.surname
    res[0]['EMAIL'] == user.email
    res[0]['PASSWORD'] == user.password
  }
}
