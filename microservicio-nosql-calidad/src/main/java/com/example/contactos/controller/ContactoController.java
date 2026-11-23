package com.example.contactos.controller;
import com.example.contactos.domain.Contacto;
import com.example.contactos.service.ContactoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/contactos")
public class ContactoController {
    private final ContactoService service;
    public ContactoController(ContactoService service) { this.service = service; }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public Contacto crear(@Valid @RequestBody Contacto contacto) { return service.crear(contacto); }
    @GetMapping public List<Contacto> listar() { return service.listar(); }
    @GetMapping("/{id}") public Contacto obtener(@PathVariable String id) { return service.obtener(id); }
    @PutMapping("/{id}") public Contacto actualizar(@PathVariable String id, @Valid @RequestBody Contacto contacto) {
        return service.actualizar(id, contacto);
    }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable String id) { service.eliminar(id); }
}