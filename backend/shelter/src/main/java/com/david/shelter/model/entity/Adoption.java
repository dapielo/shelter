package com.david.shelter.model.entity;

import java.time.LocalDate;
import com.david.shelter.model.enums.AdoptionStatus;
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
@Table (name = "adoptions")
public class Adoption {

    @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    // No la voy a hacer bilateral, cuando busque por una adopcion la busco en esta tabla, no necesito que se haga una segunda consulta al buscar un usuario
    @ManyToOne
    @JoinColumn (name = "user_id", nullable = false)
    @NotNull 
    private User adopter;

    // Igual que el la anterior, cuando se trata de las adopciones hago siempre las consultas en esta tabla filtrando por el id que necesito
    // Es ManyToOne, aunque un animal no tiene un historial de adopciones, poruqe si vuelve a ser abandonado es muy dificil saber si es el mismo
    // la pongo manyToOne poruqe un animal, antes de cerrar un contrato de adopcion, podria recibir varias solicitudes de adopcion de varios usuarios
    @ManyToOne  
    @JoinColumn (name = "pet_id", nullable = false)
    @NotNull  
    private Pet pet;

    @Column (nullable = false)
    private LocalDate initialDate = LocalDate.now(); // La fecha de creacion de la peticion de adopción se crea automaticamente al instanciar el objeto

    private LocalDate endingDate;

    @NotNull  
    @Column (nullable = false)
    @Enumerated (EnumType.STRING)
    private AdoptionStatus status;
}
