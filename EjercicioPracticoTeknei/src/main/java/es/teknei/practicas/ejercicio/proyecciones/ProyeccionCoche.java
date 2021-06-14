package es.teknei.practicas.ejercicio.proyecciones;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

/**
 * Clase de proyeccion utilizada en busquedas personalizadas de CRUD
 * 
 * @author Julen Martin
 * @version 1.0
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProyeccionCoche {
	private String modelo;
	private String matricula;
	private String marca;

	public ProyeccionCoche(String modelo, String matricula, String marca) {
		this.modelo = modelo;
		this.matricula = matricula;
		this.marca = marca;
	}

	public ProyeccionCoche() {
		super();
	}
}
