package com.example.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;


/*
source connect -> sink connector 로 데이터를 보내고자 할때 어떤 정보가 저장될지 정하는 필드 클래스
 */
@Data
@AllArgsConstructor
public class Field {
    private String type;
    private boolean optional;
    private String field;
}
