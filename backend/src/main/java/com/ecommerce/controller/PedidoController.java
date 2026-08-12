package com.ecommerce.controller;

import com.ecommerce.dto.PedidoDTO;
import com.ecommerce.dto.PedidoResponseDTO;
import com.ecommerce.model.Estado;
import com.ecommerce.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    @PostMapping
    public ResponseEntity<PedidoResponseDTO> realizarPedido(@Valid @RequestBody PedidoDTO pedidoDto) {
        PedidoResponseDTO nuevoPedido = pedidoService.crearPedido(pedidoDto);
        return new ResponseEntity<>(nuevoPedido, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<PedidoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(pedidoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.buscarPorId(id));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<PedidoResponseDTO>> listarPorUsuario(@PathVariable Long usuarioId) {
        List<PedidoResponseDTO> pedidos = pedidoService.listarPedidosPorUsuario(usuarioId);
        return ResponseEntity.ok(pedidos);
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<PedidoResponseDTO> cambiarEstado(
            @PathVariable Long id, 
            @RequestParam Estado nuevoEstado) {
        return ResponseEntity.ok(pedidoService.cambiarEstado(id, nuevoEstado));
    }

    @DeleteMapping("/{id}/cancelar")
    public ResponseEntity<PedidoResponseDTO> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.cancelarPedido(id));
    }
}