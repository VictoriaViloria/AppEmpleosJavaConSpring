package net.itinajero.service;

import java.util.List;

import net.itinajero.model.Usuario;

public interface IUsuariosService {
	/**Ejercicio: Implementar metodo para registrar un usuario nuevo
	 * 1. Usar la plantilla del archivo formRegistro.html
	 * 2. El metodo para mostrar el formulario para registrar y el metodo para guardar el usuario  debera 	 *    estar en el Controlador  HomeController 
	 * 3. Al guardar el usuario se le asignara el perfil USUARIO y la fecha de REgistro	 *    sera la fecha actual del sisytema
	 *    @param usuario	 */
	void guardar(Usuario usuario);	
	//Ejercicio: Metodo que elimina un usuario de la base de datos
	void eliminar(Integer idUsuario);
	// Ejercicio: Implementar metodo que recupera todos los usuarios, Usar vista de listUsuarios.html
	List<Usuario> buscarTodos();
	
	Usuario buscarPorUsername(String username);
	}
//Agregar el archivo menu.html el link para acceder al listado de Usuarios y cnfigurar el link del boton Registrarse
