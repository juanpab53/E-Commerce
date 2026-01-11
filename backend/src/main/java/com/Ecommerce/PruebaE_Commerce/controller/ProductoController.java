package com.Ecommerce.PruebaE_Commerce.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.Ecommerce.PruebaE_Commerce.model.Producto;
import com.Ecommerce.PruebaE_Commerce.service.ProductoService;

@RestController
@RequestMapping("/productos")
@CrossOrigin(origins = "http://localhost:8080")
public class ProductoController {
    @Autowired
    private ProductoService productoService;

    @PostMapping
    public ResponseEntity<Producto> crear(@RequestBody Producto producto) {
        return new ResponseEntity<>(productoService.creaProducto(producto), HttpStatus.CREATED);
    }

    @GetMapping
    public List<Producto> listarTodos() {
        return productoService.listarTodos();
    }
}
