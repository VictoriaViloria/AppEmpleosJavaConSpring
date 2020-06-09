package net.itinajero.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import net.itinajero.model.Categoria;
import net.itinajero.model.Vacante;
import net.itinajero.service.ICategoriasService;
import net.itinajero.service.IVacantesService;
@Controller
@RequestMapping(value="/categorias")
public class CategoriasController {
	
	@Autowired
	private ICategoriasService serviceCategorias;
	@Autowired
	private IVacantesService serviceVacantes;
	// @GetMapping("/index")
	@RequestMapping(value="/index", method=RequestMethod.GET)
	public String mostrarIndex(Model model) {
	 List<Categoria> lista = serviceCategorias.buscarTodas();
     model.addAttribute("categorias", lista);
	return "categorias/listCategorias";
	}
	// PAGINATE
	@GetMapping("/indexPaginate")
	public String mostrarIndexPaginado(Model model, Pageable page) {
		Page<Categoria> lista = serviceCategorias.buscarTodas(page);
		model.addAttribute("categorias", lista);
		return "categorias/listCategorias";
	}
	// @GetMapping("/create")
	@RequestMapping(value="/create", method=RequestMethod.GET)
	public String crear(Categoria categoria) {
	return "categorias/formCategoria";
	}
	
	//@RequestMapping(value="/save", method=RequestMethod.POST)
	@PostMapping("/save")
	public String guardar(Categoria categoria, BindingResult result, RedirectAttributes attributes) {
	
		if (result.hasErrors()) {
			for (ObjectError error: result.getAllErrors()){
				System.out.println("Ocurrio un error: "+ error.getDefaultMessage());
				}			
			return "categorias/formCategoria";
			}
	serviceCategorias.guardar(categoria);
	attributes.addFlashAttribute("msg", "Categoria Guardada con exito");
	System.out.println("Categoria es: "+ categoria);
	//return "categorias/listCategorias";
	return "redirect:/categorias/indexPaginate";
	}
	//Ejercicio
	@GetMapping("/delete/{id}")
	public String eliminar(@PathVariable("id") int idCategoria, RedirectAttributes attributes) {
		System.out.println("Borrando categoria con id: "+idCategoria);		
		List<Vacante> lista = serviceVacantes.buscarTodas();
		for (Vacante vacante: lista) {
			if (vacante.getCategoria().getId() == idCategoria) {
			  serviceVacantes.eliminar(vacante.getId());
			  System.out.println("dentro de if");
		    }else {
		    	System.out.println("en else ");
		    }
		}
		System.out.println("saliendo de for");
		serviceCategorias.eliminar(idCategoria);
		attributes.addFlashAttribute("msg", "La categoria fue eliminada");		 		
		return "redirect:/categorias/indexPaginate";
	}	
	@GetMapping("/editar/{id}")
	public String editar(@PathVariable("id") int idCategoria, Model model) {
		Categoria categoria = serviceCategorias.buscarPorId(idCategoria);
		model.addAttribute("categoria", categoria);
		//model.addAttribute("categorias", serviceCategorias.buscarTodas());
		return "categorias/formCategoria";		
	}
	//Ejercicio
	/// es loMISMO QUE LO SIGUIENTE:
//	// @PostMapping("/save")
//	@RequestMapping(value="/save", method=RequestMethod.POST)
//	public String guardar(@RequestParam("nombre") String nombre, @RequestParam("descripcion") String descripcion) {
//	System.out.println("Categoria: "+nombre);
//	System.out.println("Descripcion: "+descripcion);
//	return "categorias/listCategorias";
//	}
	
}
