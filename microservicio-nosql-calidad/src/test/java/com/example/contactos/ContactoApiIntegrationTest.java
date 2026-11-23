package com.example.contactos;
import com.example.contactos.domain.Contacto;
import com.example.contactos.repository.ContactoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import static org.junit.jupiter.api.Assertions.*;
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ContactoApiIntegrationTest {
    @Container static final MongoDBContainer mongo = new MongoDBContainer("mongo:7.0");
    @DynamicPropertySource static void mongoProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongo::getReplicaSetUrl);
    }
    @LocalServerPort int port;
    @Autowired TestRestTemplate rest;
    @Autowired ContactoRepository repository;
    String url() { return "http://localhost:" + port + "/api/contactos"; }
    @BeforeEach void clean() { repository.deleteAll(); }
    @Test void flujoCrudCompleto() {
        Contacto nuevo = new Contacto("Luis García","luis@example.com","6141234567");
        ResponseEntity<Contacto> creado = rest.postForEntity(url(), nuevo, Contacto.class);
        assertEquals(HttpStatus.CREATED, creado.getStatusCode());
        assertNotNull(creado.getBody());
        String id = creado.getBody().getId();
        assertNotNull(id);
        ResponseEntity<Contacto[]> lista = rest.getForEntity(url(), Contacto[].class);
        assertEquals(HttpStatus.OK, lista.getStatusCode());
        assertEquals(1, lista.getBody().length);
        Contacto cambios = new Contacto("Luis Modificado","luis@example.com","6147654321");
        rest.put(url()+"/"+id, cambios);
        ResponseEntity<Contacto> actualizado = rest.getForEntity(url()+"/"+id, Contacto.class);
        assertEquals("Luis Modificado", actualizado.getBody().getNombre());
        rest.delete(url()+"/"+id);
        assertEquals(HttpStatus.NOT_FOUND, rest.getForEntity(url()+"/"+id, String.class).getStatusCode());
    }
    @Test void validaEntradaIncorrectaCon400() {
        String json = "{\"nombre\":\"\",\"email\":\"correo-invalido\",\"telefono\":\"x\"}";
        ResponseEntity<String> response = rest.postForEntity(url(), new HttpEntity<>(json, jsonHeaders()), String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("message"));
    }
    @Test void correoDuplicadoResponde409() {
        Contacto contacto = new Contacto("Eva","eva@example.com","6141234567");
        rest.postForEntity(url(), contacto, Contacto.class);
        ResponseEntity<String> duplicate = rest.postForEntity(url(), contacto, String.class);
        assertEquals(HttpStatus.CONFLICT, duplicate.getStatusCode());
    }
    @Test void recursoNoEncontradoResponde404ConJson() {
        ResponseEntity<String> response = rest.getForEntity(url()+"/inexistente", String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().contains("message"));
    }
    private HttpHeaders jsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}