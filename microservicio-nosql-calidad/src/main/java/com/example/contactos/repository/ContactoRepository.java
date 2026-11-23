package com.example.contactos.repository;
import com.example.contactos.domain.Contacto;
import org.springframework.data.mongodb.repository.MongoRepository;
public interface ContactoRepository extends MongoRepository<Contacto, String> {
    boolean existsByEmail(String email);
}