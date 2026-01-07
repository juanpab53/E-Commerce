package com.Ecommerce.PruebaE_Commerce.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "producto")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(length = 1000)
    private String descripcion;

    @Column(nullable = false)
    private double precio;

    private int cantidad;

    @ManyToOne
    @JoinColumn(name = "id_categoria")
    private Categoria categoria;

}