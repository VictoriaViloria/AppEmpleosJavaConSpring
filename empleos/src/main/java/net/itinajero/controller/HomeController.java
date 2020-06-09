package net.itinajero.controller;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import net.itinajero.model.Perfil;
import net.itinajero.model.Usuario;
import net.itinajero.model.Vacante;
import net.itinajero.service.ICategoriasService;
import net.itinajero.service.IUsuariosService;
import net.itinajero.service.IVacantesService;

@Controller
public class HomeController {	
	@Autowired
	private IVacantesService serviceVacantes;
	@Autowired
	private IUsuariosService serviceUsuarios;	
	@Autowired
	private ICategoriasService serviceCategorias;
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@GetMapping("/tabla")
	public String mostrarTabla(Model model) {
		List<Vacante> lista = serviceVacantes.buscarTodas();
		model.addAttribute("vacantes", lista);
		return "tabla";
	}	
	@GetMapping("/detalle")
	public String mostrarDetalle(Model model) {
		Vacante vacante = new Vacante();
		vacante.setNombre("Ingeniero en comunicaciones");
		vacante.setDescripcion("se solicita ingeniero para dar soporte a internet");
		vacante.setFecha(new Date());
		vacante.setSalario(97000.0);
		model.addAttribute("vacante", vacante);
		return "detalle";			
		
	}	
	@GetMapping("/listado")
	public  String mostrarListado(Model model) {
		List<String> lista = new LinkedList<String>();
		lista.add("Ingeniero en Sistemas");
		lista.add("Auxiliar de contabilidad");
		lista.add("Vendedor");
		lista.add("Arquitecto");
		
		model.addAttribute("empleos", lista);
		
		return "listado";		
	}	
	@GetMapping("/")
	public String mostrarHome(Model model) {
		//List<Vacante> lista = serviceVacantes.buscarTodas();
		//model.addAttribute("vacantes", lista);
		return "home";
	}
	
	@GetMapping("/index")
	public String mostrarIndex(Authentication auth, HttpSession session) {
	//Authentication interfaz contiene muchos metodos para ontener informacion del usuario que inicia session
		// Como el usuario ya ingreso, ya podemos agregar a la session el objeto usuario.
		String username = auth.getName();
		System.out.println("Nombre del usuario: "+username);
		
		for (GrantedAuthority rol: auth.getAuthorities()) {
			System.out.println("ROL: "+rol.getAuthority());
		}
		if (session.getAttribute("usuario") == null) {
			Usuario usuario = serviceUsuarios.buscarPorUsername(username);
			usuario.setPassword(null);
			System.out.println("Usuario: "+usuario);
			session.setAttribute("usuario", usuario);
		}
		
		return "redirect:/";
	}
	//Ejercicio
	@GetMapping("/signup")
	public String registrarse(Usuario usuario) {
		return "formRegistro";
	}
	//*** Método que guarda en la base de datos el usuario registrado
	@PostMapping("/signup")
	public String guardarRegistro(Usuario usuario, RedirectAttributes attributes) {
		
		String pwdPlano = usuario.getPassword();
		String pwdEncriptado = passwordEncoder.encode(pwdPlano);
		usuario.setPassword(pwdEncriptado);
		
		usuario.setEstatus(1); // Activado por defecto
		usuario.setFechaRegistro(new Date()); // Fecha de Registro, la fecha actual del servidor		
		// Creamos el Perfil que le asignaremos al usuario nuevo
		Perfil perfil = new Perfil();
		perfil.setId(3); // Perfil USUARIO
		usuario.agregar(perfil);		
		/**		 * Guardamos el usuario en la base de datos. El Perfil se guarda automaticamente 		 */
		serviceUsuarios.guardar(usuario);				
		attributes.addFlashAttribute("msg", "El registro fue guardado correctamente!");		
		//return "redirect:/usuarios/index";
		return "redirect:/login";
	}	
	//termina Ejercicio
	@GetMapping("/search")
	public String buscar(@ModelAttribute("search") Vacante vacante, Model model) {
		System.out.println("Buscando por: "+vacante);
		/**
		 * La busqueda de vacantes desde el formulario debera de ser únicamente en Vacantes con estatus 
		 * "Aprobada". Entonces forzamos ese filtrado.
		 */
		vacante.setEstatus("Aprobada");
		
		// Personalizamos el tipo de busqueda...
		ExampleMatcher matcher = ExampleMatcher.matching().
				//where descripcion like '%?%'
				withMatcher("descripcion", ExampleMatcher.GenericPropertyMatchers.contains());
		
		Example<Vacante> example = Example.of(vacante, matcher);
		List<Vacante> lista = serviceVacantes.buscarByExample(example);
		model.addAttribute("vacantes", lista);
		return "home";
	}
	
	@GetMapping("/login" )
	public String mostrarLogin() {
	return "formLogin";
	}
	
	@GetMapping("/logout")
	public String logout(HttpServletRequest request){
	SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
	System.out.println("getContextPath() : "+request.getContextPath() );
	System.out.println("getLocalName() : "+ request.getLocalName());
	System.out.println("getLocalPort() : "+ request.getLocalPort());
	System.out.println("getMethod() : "+ request.getMethod());
	System.out.println("getPathInfo() : "+ request.getPathInfo());
	System.out.println("getPathInfo() : "+ request.getQueryString());
	System.out.println("getRemoteUser() : "+ request.getRemoteUser());
	System.out.println("getRequestedSessionId() : "+ request.getRequestedSessionId());
	System.out.println("getServerName() : "+ request.getServerName());
	System.out.println("getServerPort() : "+ request.getServerPort());
	System.out.println("getServletPath() : "+ request.getServletPath());
	System.out.println("getSession() ////: "+ request.getSession());//
	//System.out.println(" : "+ request.);
	logoutHandler.logout(request, null, null);
	return "redirect:/";
	}
	
	@GetMapping("/bcrypt/{texto}")
	@ResponseBody  //para enviar un texto a la web en lugar de renderizar una vista
	public String encriptar(@PathVariable("texto") String texto) {
		return texto + "  Encriptado en Bcrypt:  "+passwordEncoder.encode(texto);
	}
	
/** InitBinder para Strings si los detecta vacios en el Data Binding los settea
 *  a NULL @param binder**/	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
	}
	
	@ModelAttribute
	public void setGenericos(Model model) {
		Vacante vacanteSearch = new Vacante();
		vacanteSearch.reset();
		model.addAttribute("vacantes", serviceVacantes.buscarDestacadas());
		model.addAttribute("categorias", serviceCategorias.buscarTodas());
		model.addAttribute("search", vacanteSearch);
	}
	/**Metodo que regresa una lista deobjetos de tipo Vacante **/
//	private List<Vacante> getVacantes(){
//	SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
//	List<Vacante> lista = new LinkedList<Vacante>();		
//	return lista;
//	}
// 	@InitBinder
// 	public void initBinder(WebDataBinder webDataBinder) {
// 	SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
// 	webDataBinder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
// 	}
	
}
