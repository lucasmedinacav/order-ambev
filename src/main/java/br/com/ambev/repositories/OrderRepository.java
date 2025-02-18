package br.com.ambev.repositories;

import br.com.ambev.data.Order;
import br.com.ambev.enums.Status;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface OrderRepository extends ReactiveMongoRepository<Order, String> {

    Flux<Order> findByStatus(Status status);

    Mono<Order> findByIdExternal(String idExternal);
}