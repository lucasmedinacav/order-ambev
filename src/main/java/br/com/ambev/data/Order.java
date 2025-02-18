package br.com.ambev.data;

import br.com.ambev.enums.Status;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@Document(collection = "orders")
public class Order {
    @Id
    private String id;

    @Indexed(unique = true)
    private String idExternal;

    private List<ProductItem> items;
    private BigDecimal total;
    private Status status;
}
