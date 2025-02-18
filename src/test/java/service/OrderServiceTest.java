package service;

import br.com.ambev.data.Order;
import br.com.ambev.enums.Status;
import br.com.ambev.repositories.OrderRepository;
import br.com.ambev.services.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    public void shouldFindOrdersByStatus() {
        Order order1 = Order.builder().build();
        order1.setId("123");
        order1.setIdExternal("ABC");
        order1.setStatus(Status.OPENED);

        when(orderRepository.findByStatus(Status.OPENED)).thenReturn(Flux.just(order1));

        List<Order> orders = orderService.findByStatus(Status.OPENED)
                .collectList()
                .block();

        assertNotNull(orders);
        assertEquals(1, orders.size());
        assertEquals("ABC", orders.get(0).getIdExternal());
        verify(orderRepository).findByStatus(Status.OPENED);
    }

    @Test
    public void shouldReturnSingleOrderByIdExternal() {
        Order order = Order.builder().build();
        order.setIdExternal("XYZ");
        order.setStatus(Status.CALCULATED);

        when(orderRepository.findByIdExternal("XYZ")).thenReturn(Mono.just(order));

        Order result = orderService.findByIdExternal("XYZ")
                .block();

        assertNotNull(result);
        assertEquals(Status.CALCULATED, result.getStatus());
        verify(orderRepository).findByIdExternal("XYZ");
    }
}
