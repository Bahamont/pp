# Reporte técnico: Microservicio CRUD NoSQL y calidad de código

## 1. Datos generales
**Historia de usuario:** HU-01.  
**Modalidad:** Individual.  
**Estimación:** 4 horas de laboratorio.  
**Entregable:** Microservicio REST CRUD conectado a MongoDB, manejo de excepciones, pruebas automatizadas, cobertura JaCoCo y análisis SonarQube.

## 2. Propósito
Desarrollar un microservicio mantenible que administre contactos en una base NoSQL. Se utiliza Spring Boot 3 y MongoDB, con validaciones de entrada, respuestas HTTP coherentes, pruebas unitarias y de integración y herramientas de calidad.

## 3. Arquitectura y tecnologías
- Java 17 y Spring Boot 3.3.5: framework y runtime de la API.
- Spring Web: endpoints REST.
- Spring Data MongoDB: persistencia documental.
- Jakarta Validation: validación de campos.
- JUnit 5 y Mockito: pruebas unitarias.
- Spring Boot Test y Testcontainers MongoDB: pruebas de integración contra MongoDB real en contenedor.
- JaCoCo: medición de cobertura de líneas y ramas.
- SonarQube: análisis estático de bugs, vulnerabilidades y code smells.
- Docker Compose: ejecución local coordinada de MongoDB y API.

La entidad del dominio es Contacto, con identificador, nombre, email y teléfono. MongoDB almacena los documentos en la colección `contactos`; se crea un índice único para el correo electrónico.

## 4. Operaciones CRUD
| Método | Endpoint | Descripción | Respuesta esperada |
|---|---|---|---|
| POST | /api/contactos | Crea un contacto | 201 |
| GET | /api/contactos | Lista contactos | 200 |
| GET | /api/contactos/{id} | Consulta por ID | 200 o 404 |
| PUT | /api/contactos/{id} | Actualiza contacto | 200, 404 o 409 |
| DELETE | /api/contactos/{id} | Elimina por ID | 204 o 404 |

Ejemplo de JSON:
```json
{"nombre":"Ana Pérez","email":"ana@example.com","telefono":"+526141234567"}
```

## 5. Validación y manejo de excepciones
El modelo aplica @NotBlank y @Size al nombre, @Email y @NotBlank al correo, y @Pattern al teléfono. El servicio comprueba correos repetidos antes de guardar. Además, el índice único de MongoDB protege la unicidad a nivel de persistencia.

Se implementan `RecursoNoEncontradoException` y `DatoDuplicadoException`. `GlobalExceptionHandler`, anotado con @RestControllerAdvice, transforma las excepciones y errores de validación en JSON con timestamp, status, error y message:
- 400 Bad Request: datos inválidos.
- 404 Not Found: contacto inexistente.
- 409 Conflict: correo duplicado.
- 500 Internal Server Error: error no controlado, con mensaje genérico para no filtrar detalles internos.

## 6. Pruebas automatizadas
### 6.1 Unitarias
`ContactoServiceTest` utiliza JUnit 5 y Mockito para simular ContactoRepository. Cubre creación, duplicidad, listado, consulta, recurso inexistente, actualización y eliminación, incluyendo la rama de eliminación inexistente.

### 6.2 Integración
`ContactoApiIntegrationTest` inicia el contexto Spring Boot y un MongoDB 7 mediante Testcontainers. Comprueba el flujo CRUD, serialización JSON, códigos HTTP, validación 400, duplicidad 409 y recurso inexistente 404. Para ejecutar estas pruebas se requiere Docker activo.

## 7. Cobertura JaCoCo
El plugin JaCoCo se integra en Maven. El objetivo `mvn clean verify` ejecuta pruebas, genera el reporte HTML en `target/site/jacoco/index.html` y valida un mínimo configurado de 80% de líneas y 70% de ramas. Si la cobertura real no alcanza el umbral, Maven falla en la fase verify; se deben añadir pruebas en vez de alterar artificialmente el resultado.

**Resultado:** completar después de ejecutar la suite. Registrar el porcentaje real de líneas y ramas y adjuntar captura del reporte HTML. No se asigna un porcentaje sin ejecución comprobable.

