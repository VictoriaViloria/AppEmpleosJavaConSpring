package net.itinajero.service.db;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import net.itinajero.model.Usuario;
import net.itinajero.repository.UsuariosRepository;
import net.itinajero.service.IUsuariosService;

@Service
@Primary
public class UsuariosServiceJpa implements IUsuariosService {	
	@Autowired
	private UsuariosRepository usuariosrepo;

	@Override
	public void guardar(Usuario usuario) {
		usuariosrepo.save(usuario);
	}
	@Override
	public void eliminar(Integer idUsuario) {
		usuariosrepo.deleteById(idUsuario);
	}
	@Override
	public List<Usuario> buscarTodos() {		
		return usuariosrepo.findAll();
	}
	@Override
	public Usuario buscarPorUsername(String username) {
		Usuario usuario = usuariosrepo.findByUsername(username);
		return usuario;
	}
}
