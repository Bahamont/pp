package com.example.contactos.exception;
public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String id) { super("No se encontró el contacto con id: " + id); }
}