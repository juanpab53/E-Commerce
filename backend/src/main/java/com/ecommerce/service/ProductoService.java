package com.ecommerce.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ecommerce.dto.ProductoDTO;
import com.ecommerce.dto.ProductoResponseDTO;
import com.ecommerce.exceptions.BusinessLogicException;
import com.ecommerce.exceptions.ResourceNotFoundException;
import com.ecommerce.model.Categoria;
import com.ecommerce.model.Producto;
import com.ecommerce.repository.CategoriaRepository;
import com.ecommerce.repository.DetallePedidoRepository;
import com.ecommerce.repository.ProductRepository;
import jakarta.transaction.Transactional;

@Service
public class ProductoService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private DetallePedidoRepository detallePedidoRepository;

    @Transactional
    public ProductoResponseDTO crear(ProductoDTO productoDto) {
        Categoria categoria = categoriaRepository.findById(productoDto.categoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("No se puede crear el producto: Categoría no encontrada con ID: " + productoDto.categoriaId()));
        
        Producto producto = new Producto();
        producto.setNombre(productoDto.nombre());
        producto.setDescripcion(productoDto.descripcion());
        producto.setPrecio(productoDto.precio());
        producto.setCantidad(productoDto.cantidad());
        producto.setCategoria(categoria);

        return mapearAResponse(productRepository.save(producto));
    }

    @Transactional
    private Producto encontrarProducto(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));
    }

    @Transactional
    public List<ProductoResponseDTO> listarTodos() {
        return productRepository.findAll().stream()
                .map(this::mapearAResponse).toList();
    }

    @Transactional
    public ProductoResponseDTO buscarPorId(Long id) {
        Producto p = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));
        return mapearAResponse(p);
    }

    @Transactional
    public List<ProductoResponseDTO> buscarPorNombre(String nombre) {
        return productRepository.findByNombreContainingIgnoreCase(nombre).stream()
                .map(this::mapearAResponse).toList();
    }

    @Transactional
    public List<ProductoResponseDTO> listarPorCategoria(Long categoriaId) {
        if (!categoriaRepository.existsById(categoriaId)) {
            throw new ResourceNotFoundException("No se pueden listar productos: Categoría no encontrada con ID: " + categoriaId);
        }
        return productRepository.findByCategoriaId(categoriaId).stream()
                .map(this::mapearAResponse).toList();
    }

    @Transactional
    public ProductoResponseDTO actualizarStock(Long id, Integer nuevoStock) {
        if (nuevoStock < 0) {
            throw new BusinessLogicException("Error de inventario: El stock no puede ser negativo (" + nuevoStock + ")");
        }
        Producto producto = encontrarProducto(id);
        producto.setCantidad(nuevoStock);
        return mapearAResponse(productRepository.save(producto));
    }

    @Transactional
    public void eliminar(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se puede eliminar: Producto no encontrado con ID: " + id);
        }

        if (detallePedidoRepository.existsByProductoId(id)) {
            throw new BusinessLogicException("Restricción de integridad: El producto con ID " + id + 
                                           " tiene historial de ventas y no puede ser eliminado físicamente.");
        }

        productRepository.deleteById(id);
    }

    private ProductoResponseDTO mapearAResponse(Producto p) {
        return new ProductoResponseDTO(
                p.getId(), p.getNombre(), p.getDescripcion(),
                p.getPrecio(), p.getCantidad(), p.getCategoria().getNombre());
    }
}