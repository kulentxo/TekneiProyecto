package es.teknei.practicas.ejercicio.practicas.repositorio;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import es.teknei.practicas.ejercicio.entidades.Fabricante;

/**
 * Repositorio de CRUD general de fabricantes
 * 
 * @author Julen Martin
 * @version 1.0
 */
@Repository
public interface FabricanteRepository extends CrudRepository<Fabricante, Long> {

	@Query("SELECT id FROM Fabricante WHERE marca = ?1")
	Long buscarFabricantePorMarca(String marca);
}
