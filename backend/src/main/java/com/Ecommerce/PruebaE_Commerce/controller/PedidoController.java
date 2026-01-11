package com.Ecommerce.PruebaE_Commerce.controller;

import com.Ecommerce.PruebaE_Commerce.model.Pedido;
import com.Ecommerce.PruebaE_Commerce.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/pedidos")
@CrossOrigin(origins = "http://localhost:8080")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @PostMapping
    public ResponseEntity<Pedido> realizarPedido(@RequestBody Pedido pedido) {
        return new ResponseEntity<>(pedidoService.crearPedido(pedido), HttpStatus.CREATED);
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<Pedido> listarPorUsuario(@PathVariable Long usuarioId) {
        return pedidoService.listarPedidosPorUsuario(usuarioId);
    }
    
}
