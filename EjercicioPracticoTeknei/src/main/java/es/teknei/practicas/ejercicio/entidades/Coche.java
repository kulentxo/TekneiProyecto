package es.teknei.practicas.ejercicio.entidades;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representa la clase de un coche
 * 
 * @author Julen Martin
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "coche", schema = "concesionario")
public class Coche {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "modelo", length = 45, nullable = true, unique = false)
	private String modelo;
	@Column(name = "matricula", length = 45, nullable = true, unique = true)
	private String matricula;
	@ManyToOne()
	@JoinColumn(name = "fabricante_id")
	private Fabricante fabricante;
}
