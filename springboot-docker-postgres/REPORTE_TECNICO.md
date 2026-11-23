# REPORTE TÉCNICO
## Implementación de contenedores con Spring Boot, Docker y PostgreSQL

**Proyecto:** Despliegue de una aplicación web contenerizada  
**Tecnologías:** Java 17, Spring Boot 3.3.5, Maven, Docker, Docker Compose, PostgreSQL 16 y Nginx  
**Fecha:** 22 de noviembre de 2026

## 1. Introducción
La práctica consiste en empaquetar una aplicación web y su base de datos en servicios aislados y reproducibles. Se utiliza una API REST de productos con Spring Boot, PostgreSQL como almacenamiento, Docker Compose para coordinar servicios y Nginx como proxy inverso con TLS.

## 2. Contenedores utilizados e implementación
Un contenedor es una unidad ejecutable que empaqueta aplicación y dependencias, aislada mediante funciones del sistema operativo. Comparte el kernel del host y suele requerir menos recursos que una máquina virtual.

Se eligió Docker por su portabilidad, imágenes, redes, volúmenes y facilidad de orquestación con Compose. Se definen tres servicios:
- **app:** Dockerfile multi-stage. Maven compila el proyecto y la etapa final ejecuta el JAR con Java 17 JRE. Se ejecuta como usuario sin privilegios.
- **db:** PostgreSQL 16 Alpine, configurado mediante variables de entorno.
- **proxy:** Nginx Alpine, recibe conexiones HTTP/HTTPS y reenvía las solicitudes a Spring Boot.

El despliegue se realiza con `docker compose up -d --build`. La aplicación espera el healthcheck de PostgreSQL mediante depends_on con condition service_healthy.

## 3. Configuración de infraestructura (host)
El host ejecuta Docker Engine/Docker Desktop y Docker Compose. Los servicios se localizan por DNS interno; Spring Boot se conecta a `db:5432`.

### 3.1 Puertos y redes
| Componente | Puerto | Exposición |
|---|---:|---|
| Nginx HTTP | 80/tcp | Publicado; redirige a HTTPS |
| Nginx HTTPS | 443/tcp | Publicado |
| Spring Boot | 8080/tcp | Solo red Docker |
| PostgreSQL | 5432/tcp | Solo red interna backend |

La red **backend** es interna y conecta app con db. La red **frontend** conecta proxy con app. PostgreSQL no publica su puerto al host, reduciendo su exposición.

### 3.2 Variables de entorno
`.env.example` documenta POSTGRES_DB, POSTGRES_USER y POSTGRES_PASSWORD. Compose entrega los valores a PostgreSQL y crea DB_URL, DB_USER y DB_PASSWORD para Spring Boot. El archivo real .env se excluye del repositorio; se debe crear localmente y usar una contraseña robusta.

### 3.3 Persistencia
El volumen nombrado `postgres_data` se monta en `/var/lib/postgresql/data`. Los datos sobreviven a la recreación de contenedores. `docker compose down -v` elimina el volumen y sus datos.

### 3.4 Aplicación y balanceo
La API CRUD se encuentra en `/api/products`; `/actuator/health` permite revisar salud. Spring Data JPA administra la persistencia. Hibernate actualiza el esquema para esta demostración; en producción se recomienda Flyway o Liquibase. No se configura balanceador de carga porque se utiliza una sola instancia; Nginx funciona como proxy inverso.

## 4. Certificados de seguridad
Nginx utiliza TLS 1.2/1.3. Para el laboratorio, `scripts/generar-certificado.sh` crea un certificado autofirmado RSA de 2048 bits para localhost con SAN localhost y 127.0.0.1. Nginx monta los archivos desde certs/ y escucha en 443; el puerto 80 redirige a HTTPS.

El certificado autofirmado cifra la conexión, pero el navegador no confía en su identidad y mostrará una advertencia. Es válido para pruebas locales, no para un servicio público. Producción requiere certificado de una CA confiable (por ejemplo Let's Encrypt), dominio real, renovación automática y almacenamiento seguro de la llave privada. Los archivos de certificado y llave están excluidos del control de versiones.

## 5. Procedimiento de despliegue
1. Instalar Docker y Compose.
2. Entrar a `springboot-docker-postgres`.
3. Copiar .env.example como .env y cambiar la contraseña.
4. Ejecutar `sh scripts/generar-certificado.sh`.
5. Ejecutar `docker compose up -d --build`.
6. Verificar `docker compose ps` y `docker compose logs -f app`.
7. Abrir `https://localhost/actuator/health`.
8. Probar POST y GET en `/api/products`.

## 6. Evidencia de funcionamiento
**Nota de honestidad técnica:** el repositorio incluye la implementación y los pasos para reproducirla. Las capturas deben obtenerse en el equipo donde se ejecuten los contenedores; no se agregan imágenes ficticias ni se afirma que el despliegue remoto ya se haya ejecutado.

Anexar las siguientes evidencias:
- **E1. Contenedores activos:** captura de `docker compose ps`, con db, app y proxy.
- **E2. Construcción:** salida de `docker compose up -d --build`.
- **E3. Logs:** `docker compose logs --tail=50 app`, mostrando conexión correcta.
- **E4. HTTPS:** navegador en `https://localhost/actuator/health`; explicar la advertencia del certificado autofirmado.
- **E5. API:** respuesta de GET /api/products y creación mediante POST.
- **E6. Persistencia:** crear un producto, reiniciar con `docker compose restart` y comprobar que permanece.

| Etapa | Comando | Resultado a registrar |
|---|---|---|
| Construcción | docker compose build | Imagen app construida |
| Inicio | docker compose up -d | Servicios iniciados |
| Salud | docker compose ps | db healthy y servicios activos |
| HTTPS | GET /actuator/health | Respuesta UP |
| CRUD | POST y GET /api/products | Registro creado y consultado |
| Persistencia | restart y nuevo GET | Registro conservado |

## 7. Retos técnicos y planificación
Los retos principales son coordinar el arranque, usar DNS interno de Docker, proteger credenciales, conservar datos y configurar TLS. Se atienden con healthcheck, redes separadas, variables de entorno, volumen nombrado y certificado local generado fuera del repositorio.

Plan: (1) preparación de herramientas; (2) compilación e imagen; (3) redes, volumen y servicios; (4) certificado y Nginx; (5) validación de salud, CRUD y persistencia; (6) capturas y bitácora de incidencias.

## 8. Conclusiones
La contenerización entrega aplicación, base de datos y proxy con una configuración reproducible. Compose simplifica el despliegue, las redes reducen la exposición de PostgreSQL y Spring Boot, el volumen conserva información y Nginx centraliza HTTPS. Es una base académica; antes de producción se requieren certificados confiables, secretos administrados, respaldos, migraciones, autenticación, monitoreo y pruebas de carga.

## 9. Referencias
- Docker. (s. f.). *Docker Compose*. https://docs.docker.com/compose/
- Docker. (s. f.). *Dockerfile reference*. https://docs.docker.com/reference/dockerfile/
- Spring. (s. f.). *Spring Boot Documentation*. https://docs.spring.io/spring-boot/
- PostgreSQL Global Development Group. (s. f.). *PostgreSQL Documentation*. https://www.postgresql.org/docs/
- Nginx. (s. f.). *NGINX documentation*. https://nginx.org/en/docs/
- Let's Encrypt. (s. f.). *Getting started*. https://letsencrypt.org/getting-started/