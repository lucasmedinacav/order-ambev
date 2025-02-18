package br.com.ambev.services;

import br.com.ambev.config.RabbitConfig;
import br.com.ambev.converters.OrderConverter;
import br.com.ambev.data.Order;
import br.com.ambev.message.input.OrderMessageInput;
import br.com.ambev.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderListener {

    private final OrderRepository orderRepository;
    private final List<Order> buffer = Collections.synchronizedList(new ArrayList<>());
    private static final int BATCH_SIZE = 1;
    private final OrderConverter orderConverter;

    @RabbitListener(queues = RabbitConfig.QUEUE_ORDERS)
    public void receiveOrder(OrderMessageInput orderInput) {
        Order order = orderConverter.convertToOrder(orderInput);

        synchronized (buffer) {
            buffer.add(order);

            if (buffer.size() >= BATCH_SIZE) {
                flushBatch();
            }
        }
    }


    private void flushBatch() {
        List<Order> snapshot;
        synchronized (buffer) {
            if (buffer.isEmpty()) {
                return;
            }
            snapshot = new ArrayList<>(buffer);
            buffer.clear();
        }

        orderRepository.saveAll(snapshot)
                .collectList()
                .doOnSuccess(savedList -> log.info("Inseridos em lote: {}", savedList.size()))
                .subscribe();
    }

    @Scheduled(fixedRate = 2 * 60 * 1000)
    public void scheduledFlush() {
        synchronized (buffer) {
            if (!buffer.isEmpty()) {
                log.info("Tempo de espera atingido. Processando lote com {} mensagem(ns).", buffer.size());
                flushBatch();
            }
        }
    }
}

