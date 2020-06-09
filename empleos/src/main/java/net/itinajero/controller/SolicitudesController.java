package net.itinajero.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import net.itinajero.model.Solicitud;
import net.itinajero.model.Usuario;
import net.itinajero.model.Vacante;
import net.itinajero.service.ICategoriasService;
import net.itinajero.service.ISolicitudesService;
import net.itinajero.service.IUsuariosService;
import net.itinajero.service.IVacantesService;
import net.itinajero.util.Utileria;

@Controller
@RequestMapping("/solicitudes")
public class SolicitudesController {
	
	/**
	 * EJERCICIO: Declarar esta propiedad en el archivo application.properties. El valor sera el directorio
	 * en donde se guardarán los archivos de los Curriculums Vitaes de los usuarios.
	 * empleosapp.ruta.cv=c:/empleos/cv-vacantes/
	 */
	@Value("${empleosapp.ruta.cv}")
	private String ruta;
	/** Declarando parametros  **/
	
	@Autowired
	private ISolicitudesService serviceSolicitudes;
	@Autowired
	private IVacantesService serviceVacantes;
	@Autowired
	private IUsuariosService serviceUsuarios;
	@Autowired
	private ICategoriasService serviceCategorias;  // no se utiliza ??
	
    /**
	 * Metodo que muestra la lista de solicitudes sin paginacion
	 * Seguridad: Solo disponible para un usuarios con perfil ADMINISTRADOR/SUPERVISOR.
	 * @return
	 */
    @GetMapping("/index") 
	public String mostrarIndex(Model model) {
    	// EJERCICIO
    	List<Solicitud> lista = serviceSolicitudes.buscarTodas();
    	model.addAttribute("solicitudes", lista);
		return "solicitudes/listSolicitudes";		
	}
    
    /**
	 * Metodo que muestra la lista de solicitudes con paginacion
	 * Seguridad: Solo disponible para usuarios con perfil ADMINISTRADOR/SUPERVISOR.
	 * @return
	 */
	@GetMapping("/indexPaginate")
	public String mostrarIndexPaginado(Model model, Pageable page) {		
		// EJERCICIO
		Page<Solicitud> lista = serviceSolicitudes.buscarTodas(page);
		model.addAttribute("solicitudes", lista);
		return "solicitudes/listSolicitudes";		
	} 
    
	/**
	 * Método para renderizar el formulario para aplicar para una Vacante
	 * Seguridad: Solo disponible para un usuario con perfil USUARIO.
	 * @return
	 */
	@GetMapping("/create/{idVacante}")
	public String crear(Solicitud solicitud, @PathVariable("idVacante") int id, Model model) {
		// EJERCICIO
		// Traemos los detalles de la Vacante seleccionada para despues mostrarla en la vista
		Vacante vacante = serviceVacantes.buscarPorId(id);
		model.addAttribute("vac", vacante);
		return "/solicitudes/formSolicitud";
		
	}
	
	/**
	 * Método que guarda la solicitud enviada por el usuario en la base de datos
	 * Seguridad: Solo disponible para un usuario con perfil USUARIO.
	 * @return
	 */
	@PostMapping("/save")
	public String guardar(Solicitud solicitud, Vacante vac, BindingResult result,RedirectAttributes attributes,
			@RequestParam("archivoCV") MultipartFile multiPart, @RequestParam("vac.id") int idvac, Authentication auth) {		
		// EJERCICIO
		System.out.println("PRUEBA COMIENZA solicitud es: " + solicitud);
		System.out.println("CONTINUA PRUEBA Vacante es vac: " + vac);
		System.out.println("el valor de vacante.id es " +idvac);
		vac = serviceVacantes.buscarPorId(idvac);
		// Recuperamos el username que inicio sesión
		String nombreUsuario =auth.getName();
		System.out.println("este es el nombre del usuario actual:  "+nombreUsuario);
		// Buscamos el objeto Usuario en BD
		Usuario usuario = serviceUsuarios.buscarPorUsername(nombreUsuario);
		System.out.println("Este es el usuario: "+ usuario);
		//String identificador =  request.getSession().getAttribute(name);		
		if (result.hasErrors()) {
			for (ObjectError error : result.getAllErrors()) {
				System.out.println("Ocurrieron los errores: " +error.getDefaultMessage());
			}
			return "solicitudes/formSolicitud";
		}
		// subir archivo pdf
		if (!multiPart.isEmpty()) {
			String nombrePdf = Utileria.guardarArchivoPDF(multiPart, ruta); 
			if (nombrePdf != null) { //pdf si se subio
				solicitud.setArchivo(nombrePdf); // Asignamos el nombre de la imagen
			}
		}
		solicitud.setFecha(new Date());
		solicitud.setVacante(vac);
		//String request.getSession().getId();
		// Referenciamos la solicitud con el usuario
        solicitud.setUsuario(usuario); 
		
		System.out.println("PRUEBA DE SOLICITUD: "+solicitud);
		System.out.println("PRUEBA DE VAc: "+vac);
		// Guadamos el objeto solicitud en la bd
		serviceSolicitudes.guardar(solicitud);
		attributes.addFlashAttribute("msg", "registro solicitud alamacenado ");
		System.out.println("La Solicitud es: " + solicitud);
		return "redirect:/";	//aqui
		
	}
	/* EDITAR */
//	@GetMapping("/editar/{id}")
//	public String editar(@PathVariable("id") int idSolicitud, Model model) {
//		Solicitud solicitud = serviceSolicitudes.buscarPorId(idSolicitud);
//		model.addAttribute("solicitud", solicitud);
//		
//		return "solicitudes/formSolicitud";
//	}
	
	
	/**
	 * Método para eliminar una solicitud
	 * Seguridad: Solo disponible para usuarios con perfil ADMINISTRADOR/SUPERVISOR. 
	 * @return
	 */
	@GetMapping("/delete/{id}")
	public String eliminar(@PathVariable("id") int idSolicitud, RedirectAttributes attributes) {		
		// EJERCICIO
		serviceSolicitudes.eliminar(idSolicitud); 
		attributes.addFlashAttribute("msg", "La solicitud fue eliminada");
		return "redirect:/solicitudes/indexPaginate";
		
	}
	/***   setGenericos  metodo que esta disponible para todos los metodos de aqui**/
	@ModelAttribute
	public void setGenericos(Model model) {
		model.addAttribute("vacantes", serviceVacantes.buscarTodas());
		model.addAttribute("usuarios", serviceUsuarios.buscarTodos());
		model.addAttribute("categorias", serviceCategorias.buscarTodas());
	}
			
	/**
	 * Personalizamos el Data Binding para todas las propiedades de tipo Date
	 * @param webDataBinder
	 */
	@InitBinder
	public void initBinder(WebDataBinder webDataBinder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		webDataBinder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}
	
}
