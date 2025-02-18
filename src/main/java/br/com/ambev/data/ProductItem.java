package br.com.ambev.data;

import br.com.ambev.message.input.ItemMessageInput;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class ProductItem {
    private String productId;
    private String productName;
    private Integer quantity;
    private BigDecimal price;
}
