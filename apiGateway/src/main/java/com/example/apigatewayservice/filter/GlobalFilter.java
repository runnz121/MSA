package com.example.apigatewayservice.filter;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;

//reactive -> webflux를 사용하는 클래스에 포함됨
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class GlobalFilter extends AbstractGatewayFilterFactory<GlobalFilter.Config> {

    public GlobalFilter(){
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain)-> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

//            //pre필터 적용
//            log.info("Custom PRE filter baseMessage: request id-> {}", config.getBaseMessage());

            log.info("Global Filter baseMessage: {}, {}", config.getBaseMessage(), request.getRemoteAddress())
;            if (config.isPreLogger()){
                log.info("Global Filter start: request id -> {}", request.getId());
            }


            //post 필터 적용
            return chain.filter(exchange).then(Mono.fromRunnable(()-> {
                if (config.isPostLogger()){
                    log.info("Global Filter start: request id -> {}", response.getStatusCode());
                }
            }));
        });
    }

    @Data
    public static class Config {
        private String baseMessage;
        private boolean preLogger;
        private boolean postLogger;
    }
}
