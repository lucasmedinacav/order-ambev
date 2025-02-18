package integration;

import br.com.ambev.Application;
import br.com.ambev.data.Order;
import br.com.ambev.enums.Status;
import br.com.ambev.message.input.ItemMessageInput;
import br.com.ambev.message.input.OrderMessageInput;
import br.com.ambev.repositories.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = Application.class
)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RabbitIntegrationTest {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    void setup() {
        orderRepository.deleteAll();
    }

    @Test
    public void shouldProcessIncomingOrderMessage() throws Exception {

        OrderMessageInput input = new OrderMessageInput();
        input.setIdExternal("EXTERNAL-123");
        ItemMessageInput item = new ItemMessageInput();
        item.setProductId("p1");
        item.setPrice(BigDecimal.valueOf(10));
        item.setQuantity(3);
        input.setItems(List.of(item));

        rabbitTemplate.convertAndSend("orders-queue", input);

        Thread.sleep(2000);

        Order order = orderRepository.findByIdExternal("EXTERNAL-123")
                .block();

        assertNotNull(order);
        assertEquals(Status.OPENED, order.getStatus());
        assertEquals(1, order.getItems().size());
        assertEquals("p1", order.getItems().get(0).getProductId());
    }

}
