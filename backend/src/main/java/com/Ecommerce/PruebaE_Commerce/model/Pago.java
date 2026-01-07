package com.Ecommerce.PruebaE_Commerce.model;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "pago")
public class Pago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double monto;
    private String comprobante;
    private String fechaPago;

    @Enumerated(EnumType.STRING)
    private MetodoPago metodoPago;

    @OneToOne
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

}
