package br.com.ambev.services;

import br.com.ambev.data.Order;
import br.com.ambev.enums.Status;
import br.com.ambev.exceptions.NotFoundException;
import br.com.ambev.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public Flux<Order> findByStatus(Status status) {
        return orderRepository.findByStatus(status);
    }

    public Mono<Order> findByIdExternal(String idExternal) {
        return orderRepository.findByIdExternal(idExternal)
                .switchIfEmpty(Mono.error(new NotFoundException("Pedido não encontrado")));
    }
}