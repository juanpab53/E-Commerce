package com.Ecommerce.PruebaE_Commerce.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.Ecommerce.PruebaE_Commerce.model.Usuario;

@Repository
public interface UserRepository extends JpaRepository<Usuario, Long>{
    
}
