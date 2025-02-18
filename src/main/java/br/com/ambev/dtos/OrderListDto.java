package br.com.ambev.dtos;

import br.com.ambev.enums.Status;

import java.math.BigDecimal;

public record OrderListDto(String idExternal, Status status, BigDecimal total) {
}
