package com.example.orderservice.controller;


import com.example.orderservice.dto.OrderDto;
import com.example.orderservice.jpa.OrderEntity;
import com.example.orderservice.messageque.KafkaProducer;
import com.example.orderservice.messageque.OrderProducer;
import com.example.orderservice.service.OrderService;
import com.example.orderservice.vo.RequestOrder;
import com.example.orderservice.vo.ResponseOrder;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/order-service")
public class OrderController {


    private Environment env;
    private OrderService orderService;
    //kafka 주입
    private KafkaProducer kafkaProducer;
    //kafka order 전달
    private OrderProducer orderProducer;


    @Autowired
    public OrderController(Environment env, OrderService orderService, KafkaProducer kafkaProducer, OrderProducer orderProducer ) {
        this.env = env;
        this.orderService = orderService;
        this.kafkaProducer = kafkaProducer;
        this.orderProducer = orderProducer;
    }

    @GetMapping("/health_check")
    public String status(){
        return String.format("It's working in order Service on PORT %s", env.getProperty("local.server.port"));
    }


    //kafka 에 메시지를 전달하는 코드 추가
    @PostMapping("/{userId}/orders")
    public ResponseEntity<ResponseOrder> createOrder(@PathVariable("userId") String userId, @RequestBody RequestOrder order){ //requestUser를 받아서 파라미터로 받음
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT); //mapper환경설정해주고


        OrderDto orderDto = mapper.map(order, OrderDto.class); //모델 멥퍼로 request user -> userdto로 변환
        orderDto.setUserId(userId);

//        /* JPA */
        //서킷 브레이커 테스트
        OrderDto createOrder = orderService.createOrder(orderDto); //서비스로 전달
        ResponseOrder responseOrder = mapper.map(orderDto, ResponseOrder.class); //response 객체로 반환해서 body에 추가하여 보냄

        /* kafka */
        //사용자가 전달했던 주문 정보를 갖고옴
//        orderDto.setOrderId(UUID.randomUUID().toString());
//        orderDto.setTotalPrice(order.getQty() * order.getUnitPrice());

        /* send this order to the kafka */
        //서킷 브레이커 테스트 를 위한 카프카 주석코드 주석처리
//        kafkaProducer.send("example-catalog-topic", orderDto);
//        orderProducer.send("orders", orderDto);//->kafaka로 데이터를 전달할 메시지 추가 -> 사용자 주문정보를 토픽으로 전달
//        ResponseOrder responseOrder = mapper.map(orderDto, ResponseOrder.class);


        return ResponseEntity.status(HttpStatus.CREATED).body(responseOrder);
    }





    @GetMapping("/{userId}/orders")
    public ResponseEntity<List<ResponseOrder>> getOrder(@PathVariable("userId") String userId){
        Iterable<OrderEntity> orderList = orderService.getOrdersByUserId(userId);

        List<ResponseOrder> result = new ArrayList<>();
        orderList.forEach(v -> {
            result.add(new ModelMapper().map(v, ResponseOrder.class));
        });

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

}
