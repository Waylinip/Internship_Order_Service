package org.example.internship_order_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.internship_order_service.dto.ItemDTO;
import org.example.internship_order_service.service.ItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ItemDTO> create(@Valid @RequestBody ItemDTO dto) {
        return new ResponseEntity<>(itemService.create(dto), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(itemService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<ItemDTO>> getAll() {
        return ResponseEntity.ok(itemService.getAll());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ItemDTO> update(@PathVariable Long id,@Valid @RequestBody ItemDTO dto) {
        return ResponseEntity.ok(itemService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        itemService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
