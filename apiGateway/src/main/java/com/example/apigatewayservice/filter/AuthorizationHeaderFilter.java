package com.example.apigatewayservice.filter;


import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.boot.autoconfigure.couchbase.CouchbaseProperties;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {

    Environment env;

    public AuthorizationHeaderFilter(Environment env){
        super(Config.class);
        this.env = env;
    }

    public static class Config {

    }


    //다른 서버에서 받은 토큰을 확인하고 전달
    //login -> token을 받음 -> users에 토큰과 함께 요청 -> 이 토큰은 헤더에 있음
    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain)-> {
            ServerHttpRequest request = exchange.getRequest();

            //헤더값에서 인증에관한 값이 있는지를 확인함 -> 인증헤더가 없는 경우
            //"Authorizaiton"이라는 글자가 있는지 본다!!!
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)){
                return onError(exchange, "no autorization header", HttpStatus.UNAUTHORIZED);
            }

            //인증헤더가 있는 경우 -> 반환값은 배열형태이기 때문에 0번째 데이터를 갖고온다고 적음
            //이 객체에는 jwt토큰값이 있을 것이다.
            String authorizationHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);

            //토큰은 string값으로 들어옴 -> Bearer dasasdfasd 이런식으로 들어옴으로 이 Bearer부분을 없에주고 토큰값만 비교
            String jwt = authorizationHeader.replace("Bearer", "");

            if(!isJwtValid(jwt)) {
                return onError(exchange, "Jwt token is not valid", HttpStatus.UNAUTHORIZED);
            }


            return chain.filter(exchange);
        });
    }

    private boolean isJwtValid(String jwt) {
        boolean returnValue = true;

        String subject = null;

        try{
            //복호화 처리
            subject = Jwts.parser().setSigningKey(env.getProperty("token.secret"))
                    .parseClaimsJws(jwt).getBody() //파싱하고 그 안의 값에서
                    .getSubject(); //스트링값을 추출(인코딩된 값을 디코딩한 것)
        } catch (Exception ex) {
            returnValue = false;
        }

        if (subject == null || subject.isEmpty()){
            returnValue = false;
        }

        return returnValue;

    }

    //webflux라 mono로 반환(단일값으로 반환한다는 뜻) <-> 단일 값이 아니면  flux임
    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {

        //flux라서 servlet이 아니다
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);

        log.error(err);
        return response.setComplete();
    }
}

