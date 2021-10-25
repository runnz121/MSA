package com.example.usersmicroservices1.security;

import com.example.usersmicroservices1.Service.UserService;
import com.example.usersmicroservices1.dto.UserDto;
import com.example.usersmicroservices1.vo.RequestLogin;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

@Slf4j
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {


    private UserService userService;
    private Environment env;


    public AuthenticationFilter(AuthenticationManager authenticationManager,
                                UserService userService,
                                Environment env) {
        super.setAuthenticationManager(authenticationManager); //부모클래스가 갖고있는 이 객체를 여기서 set으로 바꿔 직접 전달할 수 있음
        this.userService = userService;
        this.env = env;
    }



    //로그인 요청이 들어왔을시 처리하는 메소드
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {


        try{
            //어떠한 값이 들어왔을때 그 값을 java.class타입으로 변경시켜 주기 위해 사용
            //post형태로 전달되는 것은 RequestParam으로 전달 받을 수가 없다!
            //따라서 inputstreamd으로 받으면 수작업으로 처리할 수 있다.
            RequestLogin credentials = new ObjectMapper().readValue(request.getInputStream(), RequestLogin.class);


            //사용자 이름과 패스워드를 security가 사용하도록 token으로 바꿔주는 작업이 필요함
            //arraylist는 권한용
            //작성한 것을 인증을 얻기 위해 authenticaftionmanager로 보냄(get) -> 아이디와 패스워드를 비교해줌
            return getAuthenticationManager()
                    .authenticate(
                    new UsernamePasswordAuthenticationToken(
                            credentials.getEmail(),
                            credentials.getPassword(),
                            new ArrayList<>()
                    )
            );
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }


    //성공시 처리할 메소드
    //로그인 성공시 어떠한 처리를 해줄 것인지를 정의(ex토큰 발급, 토큰 완료시간, 반환값 등등)
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {

        //생성된 user객체에서 정보를 빼옴 -> 캐스팅해서 빼옴
        String userName = ((User)authResult.getPrincipal()).getUsername();
        UserDto userDetails = userService.getUserDetailsByEmail(userName);


        //jwt token생성
        String token = Jwts.builder()
                .setSubject(userDetails.getUserId())
                .setExpiration(new Date(System.currentTimeMillis() +
                        Long.parseLong(env.getProperty("token.expiration_time"))))
                .signWith(SignatureAlgorithm.HS512, env.getProperty("token.secret"))
                .compact();

        response.addHeader("token", token);
        response.addHeader("userId", userDetails.getUserId());

    }
}
