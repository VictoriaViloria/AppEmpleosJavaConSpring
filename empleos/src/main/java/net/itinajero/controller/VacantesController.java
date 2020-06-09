package net.itinajero.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

import net.itinajero.model.Vacante;
import net.itinajero.service.ICategoriasService;
import net.itinajero.service.IVacantesService;
import net.itinajero.util.Utileria;

@Controller
@RequestMapping("/vacantes")
public class VacantesController {	
	@Value("${empleosapp.ruta.imagenes}")
	private String ruta;	
	@Autowired
	private IVacantesService serviceVacantes;
	@Autowired
	private ICategoriasService serviceCategorias;   /// IMPORTANTE
	@GetMapping("/index")
	public String mostrarIndex(Model model) {
		 // TODO 1. Obtener todas las vacantes (recuperarlas con la clase de servicio
		List<Vacante> lista = serviceVacantes.buscarTodas();
		// TODO 2. Agregar al modelo el listado de Vcantes
		model.addAttribute("vacantes", lista);
		// TODO 3. Renderizar las vacantes en la vista (integrar el archivo tempalte-empleos/listaVacantes.html
		// TODO 4. Agregar al menu una opcion llamada "Vacantes" configurando la URL "vacantes/index"
		return "vacantes/listVacantes";
	}
	// paginacion
	@GetMapping("/indexPaginate")
	public String mostrarIndexPaginado(Model model, Pageable page) {
		Page<Vacante> lista = serviceVacantes.buscarTodas(page);
		model.addAttribute("vacantes", lista);
		int num = page.getPageSize();//page.getTotalPages();
		model.addAttribute("num", num);
		return "vacantes/listVacantes";
	}
		@GetMapping("/create")
	public String crear(Vacante vacante, Model model) {
		//model.addAttribute("categorias", serviceCategorias.buscarTodas());
		return "vacantes/formVacante";
	}	
	@GetMapping("/editar/{id}")
	public String editar(@PathVariable("id") int idVacante, Model model) {
		Vacante vacante = serviceVacantes.buscarPorId(idVacante);
		model.addAttribute("vacante", vacante);
		//model.addAttribute("categorias", serviceCategorias.buscarTodas());
		return "vacantes/formVacante";		
	}
// metodo para que este disponible para los metodos de aqui y entonces lo borramos de editar y crear
	@ModelAttribute
	public void setGenericos(Model model) {
		model.addAttribute("categorias", serviceCategorias.buscarTodas());
	}
	
	@PostMapping("/save")
	public String guardar(Vacante vacante, BindingResult result, RedirectAttributes attributes,
			@RequestParam("archivoImagen") MultipartFile multiPart) {
		if (result.hasErrors()) {
			for (ObjectError error: result.getAllErrors()){
				System.out.println("Ocurrio un error: "+ error.getDefaultMessage());
				}			
			return "vacantes/formVacante";
			}		
			if (!multiPart.isEmpty()) {
				// String ruta = "/empleos/img-vacantes/"; // Linux/MAC
				//String ruta = "c:/empleos/img-vacantes/"; // Windows
				String nombreImagen = Utileria.guardarArchivo(multiPart, ruta);
				if (nombreImagen != null) { // La imagen si se subio
					// Procesamos la variable nombreImagen
					vacante.setImagen(nombreImagen);
				}
			}
		serviceVacantes.guardar(vacante);
		attributes.addFlashAttribute("msg","Registro Guardado!!!!");
		//model.addAttribute("msg", "Registro Guardado!!");
		System.out.println("Vacante: "+vacante);
		//return "vacantes/listVacantes"; aplicar redirect por que despues de crear una vacante no sale las demas lista de vacantes
		return "redirect:/vacantes/index";
	}

	/*  ES LO MISMO QUE: 
	@PostMapping("/save")
	public String guardar(@RequestParam("nombre") String nombre, @RequestParam("descripcion") String descripcion,
			@RequestParam("estatus") String estatus, @RequestParam("fecha") String fecha, @RequestParam("destacado") int destacado,
			@RequestParam("salario") double salario, @RequestParam("detalles") String detalles) {
		System.out.println("Nombre Vacante: "+nombre);
		System.out.println("Descripcion: "+descripcion);
		System.out.println("Estatus: "+estatus);
		System.out.println("Fecha Publicacion: "+fecha);
		System.out.println("Destacado: "+destacado);
		System.out.println("Salario Ofrecido: "+salario);
		System.out.println("detalles: "+detalles);
		return "vacantes/listVacantes";
	}
	*/
	
//	@GetMapping("/delete")
//	public String eliminar(@RequestParam("id") int idVacante, Model model) {
//		System.out.println("Borrando vacante con id: "+idVacante);
//		serviceVacantes.eliminar(idVacante);
//		model.addAttribute("id", idVacante);
//		return "mensaje";
//	} 
	
	
	@GetMapping("/delete/{id}")
	public String eliminar(@PathVariable("id") int idVacante, RedirectAttributes attributes) {
		System.out.println("Borrando vacante con id: "+idVacante);
		serviceVacantes.eliminar(idVacante);
		attributes.addFlashAttribute("msg", "La vacante fue eliminada");
		return "redirect:/vacantes/index";
	}
	
	
	
 	@GetMapping("/view/{id}")
    public String verDetalle(@PathVariable("id") int idVacante, Model model) {
	 
 		Vacante vacante = serviceVacantes.buscarPorId(idVacante);
// 		System.out.println("IdVacante: "+idVacante);
//	    model.addAttribute("idVacante", idVacante);
 		System.out.println("la vacante es: "+vacante);
	    model.addAttribute("vacante", vacante);
	  //buscar los detalles de la vacante en Id BD
	  //return "vacantes/detalle";
	    return "detalle";
 }	
 	
 	@InitBinder
 	public void initBinder(WebDataBinder webDataBinder) {
 	SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
 	webDataBinder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
 	}
}