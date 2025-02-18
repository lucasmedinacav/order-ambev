package br.com.ambev.message.input;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ItemMessageInput {
    private String productId;
    private Integer quantity;
    private BigDecimal price;
    private String productName;
}
