package es.teknei.practicas.ejercicio.controladores;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.teknei.practicas.ejercicio.entidades.Coche;
import es.teknei.practicas.ejercicio.entidades.Fabricante;
import es.teknei.practicas.ejercicio.practicas.repositorio.CocheRepository;
import es.teknei.practicas.ejercicio.practicas.repositorio.FabricanteRepository;
import es.teknei.practicas.ejercicio.proyecciones.ProyeccionCoche;

/**
 * Controlador de Rest Api para hacer conexion con la base de datos
 * 
 * @author Julen Martin
 * @version 1.0
 */
@RestController
@RequestMapping("/api")
public class RestApiController {

	@Autowired
	CocheRepository cocheRepo;
	@Autowired
	FabricanteRepository fabriRepo;

	/**
	 * Mapeado de get coches para sacar lista de coches por id de fabricante
	 * 
	 * @param id del fabricante
	 * @return devuelve una coleccion utilizando la clase de proyeccion
	 */
	@GetMapping("/coches/{id}")
	public Collection<ProyeccionCoche> getPorFabricanteId(@PathVariable Long id) {
		Collection<ProyeccionCoche> coches = cocheRepo.buscarPorFabricante(id);
		return coches;
	}

	/**
	 * Mapeado de get fabricante para sacar lista de todos los fabricantes
	 * 
	 * @return devuelve la lista de fabriantes
	 */
	@GetMapping("/fabricante")
	public Iterable<Fabricante> getFabrciante() {
		Iterable<Fabricante> fabricantes = fabriRepo.findAll();
		return fabricantes;
	}

	/**
	 * Mapeado de post de coches para introducir coches en la base de datos
	 * 
	 * @param objeto de la clase de coches
	 * @return devuelve el coche introducido
	 */
	@PostMapping("/coches")
	public Coche post(@RequestBody Coche c) {
		if (fabriRepo.buscarFabricantePorMarca(c.getFabricante().getMarca()) == null) {
			fabriRepo.save(c.getFabricante());
		}
		c.getFabricante().setId(fabriRepo.buscarFabricantePorMarca(c.getFabricante().getMarca()));
		return cocheRepo.save(c);
	}

}
