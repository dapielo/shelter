package com.david.shelter.model.entity;

import java.time.LocalDate;

import com.david.shelter.model.enums.AgeGroup;
import com.david.shelter.model.enums.Species;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity 
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@Table(name = "pets")
public class Pet {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank 
    @Column (nullable = false)
    private String name;

    @NotNull  
    @Column (nullable = false)
    @Enumerated (EnumType.STRING) // Sino en lugar del texto guardaría el indice que tiene dentro del enum
    private Species species;

    private String breed;

    @Enumerated (EnumType.STRING)
    private AgeGroup ageGroup;

    @Column(columnDefinition = "MEDIUMTEXT") // para que cree la columna como tipo de dato "mediumtext"
    private String info;

    @Column (nullable = false)
    private LocalDate registeredAt = LocalDate.now(); // Se genera automaticamente al crear la entidad, que sucede cuando se registra

    @ManyToOne 
    @JoinColumn (name = "fosterHome_id", nullable = false)
    @NotNull 
    private FosterHome fosterHome;
}
