package com.Ecommerce.PruebaE_Commerce.controller;

import com.Ecommerce.PruebaE_Commerce.model.Categoria;
import com.Ecommerce.PruebaE_Commerce.service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/categorias")
@CrossOrigin(origins = "http://localhost:8080")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping
    public List<Categoria> listar() {
        return categoriaService.listarTodas();
    }

    @PostMapping
    public ResponseEntity<Categoria> crear(@RequestBody Categoria categoria) {
        return new ResponseEntity<>(categoriaService.guardar(categoria), HttpStatus.CREATED);
    }
}