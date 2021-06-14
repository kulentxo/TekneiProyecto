package es.teknei.practicas.ejercicio.controladores;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestApiSecurityController {

	@GetMapping("/")
	public String home() {
		return ("<h1>Welcome welcome</h1>");
	}

	@GetMapping("/user")
	public String user() {
		return ("<h1>Welcome welcome user</h1>");
	}

	@GetMapping("/admin")
	public String admin() {
		return ("<h1>Welcome welcome admin</h1>");
	}
}