## 8. Análisis estático SonarQube
Se incluyen `sonar-project.properties` y un perfil Maven `sonar`. El análisis requiere una instancia SonarQube activa, proyecto con key `contactos-nosql` y token configurado en variables de entorno `SONAR_HOST_URL` y `SONAR_TOKEN`. Comandos:
```bash
mvn clean verify
mvn sonar:sonar -Psonar
``
El Quality Gate depende del perfil y reglas configurados en la instancia. Se debe anexar una captura del panel con bugs, vulnerabilidades, code smells, cobertura y estado del Quality Gate tras ejecutar el análisis. Este reporte no afirma que el análisis remoto se haya realizado.

## 9. Ejecución con Docker
1. Abrir terminal en la carpeta del proyecto.
2. Ejecutar `docker compose up -d --build`.
3. Revisar `docker compose ps`.
4. Probar salud en `http://localhost:8080/actuator/health`.
5. Probar endpoints con Postman, Insomnia o curl.
6. Detener con `docker compose down`.

MongoDB usa un volumen nombrado `mongo_data` para persistir los documentos. La API se conecta por DNS interno al servicio `mongodb` en el puerto 27017.

## 10. Evidencias requeridas
Las capturas deben obtenerse tras ejecutar realmente el proyecto:
1. **E1 – API:** Postman/Insomnia con POST exitoso y respuesta 201.
2. **E2 – CRUD:** GET, PUT y DELETE mostrando respuestas HTTP.
3. **E3 – Errores:** validación 400, duplicado 409 y no encontrado 404 con JSON.
4. **E4 – Pruebas:** terminal con `mvn clean verify` y pruebas finalizadas.
5. **E5 – JaCoCo:** reporte HTML con porcentajes reales de líneas y ramas.
6. **E6 – SonarQube:** dashboard con métricas y Quality Gate.
7. **E7 – Docker:** `docker compose ps` mostrando API y MongoDB activos.

No se incluyen imágenes ficticias. Pegar en los espacios del PDF las capturas generadas en el equipo donde se ejecute la práctica.

## 11. Riesgos y retos
- Disponibilidad de Docker para Testcontainers y despliegue local.
- Evitar que datos duplicados se inserten simultáneamente: se combina comprobación en servicio con índice único MongoDB.
- Mantener coherencia entre validación de entrada y restricciones de base de datos.
- Alcanzar el umbral de cobertura requiere pruebas suficientes para ramas positivas y negativas.
- El Quality Gate de SonarQube depende de la configuración del servidor y no puede darse por aprobado sin análisis.

## 12. Conclusiones
El microservicio separa responsabilidades entre controlador, servicio y repositorio, utiliza MongoDB para persistencia documental y expone operaciones CRUD mediante HTTP. Las excepciones personalizadas y el manejador global entregan respuestas consistentes. JUnit, Mockito y Testcontainers permiten validar lógica y comportamiento HTTP con una base de datos real en contenedor. JaCoCo y SonarQube complementan el proceso con métricas cuantificables de cobertura y calidad estática.

La entrega incluye la configuración necesaria para ejecutar las pruebas y análisis, pero los porcentajes de cobertura y el Quality Gate deben registrarse después de ejecutar Maven y SonarQube en un entorno con Docker y servidor de análisis disponibles.

## 13. Referencias
- Spring. (s. f.). Spring Boot Reference Documentation. https://docs.spring.io/spring-boot/
- Spring. (s. f.). Spring Data MongoDB. https://docs.spring.io/spring-data/mongodb/reference/
- MongoDB. (s. f.). MongoDB Manual. https://www.mongodb.com/docs/manual/
- JUnit. (s. f.). JUnit 5 User Guide. https://junit.org/junit5/docs/current/user-guide/
- Mockito. (s. f.). Mockito documentation. https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html
- JaCoCo. (s. f.). JaCoCo Java Code Coverage Library. https://www.jacoco.org/jacoco/
- SonarSource. (s. f.). SonarQube documentation. https://docs.sonarsource.com/sonarqube/
- Testcontainers. (s. f.). Testcontainers for Java. https://java.testcontainers.org/

## Anexo: valores para la bitácora
Completar después de ejecutar:
- Fecha de ejecución: ____________________
- Resultado de `mvn clean verify`: ____________________
- Cobertura de líneas: ______ %
- Cobertura de ramas: ______ %
- Estado Quality Gate: ____________________
- Bugs / Vulnerabilidades / Code Smells: ____________________
