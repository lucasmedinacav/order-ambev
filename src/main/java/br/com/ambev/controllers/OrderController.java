package br.com.ambev.controllers;

import br.com.ambev.data.Order;
import br.com.ambev.dtos.OrderListDto;
import br.com.ambev.enums.Status;
import br.com.ambev.services.OrderService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Api(value = "Orders API", tags = {"Orders"})
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    @ApiOperation(value = "Busca pedidos por status", notes = "Retorna uma lista de pedidos filtrados pelo status com os campos básicos: idExternal, status e total.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Pedidos retornados com sucesso"),
            @ApiResponse(code = 400, message = "Parâmetro de status inválido"),
            @ApiResponse(code = 404, message = "Nenhum pedido encontrado para o status informado")
    })
    public Flux<OrderListDto> findByStatus(
            @ApiParam(value = "Status do pedido (ex.: OPENED, CALCULATED, etc.)", required = true, allowableValues = "OPENED, CALCULATED, ...")
            @RequestParam Status status) {
        return orderService.findByStatus(status)
                .map(o -> new OrderListDto(o.getIdExternal(), o.getStatus(), o.getTotal()));
    }

    @GetMapping("/{idExternal}")
    @ApiOperation(value = "Busca pedido por idExternal", notes = "Retorna o pedido completo, com todos os detalhes e itens, com base no idExternal.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Pedido retornado com sucesso"),
            @ApiResponse(code = 404, message = "Pedido não encontrado")
    })
    public Mono<Order> getOrder(
            @ApiParam(value = "Identificador externo do pedido", required = true)
            @PathVariable String idExternal) {
        return orderService.findByIdExternal(idExternal);
    }
}
