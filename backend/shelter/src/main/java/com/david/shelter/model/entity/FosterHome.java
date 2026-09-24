package com.david.shelter.model.entity;

import java.util.List;
import com.david.shelter.model.enums.Province;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity 
@Getter 
@Setter 
@NoArgsConstructor 
@Table (name = "foster_homes")
public class FosterHome {

    @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull 
    @Column (nullable = false)
    private Integer capacity;

    @NotNull  
    @Column (nullable = false)
    @Enumerated (EnumType.STRING)
    private Province province;

    @NotNull 
    @ManyToOne
    @JoinColumn (name = "user_id", nullable = false)
    private User user;

    @OneToMany (mappedBy = "fosterHome" , cascade = CascadeType.ALL)
    private List<Pet> pets;
}
