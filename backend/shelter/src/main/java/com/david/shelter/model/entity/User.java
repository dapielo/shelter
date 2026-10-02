package com.david.shelter.model.entity;

import java.util.List;

import com.david.shelter.model.enums.Role;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity 
@Setter 
@Getter 
@NoArgsConstructor 
@AllArgsConstructor 
@Table(name = "users")
public class User {

    // Es obligatorio para la base de datos que la entidad tenga un id (clave primaria), usamos GeneratedValue para que se delege la asignacion al motor del SGBD
    // GenerationType.IDENTITY hace que se use el auto_icrement en la base de datos para las claves primarias
    @Id 
    private Long id;

    // El email tiene que ser unique, poruqe es lo que vamos a usar para generar el token de autenticacion, aunque por rendimiento el id siga siendo un Long
    @NotBlank 
    @Column (nullable = false, unique = true)
    private String email;

    // NotBlank es una validacion del backend, nullable=false es una condicion de la columna en la base de datos
    @NotBlank 
    @Column (nullable = false)
    private String username;

    @NotBlank 
    @Column (nullable = false)
    private String password;

    // Esta relacion es oneToMany, porque una persona puede registrar varios hogares de acogida en su usuario, puede ser que haga publicaciones 
    // de animales que tiene de acogida un familiar por ejemplo, o que tenga tambien una finca en la que acoge animales a parte de si propia casa
    @OneToMany (mappedBy = "user", cascade = CascadeType.ALL) 
    // Como el atributo es un array de objetos FosterHome, JPA sabe que tiene que buscar el atributo "user" dentro de la clase FosterHome
    private List<FosterHome> fosterHomes;

    @NotNull 
    @Enumerated (EnumType.STRING)
    Role role;
}
