package com.example.firstservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/first-service")
@Slf4j
public class Controller {
    //변수에 바로 Autowired를 쓰지말고
    //이건 yml 파일의 값을 갖고오는 방법 중 하나이다.
    Environment env;

    //생성자를 통해 주입 후 @Autowired를 통해 빈 등록
    @Autowired
    public Controller(Environment env){
        this.env = env;
    }

    @GetMapping("/welcome")
    public String welcome()
    {
        return "Welcome first service";
    }

    @GetMapping("/message")
    public String message(@RequestHeader("first-request") String header){
        log.info(header);
        return "Hello first service";
    }

    @GetMapping("/check")
    public String check(HttpServletRequest request){
        log.info("Server port= {}", request.getServerPort());
        return String.format("This message indicate First service PORT %S",
                env.getProperty("local.server.port"));//getProperty()안에는
        //우리가 갖고오고 싶은 값을 명시해주면 된다. 우리는 포트 번호를 갖고오고 싶음
    }
}
