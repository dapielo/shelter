package com.david.shelter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.david.shelter.model.entity.FosterHome;
import com.david.shelter.model.entity.User;

import java.util.List;


@Repository 
public interface FosterHomeRepository extends JpaRepository<FosterHome,Long> {

    // La consulta no esta con SQL, sino con JPQL que trabaja con clases en lugar de con tablas, esta consulta nos devuelve todas las casas de acogida
    // asignadas al usuario que recibe el metodo por parametro, pero ademas rellena el atributo pets de cada uno de los objetos gracias al fetch que va con el left join
    // no necesita que se le indique condicion de join, quda implicito al indicar el atributo (f.pets) ya que tenemos definida la relacion en las entidades
    @Query ("select f from FosterHome f left join fetch f.pets where f.user = :user")
    public List<FosterHome> findByUserWithPets(User user);

}
