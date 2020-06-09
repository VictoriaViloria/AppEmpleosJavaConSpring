package net.itinajero.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import net.itinajero.model.Categoria;
import net.itinajero.model.Solicitud;
import net.itinajero.repository.CategoriasRepository;
import net.itinajero.service.ICategoriasService;

@Service
@Primary
public class CategoriasServiceJpa implements ICategoriasService {
	
	@Autowired
	private CategoriasRepository categoriasrepo;

	@Override
	public void guardar(Categoria categoria) {
		categoriasrepo.save(categoria);
	}

	@Override
	public List<Categoria> buscarTodas() {		
		return categoriasrepo.findAll();
	}

	@Override
	public Categoria buscarPorId(Integer idCategoria) {
		Optional<Categoria> optional = categoriasrepo.findById(idCategoria);
		if (optional.isPresent()) {
			return optional.get(); 
		}
		return null;
	}

	@Override
	public void eliminar(Integer idCategoria) {
		Optional<Categoria> optional = categoriasrepo.findById(idCategoria);
		if (optional.isPresent()) {
			System.out.println("se va a eliminar la Categoria: "+optional.get());
		
		categoriasrepo.deleteById(idCategoria);
		System.out.println("Se elimino la Categoria: ");
		}
		
	}

	@Override
	public Page<Categoria> buscarTodas(Pageable page) {
		return categoriasrepo.findAll(page);
	}

	
	
}
