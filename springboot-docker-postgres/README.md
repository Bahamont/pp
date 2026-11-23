# Spring Boot + PostgreSQL + Docker
API REST CRUD contenerizada con PostgreSQL 16, Docker Compose y Nginx HTTPS.

## Requisitos
Docker Desktop o Docker Engine con Compose; OpenSSL para certificado local.

## Inicio
1. Copia `.env.example` como `.env` y cambia POSTGRES_PASSWORD.
2. Ejecuta `sh scripts/generar-certificado.sh` (en Windows, desde Git Bash con OpenSSL).
3. Ejecuta `docker compose up -d --build`.
4. Revisa `docker compose ps` y `docker compose logs -f app`.
5. Abre `https://localhost/actuator/health`. El navegador advertirá del certificado autofirmado.

## API
- GET /api/products
- GET /api/products/{id}
- POST /api/products (JSON: {"name":"Teclado","price":599.90})
- PUT /api/products/{id}
- DELETE /api/products/{id}
- GET /actuator/health

Ejemplo: `curl -k -X POST https://localhost/api/products -H "Content-Type: application/json" -d '{"name":"Teclado","price":599.90}'`

## Arquitectura
Nginx publica 80/443; Spring Boot escucha 8080 solo en redes Docker; PostgreSQL escucha 5432 solo en backend. El volumen postgres_data conserva datos. .env y llaves privadas no se versionan.

## Apagado
`docker compose down` conserva datos. `docker compose down -v` elimina también el volumen.

## Certificados
El certificado autofirmado solo es para laboratorio. Producción requiere dominio y certificado de CA confiable (p. ej. Let's Encrypt), renovación y resguardo seguro de llaves. No incluye autenticación ni está listo para producción.