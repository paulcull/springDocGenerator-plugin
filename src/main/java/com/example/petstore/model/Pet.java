package com.example.petstore.model;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Pet information")
public class Pet {
    @Schema(description = "Unique identifier of the pet", example = "1")
    private Long id;

    @NotBlank
    @Schema(description = "Name of the pet", example = "Fluffy")
    private String name;

    @NotNull
    @Schema(description = "Type of the pet", example = "CAT")
    private PetType type;

    @Schema(description = "Age of the pet in years", example = "3")
    private Integer age;

    public enum PetType {
        CAT,
        DOG,
        BIRD,
        FISH
    }
} 