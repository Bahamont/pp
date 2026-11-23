# Microservicio CRUD NoSQL - Contactos

Microservicio REST con Spring Boot 3, Java 17 y MongoDB. Incluye validación, excepciones de negocio, pruebas JUnit/Mockito, pruebas de integración con MongoDB Testcontainers, JaCoCo y configuración de SonarQube.

## Requisitos
- Java 17
- Maven 3.9+ (o usar Maven Wrapper si se agrega)
- Docker Desktop / Docker Engine con Compose
- SonarQube Community para análisis local (opcional)

## Ejecutar con Docker Compose
Desde esta carpeta:
```bash
docker compose up -d --build
``
API: http://localhost:8080/api/contactos  
Salud: http://localhost:8080/actuator/health  
MongoDB: localhost:27017 (solo para desarrollo local).

Detener: `docker compose down`. Para borrar también los datos: `docker compose down -v`.

## Ejecutar localmente
1. Iniciar MongoDB: `docker compose up -d mongodb`.
2. Ejecutar: `mvn spring-boot:run`.

## Endpoints
| Método | Ruta | Resultado |
|---|---|---|
| POST | /api/contactos | Crear (201) |
| GET | /api/contactos | Listar (200) |
| GET | /api/contactos/{id} | Consultar (200/404) |
| PUT | /api/contactos/{id} | Actualizar (200/404/409) |
| DELETE | /api/contactos/{id} | Eliminar (204/404) |

Ejemplo POST:
```json
{"nombre":"Ana Pérez","email":"ana@example.com","telefono":"+526141234567"}
```

Errores JSON incluyen timestamp, status, error y message. Validación: 400; recurso inexistente: 404; email duplicado: 409.

## Pruebas y cobertura
```bash
mvn clean verify
``
Las pruebas unitarias usan Mockito/JUnit. Las pruebas de integración levantan MongoDB real mediante Testcontainers; Docker debe estar activo. JaCoCo genera:
`target/site/jacoco/index.html`
El build valida como objetivo mínimo 80% de cobertura de líneas y 70% de ramas. Si el umbral no se alcanza, `mvn verify` falla y se debe ampliar la batería de pruebas; no se debe declarar un porcentaje hasta ejecutar la suite.

## SonarQube
1. Levantar SonarQube Community (por ejemplo con imagen oficial `sonarqube:community`).
2. Crear un proyecto con key `contactos-nosql` y generar un token.
3. Configurar variables de entorno `SONAR_HOST_URL` y `SONAR_TOKEN`.
4. Ejecutar:
```bash
mvn clean verify
mvn sonar:sonar -Psonar
``
El Quality Gate se configura desde el servidor SonarQube. El resultado depende de la versión, reglas y perfil asignado; este repositorio no inventa un resultado de análisis.

## Estructura
- `src/main/java`: aplicación, dominio, repositorio, servicio, controlador y manejo global de excepciones.
- `src/test/java`: pruebas unitarias y de integración.
- `compose.yaml`: MongoDB y API.
- `pom.xml`: dependencias, JaCoCo y perfil SonarQube.

## Notas
El índice único MongoDB para email se crea automáticamente. La aplicación es una base académica; antes de producción se deben añadir autenticación/autorización, secretos administrados, TLS, límites de petición, auditoría y copias de seguridad.