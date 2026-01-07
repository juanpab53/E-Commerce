package com.Ecommerce.PruebaE_Commerce.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.Ecommerce.PruebaE_Commerce.model.Categoria;
import com.Ecommerce.PruebaE_Commerce.repository.CategoriaRepository;

@Service
public class CategoriaService {
    @Autowired
    private CategoriaRepository categoriaRepository;

    public List<Categoria> listarTodas() {
        return categoriaRepository.findAll();
    }

    public Categoria guardar(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    public void eliminar(Long id) {
        categoriaRepository.deleteById(id);
    }

    public List<Categoria> buscarPorCategoria(String categoria){
        return categoriaRepository.findByCategoriaIgnoreCase(categoria);
    }
}
