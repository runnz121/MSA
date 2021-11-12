package com.example.orderservice.dto;


import lombok.Builder;
import lombok.Data;

/* mariadb의 컬럼 정보와 같아야 한다 */
@Data
@Builder
public class Payload {
    private String order_id;
    private String user_id;
    private String product_id;
    private int qty;
    private int unit_price;
    private int total_price;
}
