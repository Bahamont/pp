package com.example.contactos.exception;
public class DatoDuplicadoException extends RuntimeException {
    public DatoDuplicadoException(String email) { super("Ya existe un contacto con el correo: " + email); }
}