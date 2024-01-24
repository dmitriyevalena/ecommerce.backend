package com.commerce.store.ecommerce.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "web_order_quantities")
public class WebOrderQuantities {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @ManyToOne(optional = false)
    @JoinColumn(name = "order_id", nullable = false  )
    private WebOrder order;
}
