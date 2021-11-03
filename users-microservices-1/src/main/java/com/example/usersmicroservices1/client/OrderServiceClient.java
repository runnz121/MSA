package com.example.usersmicroservices1.client;

import com.example.usersmicroservices1.vo.ResponseOrder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name="order-service")
public interface OrderServiceClient {

    //order-service controller의 getmapping으로 된 getordASER의 반환값을 그대로 반환
   // @GetMapping("/order-service/{userId}/orders")// 정상주소 실질적인 endpoint를 지정
    @GetMapping("/order-service/{userId}/orders") //잘못된 주소 -> 일부러 에러 발생
    List<ResponseOrder> getOrders(@PathVariable String userId);

}
