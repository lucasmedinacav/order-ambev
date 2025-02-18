package br.com.ambev.converters;

import br.com.ambev.data.Order;
import br.com.ambev.data.ProductItem;
import br.com.ambev.enums.Status;
import br.com.ambev.message.input.OrderMessageInput;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderConverter {
    public Order convertToOrder(OrderMessageInput orderInput) {
        List<ProductItem> productItems = orderInput.getItems()
                .stream()
                .map(item -> ProductItem.builder()
                        .productId(item.getProductId())
                        .productName(item.getProductName())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .build())
                .collect(Collectors.toList());

        BigDecimal total = productItems.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return Order.builder()
                .idExternal(orderInput.getIdExternal())
                .items(productItems)
                .total(total)
                .status(Status.OPENED)
                .build();
    }
}
