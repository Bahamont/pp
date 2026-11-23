package com.example.contactos.exception;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test void transformaNoEncontradoA404() {
        var response = handler.noEncontrado(new RecursoNoEncontradoException("abc"));
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(404, response.getBody().get("status"));
    }

    @Test void transformaDuplicadoA409() {
        var response = handler.duplicado(new DatoDuplicadoException("x@example.com"));
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test void transformaClaveUnicaMongoA409() {
        var response = handler.claveDuplicada(new DuplicateKeyException("duplicate"));
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test void noExponeDetallesEnErrorInesperado() {
        var response = handler.inesperado(new IllegalStateException("detalle interno"));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Ocurrió un error interno.", response.getBody().get("message"));
    }
}
