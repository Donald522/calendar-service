package calendar.app.configuration;

import calendar.api.security.MyBasicAuthenticationEntryPoint;
import calendar.api.security.SimpleUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
public class CalendarServiceSecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  private SimpleUserDetailService userDetailsService;

  @Autowired
  private MyBasicAuthenticationEntryPoint authenticationEntryPoint;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.headers().frameOptions().disable();

    http
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and().csrf().disable()
        .authorizeRequests()
        .antMatchers("/h2-console/**").permitAll()
        .antMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
        .antMatchers("/users").permitAll()
        .antMatchers("/meetings**").hasRole("USER")
        .anyRequest().authenticated()
        .and()
        .httpBasic();
  }

  @Override
  public void configure(WebSecurity web)  {
    web.ignoring().antMatchers("/v3/api-docs",
        "/swagger-ui.html",
        "/swagger-ui/**");
  }

  @Override
  public void configure(AuthenticationManagerBuilder builder) throws Exception {
    builder.userDetailsService(userDetailsService);
  }
}