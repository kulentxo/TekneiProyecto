package es.teknei.practicas.ejercicio.controladores;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.teknei.practicas.ejercicio.entidades.Coche;
import es.teknei.practicas.ejercicio.entidades.Fabricante;
import es.teknei.practicas.ejercicio.entidades.Usuario;
import es.teknei.practicas.ejercicio.practicas.repositorio.CocheRepository;
import es.teknei.practicas.ejercicio.practicas.repositorio.FabricanteRepository;
import es.teknei.practicas.ejercicio.proyecciones.ProyeccionCoche;

/**
 * Clase controladora que conecta con los html para mostrar o recibir los datos
 * 
 * @author Julen Martin
 * @version 1.0
 */
@Controller
public class IndexController {
	// Conexion con la clase de repositorio de coches
	@Autowired
	CocheRepository cocheRepository;
	// Conexion con la clase de repositorio de fabricante
	@Autowired
	FabricanteRepository fabricanteRepository;

	Usuario usuario;

	private static final String URLCOCHES = "http://localhost:8080/api/coches/";
	private static final String URLFABRICANTE = "http://localhost:8080/api/fabricante";

	/**
	 * Mapeado de get de alta
	 * 
	 * @param model
	 * @return devuelve el nombre del html de index
	 * @throws JsonProcessingException
	 */
	@GetMapping("/alta")
	public String inicioAlta(Model model) throws JsonProcessingException {
		model.addAttribute("coche", new Coche());
		List<Fabricante> fabricantes = buscarFabricantes();
		model.addAttribute("fabricantes", fabricantes);
		return "index";
	}

	/**
	 * Sacar lista de fabricantes
	 * 
	 * @return devuelve una lista de fabricantes
	 * @throws JsonProcessingException
	 */
	private List<Fabricante> buscarFabricantes() throws JsonProcessingException {
		RestTemplate restTemplate = new RestTemplate();
		String result = restTemplate.getForObject(URLFABRICANTE, String.class);
		ObjectMapper mapper = new ObjectMapper();
		List<Fabricante> fabricantes = mapper.reader().forType(new TypeReference<List<Fabricante>>() {
		}).readValue(result);
		return fabricantes;
	}

	/**
	 * Mapeado de get de lista
	 * 
	 * @param model
	 * @return devuelve el nombre del html de la lista
	 * @throws JsonProcessingException
	 */
	@GetMapping("/lista")
	public String listaCoches(Model model) throws JsonProcessingException {
		model.addAttribute("fabricante", new Fabricante());
		List<Fabricante> fabricantes = buscarFabricantes();
		model.addAttribute("fabricantes", fabricantes);
		return "listaCoches";
	}

	/**
	 * Mapeado de post de lista
	 * 
	 * @param fabricante que se ha elegido en la pagina
	 * @param session
	 * @return devuelve el nombre del html de la lista
	 * @throws JsonProcessingException
	 */
	@PostMapping("/buscar")
	public String buscarLista(Fabricante fabricante, HttpSession session, Model model) throws JsonProcessingException {
		Long id = fabricanteRepository.buscarFabricantePorMarca(fabricante.getMarca());
		String url = URLCOCHES + id;
		List<Fabricante> fabricantes = buscarFabricantes();
		model.addAttribute("fabricantes", fabricantes);
		RestTemplate restTemplate = new RestTemplate();
		String result = restTemplate.getForObject(url, String.class);
		ObjectMapper mapper = new ObjectMapper();
		List<ProyeccionCoche> coches = mapper.reader().forType(new TypeReference<List<ProyeccionCoche>>() {
		}).readValue(result);
		session.setAttribute("coches", coches);
		return "listaCoches";
	}

	/**
	 * Mapeado de post de alta
	 * 
	 * @param objeto de coche para insertar
	 * @return devuelve el nombre del html de index
	 */
	@PostMapping("/darAlta")
	public String altaCoche(Coche coche) {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.postForEntity(URLCOCHES, coche, Coche.class);
		return "index";
	}
}
