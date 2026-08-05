package org.example.internship_order_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Table(name = "items")
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Item extends Auditable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    @EqualsAndHashCode.Include
    private Long id;

    @ToString.Include
    @Column(name = "name", nullable = false)
    private String name;

    @ToString.Include
    @Column(name = "price", nullable = false, precision = 19, scale = 2)
    private BigDecimal price;
}
