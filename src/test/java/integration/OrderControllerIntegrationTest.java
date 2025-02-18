package integration;

import br.com.ambev.Application;
import br.com.ambev.data.Order;
import br.com.ambev.data.ProductItem;
import br.com.ambev.enums.Status;
import br.com.ambev.repositories.OrderRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
public class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @Before
    public void setup() {
        orderRepository.deleteAll().block();
    }

    @Test
    public void shouldReturnOrdersByStatus() throws Exception {
        Order o1 = Order.builder().build();
        o1.setIdExternal("111");
        o1.setStatus(Status.OPENED);
        o1.setTotal(BigDecimal.valueOf(100));
        orderRepository.save(o1).block();

        Order o2 = Order.builder().build();
        o2.setIdExternal("222");
        o2.setStatus(Status.CALCULATED);
        o2.setTotal(BigDecimal.valueOf(200));
        orderRepository.save(o2).block();

        // when: chamar GET /orders?status=OPENED
        mockMvc.perform(get("/orders")
                        .param("status", "OPENED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].idExternal").value("111"))
                .andExpect(jsonPath("$[0].status").value("OPENED"))
                .andExpect(jsonPath("$[0].total").value(100));
    }

    @Test
    public void shouldReturnSingleOrderWithItems() throws Exception {
        ProductItem pi1 = ProductItem.builder()
                .price(BigDecimal.valueOf(10))
                .productId("p1")
                .quantity(2)
                .productName("Name1")
                .build();
        Order o1 = Order.builder().build();
        o1.setIdExternal("aaa");
        o1.setStatus(Status.CALCULATED);
        o1.setItems(List.of(pi1));
        o1.setTotal(BigDecimal.valueOf(20));
        orderRepository.save(o1).block();

        mockMvc.perform(get("/orders/{idExternal}", "aaa"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idExternal").value("aaa"))
                .andExpect(jsonPath("$.status").value("CALCULATED"))
                .andExpect(jsonPath("$.items[0].productId").value("p1"))
                .andExpect(jsonPath("$.items[0].price").value(10))
                .andExpect(jsonPath("$.total").value(20));
    }
}
