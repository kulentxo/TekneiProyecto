package es.teknei.practicas.ejercicio.practicas.repositorio;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import es.teknei.practicas.ejercicio.entidades.Coche;
import es.teknei.practicas.ejercicio.proyecciones.ProyeccionCoche;

/**
 * Repositorio de CRUD general de coches
 * 
 * @author Julen Martin
 * @version 1.0
 */
@Repository
public interface CocheRepository extends CrudRepository<Coche, Long> {
	@Query("SELECT new es.teknei.practicas.ejercicio.proyecciones.ProyeccionCoche(c.modelo, c.matricula, f.marca) FROM Coche as c INNER JOIN Fabricante as f ON c.fabricante.id= f.id WHERE c.fabricante.id = ?1")
	Collection<ProyeccionCoche> buscarPorFabricante(Long id);
}
