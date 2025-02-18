package br.com.ambev.message.input;

import lombok.Data;

import java.util.List;

@Data
public class OrderMessageInput {
    private String idExternal;
    private List<ItemMessageInput> items;
}
