package com.example.catalogservice.messageque;

import com.example.catalogservice.jpa.CatalogEntity;
import com.example.catalogservice.jpa.CatalogRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class KafkaConsumer {
    CatalogRepository repository;

    @Autowired
    public KafkaConsumer(CatalogRepository repository){
        this.repository = repository;
    }

    //연결할 토픽을 지정
    @KafkaListener(topics = "example-catalog-topic")
    public void updateQty(String kafkaMessage){
        log.info("kafka Message: -> " + kafkaMessage);

        //직렬화된 메세지를 order-service에서 받아옴으로 이 메시지를 보여주기 위해선 역직렬화가 필요하다
        //카프카 메시지가 string 형태로 들어오지만 이를 jsontype으로 변경해서 사용할것
        Map<Object, Object> map = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            map = mapper.readValue(kafkaMessage, new TypeReference<Map<Object, Object>>() {});
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
        }

        //productid는 객체로 받아올 것이기 때문에 이를 문자열로 변환, 이값을 리포에서 찾음
        CatalogEntity entity = repository.findByProductId((String)map.get("productId"));
        if (entity != null){
            entity.setStock(entity.getStock() - (Integer)map.get("qty"));
            repository.save(entity);
        }
    }
}
