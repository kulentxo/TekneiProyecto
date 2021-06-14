package es.teknei.practicas.ejercicio.controladores;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import javax.security.auth.Subject;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.filenet.api.collection.ContentElementList;
import com.filenet.api.collection.ObjectStoreSet;
import com.filenet.api.collection.StringList;
import com.filenet.api.constants.AutoClassify;
import com.filenet.api.constants.CheckinType;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.Connection;
import com.filenet.api.core.ContentTransfer;
import com.filenet.api.core.Document;
import com.filenet.api.core.Domain;
import com.filenet.api.core.Factory;
import com.filenet.api.core.Folder;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.core.ReferentialContainmentRelationship;
import com.filenet.api.util.UserContext;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import es.teknei.practicas.ejercicio.entidades.Coche;
import es.teknei.practicas.ejercicio.entidades.Fabricante;
import es.teknei.practicas.ejercicio.proyecciones.ProyeccionCoche;

@Controller
public class FilenetController {

	private static final String URI = "http://34.234.153.200/wsi/FNCEWS40MTOM";
	private static final String USER = "Julen";
	private static final String PASS = "Hola1234$";
	private static final String STANZA = "FileNetP8WSI";
	private static final String CLASS = "Coches";
	private static final String FOLDER = "/PDFCoches";
	private static final String PROPERTYVALUE1 = "Marca";
	private static final String PROPERTYVALUE2 = "Matricula";
	private static final String PROPERTYVALUE3 = "Modelo";
	private static final String URLFABRICANTE = "http://localhost:8080/api/fabricante";

	/**
	 * Mapeado de get de filenet para subir el pdf
	 * 
	 * @param conseguir los atributos buscados en la lista para crear el pdf
	 * @return devuelve el nombre del html de index
	 * @throws JsonProcessingException
	 */
	@GetMapping("/filenet")
	public String pdfFilenet(HttpSession session, Model model) throws JsonProcessingException {

		// Crear la conexion con filenet
		Connection conn = Factory.Connection.getConnection(URI);
		Subject subject = UserContext.createSubject(conn, USER, PASS, STANZA);
		UserContext.get().pushSubject(subject);

		try {
			// Get default domain.
			Domain domain = Factory.Domain.fetchInstance(conn, null, null);

			// Get object stores for domain.
			ObjectStoreSet osSet = domain.get_ObjectStores();
			ObjectStore store = null;
			Iterator osIter = osSet.iterator();

			while (osIter.hasNext()) {
				store = (ObjectStore) osIter.next();
			}

			Document doc = Factory.Document.createInstance(store, CLASS);
			ContentElementList contentElementList = Factory.ContentElement.createList();
			ContentTransfer ct = Factory.ContentTransfer.createInstance();
			List<ProyeccionCoche> coches = (List<ProyeccionCoche>) session.getAttribute("coches");

			try {
				byte[] documento = crearPdf(coches);
				InputStream fin = new ByteArrayInputStream(documento);
				ct.setCaptureSource(fin);
				contentElementList.add(ct);
				doc.set_ContentElements(contentElementList);

			} catch (Exception e) {
				e.printStackTrace();
			}

			doc.checkin(AutoClassify.DO_NOT_AUTO_CLASSIFY, CheckinType.MAJOR_VERSION);
			StringList marca = Factory.StringList.createList();
			StringList matriculas = Factory.StringList.createList();
			StringList modelos = Factory.StringList.createList();
			String marcaTitulo = coches.get(0).getMarca();
			marca.add(coches.get(0).getMarca());

			for (int i = 0; i < coches.size(); i++) {
				matriculas.add(coches.get(i).getMatricula());
				modelos.add(coches.get(i).getModelo());
			}
			doc.getProperties().putValue("DocumentTitle", marcaTitulo);
			doc.getProperties().putValue(PROPERTYVALUE1, marca);
			doc.getProperties().putValue(PROPERTYVALUE2, matriculas);
			doc.getProperties().putValue(PROPERTYVALUE3, modelos);
			doc.set_MimeType("application/pdf");

			doc.save(RefreshMode.REFRESH);

			Folder container = Factory.Folder.getInstance(store, null, FOLDER);

			Document docVer = Factory.Document.getInstance(store, null, (doc.get_Id()));

			ReferentialContainmentRelationship rcr = Factory.ReferentialContainmentRelationship.createInstance(store,
					null);

			rcr.set_Head(docVer);
			rcr.set_Tail(container);
			rcr.save(RefreshMode.NO_REFRESH);
		} finally {
			UserContext.get().popSubject();
		}
		model.addAttribute("coche", new Coche());
		List<Fabricante> fabricantes = buscarFabricantes();
		model.addAttribute("fabricantes", fabricantes);
		return "index";
	}

	private List<Fabricante> buscarFabricantes() throws JsonProcessingException {
		RestTemplate restTemplate = new RestTemplate();
		String result = restTemplate.getForObject(URLFABRICANTE, String.class);
		ObjectMapper mapper = new ObjectMapper();
		List<Fabricante> fabricantes = mapper.reader().forType(new TypeReference<List<Fabricante>>() {
		}).readValue(result);
		return fabricantes;
	}

	private void addRows(PdfPTable tabla, List<ProyeccionCoche> coches) {
		for (ProyeccionCoche coche : coches) {
			tabla.addCell(coche.getMarca());
			tabla.addCell(coche.getModelo());
			tabla.addCell(coche.getMatricula());
		}
	}

	private void addTableHeader(PdfPTable table) {
		Stream.of(PROPERTYVALUE1, PROPERTYVALUE2, PROPERTYVALUE3).forEach(columnTitle -> {
			PdfPCell header = new PdfPCell();
			header.setBackgroundColor(BaseColor.LIGHT_GRAY);
			header.setBorderWidth(2);
			header.setPhrase(new Phrase(columnTitle));
			table.addCell(header);
		});

	}

	private byte[] crearPdf(List<ProyeccionCoche> coches) throws DocumentException {
		com.itextpdf.text.Document documento = new com.itextpdf.text.Document();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PdfWriter.getInstance(documento, out);

		documento.open();

		PdfPTable tabla = new PdfPTable(3);
		addTableHeader(tabla);
		addRows(tabla, coches);

		documento.add(tabla);
		documento.close();
		return out.toByteArray();

	}
}
