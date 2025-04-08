package com.example.petstore.controller;

import com.example.petstore.model.Pet;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/pets")
@Tag(name = "Pet Management", description = "Operations for managing pets")
public class PetController {
    private final Map<Long, Pet> pets = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong();

    @GetMapping
    @Operation(summary = "Get all pets", description = "Returns a list of all pets in the system")
    public List<Pet> getAllPets() {
        return new ArrayList<>(pets.values());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a pet by ID", description = "Returns a single pet by its ID")
    public ResponseEntity<Pet> getPetById(@PathVariable Long id) {
        Pet pet = pets.get(id);
        return pet != null ? ResponseEntity.ok(pet) : ResponseEntity.notFound().build();
    }

    @PostMapping
    @Operation(summary = "Create a new pet", description = "Creates a new pet in the system")
    public Pet createPet(@Valid @RequestBody Pet pet) {
        Long newId = idGenerator.incrementAndGet();
        pet.setId(newId);
        pets.put(pet.getId(), pet);
        return pet;
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a pet", description = "Updates an existing pet's information")
    public ResponseEntity<Pet> updatePet(@PathVariable Long id, @Valid @RequestBody Pet pet) {
        if (!pets.containsKey(id)) {
            return ResponseEntity.notFound().build();
        }
        pet.setId(id);
        pets.put(id, pet);
        return ResponseEntity.ok(pet);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a pet", description = "Deletes a pet from the system")
    public ResponseEntity<Void> deletePet(@PathVariable Long id) {
        if (!pets.containsKey(id)) {
            return ResponseEntity.notFound().build();
        }
        pets.remove(id);
        return ResponseEntity.noContent().build();
    }
} 