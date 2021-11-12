package com.example.catalogservice.messageque;


import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    //카프가 접속 정보가 들어가는 bean
    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
            Map<String, Object> properties = new HashMap<>();
            properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092"); //카프카 접속 아이피
            properties.put(ConsumerConfig.GROUP_ID_CONFIG, "consumerGroupId"); //컨슈머를 그룹핑 하는 그룹을 지정(여러개의 컨슈머가 데이터를 가져갈때 사용할 수 있음)
            properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class); //키를 역직렬화 지정(문자열로 바꿔줌)
            properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class); //값을 역직렬화

            //consumer가 반환하는 값
            return new DefaultKafkaConsumerFactory<>(properties);
    }

    //리스너를 등록
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(){
            ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory
                = new ConcurrentKafkaListenerContainerFactory<>();

            kafkaListenerContainerFactory.setConsumerFactory(consumerFactory()); //위에 선언했던 등록 정보를 갖고와서 리스너로 등록시킴
            return kafkaListenerContainerFactory;
    }
}
