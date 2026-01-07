package com.Ecommerce.PruebaE_Commerce.model;
import jakarta.persistence.Embeddable;  
import lombok.Data;

@Data
@Embeddable
public class Direccion {
    private String pais;
    private String ciudad;
    private String calle;
}