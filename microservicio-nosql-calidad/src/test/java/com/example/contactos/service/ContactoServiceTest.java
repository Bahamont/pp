package com.example.contactos.service;
import com.example.contactos.domain.Contacto;
import com.example.contactos.exception.DatoDuplicadoException;
import com.example.contactos.exception.RecursoNoEncontradoException;
import com.example.contactos.repository.ContactoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class ContactoServiceTest {
    @Mock ContactoRepository repository;
    @InjectMocks ContactoService service;
    Contacto contacto;
    @BeforeEach void setup() { contacto = new Contacto("Ana Pérez","ana@example.com","+526141234567"); }
    @Test void crearContactoCorrectamente() {
        when(repository.existsByEmail(contacto.getEmail())).thenReturn(false);
        when(repository.save(contacto)).thenReturn(contacto);
        assertSame(contacto, service.crear(contacto));
        verify(repository).save(contacto);
    }
    @Test void rechazaCorreoDuplicado() {
        when(repository.existsByEmail(contacto.getEmail())).thenReturn(true);
        assertThrows(DatoDuplicadoException.class, () -> service.crear(contacto));
        verify(repository, never()).save(any());
    }
    @Test void listaContactos() {
        when(repository.findAll()).thenReturn(List.of(contacto));
        assertEquals(1, service.listar().size());
    }
    @Test void obtieneContactoExistente() {
        when(repository.findById("1")).thenReturn(Optional.of(contacto));
        assertSame(contacto, service.obtener("1"));
    }
    @Test void lanzaExcepcionCuandoNoExiste() {
        when(repository.findById("404")).thenReturn(Optional.empty());
        assertThrows(RecursoNoEncontradoException.class, () -> service.obtener("404"));
    }
    @Test void actualizaContacto() {
        when(repository.findById("1")).thenReturn(Optional.of(contacto));
        when(repository.save(contacto)).thenReturn(contacto);
        Contacto datos = new Contacto("Ana Actualizada","ana@example.com","6141234567");
        assertEquals("Ana Actualizada", service.actualizar("1", datos).getNombre());
        verify(repository).save(contacto);
    }
    @Test void eliminaContactoExistente() {
        when(repository.existsById("1")).thenReturn(true);
        service.eliminar("1");
        verify(repository).deleteById("1");
    }
    @Test void noEliminaContactoInexistente() {
        when(repository.existsById("404")).thenReturn(false);
        assertThrows(RecursoNoEncontradoException.class, () -> service.eliminar("404"));
        verify(repository, never()).deleteById(anyString());
    }
}