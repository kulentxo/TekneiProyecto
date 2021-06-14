package es.teknei.practicas.ejercicio.entidades;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "user", schema = "concesionario")
public class Usuario {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "username", length = 45, nullable = false, unique = false)
	private String user;
	@Column(name = "password", length = 45, nullable = false, unique = false)
	private String pass;
	@Column(name = "roles", length = 45, nullable = false, unique = false)
	private String role;
}
