package com.example.usersmicroservices1.security;

import com.example.usersmicroservices1.Service.UserService;
import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.servlet.Filter;

@Configuration //이렇게 등록하면 다른 bean들보다 우선선위를 갖고 작동된다.
@EnableWebSecurity //security용도로 사용
public class WebSecurity extends WebSecurityConfigurerAdapter {

    private UserService userService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private Environment env;

    @Autowired
    public WebSecurity(UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder, Environment env){
        this.userService=userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.env =env;
    }



    //권한에 관련한 설정(http security)
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        //http.authorizeRequests().antMatchers("/users/**").permitAll();
        http.authorizeRequests().antMatchers("/**","/users/**","/actuator/**").permitAll(); //actuator는 모두 통과
        http.authorizeRequests().antMatchers("/**") //모든 코드를 통과시키지 않음
                .access("hasIpAddress('127.0.0.1') or hasIpAddress('192.168.219.108')")
//                .hasIpAddress("127.0.0.1")//아이피 제약조건 설정
//                .hasIpAddress("192.168.219.108")
                .and()
                        .addFilter(getAuthenticationFilter()); //필터를 추가 -> 이 필터를 통과하면 그제서야 서비스 이용가능

        http.headers().frameOptions().disable(); //frame옵션을 disable함으로서 h2 console사용가능

    }

    //여기서 AuthenticationFilter는 우리가 만든 필터인데, 이 클래스는 어차피 filter를 상속 받았기 때문에 상관없다.
    private AuthenticationFilter getAuthenticationFilter() throws Exception
    {

        //authenticationFilter로 반환하기 위해 인스턴스 생성
        AuthenticationFilter authenticationFilter
                = new AuthenticationFilter(authenticationManager(), userService, env); //default 생성자에 매개변수로 AuthenticationFilter.class에서
                //생성자 주입을 구현한 값을 전달해서 넣어줘야 한다.

        // AuthenticationFilter.class 에서 생성자를 통해 이미 set값을 지정해주었음으로 필요 없게되었다.
  //      authenticationFilter.setAuthenticationManager(authenticationManager()); //->인증 처리를 위해 manager를 사용

        return authenticationFilter;
    }


    //인증 설정을 위함함  (authenticationManager builder)
    //select pwd from users where email =?
    //db_pwd(encrypted) == input_pwd(encrypted)
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //userdetailservice -> 사용자 이름과 비밀번호를 갖고옴(select부분을 이게 처리)
        auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);
    }
}
