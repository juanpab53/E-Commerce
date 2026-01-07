package com.Ecommerce.PruebaE_Commerce.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Ecommerce.PruebaE_Commerce.model.Producto;
import com.Ecommerce.PruebaE_Commerce.repository.ProductRepository;

@Service
public class ProductoService {
    @Autowired
    private ProductRepository productRepository;

    public List<Producto> buscarPorNombre(String nombre) {
        return productRepository.findByNombreContainingIgnoreCase(nombre);
    }

    public List<Producto> listarPorCategoria(Long idCategoria) {
        return productRepository.findByCategoria(idCategoria);
    }

    public Producto actualizarStock(Producto producto) {
        return productRepository.save(producto);
    }

    public Producto creaProducto(Producto producto){
        return productRepository.save(producto);
    }

    public void eliminarProducto(Long id) {
        productRepository.deleteById(id);
    }

}
