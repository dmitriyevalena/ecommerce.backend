package com.commerce.store.ecommerce.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "address")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name= "id", nullable = false)
    private long id;

    @Column(name = "address_line_1", length = 512, nullable = false)
    private String addressLine1;

    @Column(name = "address_line_2", length = 512)
    private String addressLine2;

    @Column(name="city", nullable = false)
    private String city;

    @Column(name="country", nullable = false, length = 75)
    private String country;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private LocalUser user;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

}
