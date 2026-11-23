package com.example.contactos.domain;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
@Document(collection = "contactos")
public class Contacto {
    @Id
    private String id;
    @NotBlank @Size(max = 100)
    private String nombre;
    @NotBlank @Email @Size(max = 150)
    @Indexed(unique = true)
    private String email;
    @NotBlank @Pattern(regexp = "^[0-9+()\\- ]{7,20}$", message = "El teléfono debe contener entre 7 y 20 caracteres válidos")
    private String telefono;
    public Contacto() {}
    public Contacto(String nombre, String email, String telefono) {
        this.nombre = nombre; this.email = email; this.telefono = telefono;
    }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
}