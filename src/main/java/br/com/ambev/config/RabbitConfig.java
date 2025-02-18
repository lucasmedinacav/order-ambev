package br.com.ambev.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitConfig {

    public static final String QUEUE_ORDERS = "orders-queue";
    public static final String DLX_EXCHANGE = "dlx.exchange";
    public static final String DLQ_ORDERS = "orders-queue.dlq";

    @Bean
    public DirectExchange ordersDlx() {
        return ExchangeBuilder.directExchange(DLX_EXCHANGE)
                .durable(true)
                .build();
    }

    @Bean
    public Queue ordersQueue() {
        return QueueBuilder.durable(QUEUE_ORDERS)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", DLQ_ORDERS)
                .build();
    }

    @Bean
    public Queue ordersDlq() {
        return QueueBuilder.durable(DLQ_ORDERS).build();
    }

    @Bean
    public Binding ordersDlqBinding() {
        return BindingBuilder
                .bind(ordersDlq())
                .to(ordersDlx())
                .with(DLQ_ORDERS);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            SimpleRabbitListenerContainerFactoryConfigurer configurer) {

        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        factory.setConcurrentConsumers(5);
        factory.setMaxConcurrentConsumers(25);
        factory.setDefaultRequeueRejected(false);

        return factory;
    }
}
