package org.example.internship_order_service.repository;

import org.example.internship_order_service.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Optional<Item> findByName(String name);
}
