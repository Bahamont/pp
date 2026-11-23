package com.example.contactos.service;
import com.example.contactos.domain.Contacto;
import com.example.contactos.exception.DatoDuplicadoException;
import com.example.contactos.exception.RecursoNoEncontradoException;
import com.example.contactos.repository.ContactoRepository;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
public class ContactoService {
    private final ContactoRepository repository;
    public ContactoService(ContactoRepository repository) { this.repository = repository; }
    public Contacto crear(Contacto contacto) {
        if (repository.existsByEmail(contacto.getEmail())) throw new DatoDuplicadoException(contacto.getEmail());
        try { return repository.save(contacto); }
        catch (DuplicateKeyException ex) { throw new DatoDuplicadoException(contacto.getEmail()); }
    }
    public List<Contacto> listar() { return repository.findAll(); }
    public Contacto obtener(String id) {
        return repository.findById(id).orElseThrow(() -> new RecursoNoEncontradoException(id));
    }
    public Contacto actualizar(String id, Contacto datos) {
        Contacto actual = obtener(id);
        if (!actual.getEmail().equals(datos.getEmail()) && repository.existsByEmail(datos.getEmail()))
            throw new DatoDuplicadoException(datos.getEmail());
        actual.setNombre(datos.getNombre());
        actual.setEmail(datos.getEmail());
        actual.setTelefono(datos.getTelefono());
        try { return repository.save(actual); }
        catch (DuplicateKeyException ex) { throw new DatoDuplicadoException(datos.getEmail()); }
    }
    public void eliminar(String id) {
        if (!repository.existsById(id)) throw new RecursoNoEncontradoException(id);
        repository.deleteById(id);
    }
}