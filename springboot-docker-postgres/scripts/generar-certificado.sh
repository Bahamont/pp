#!/usr/bin/env sh
set -eu
mkdir -p certs
openssl req -x509 -nodes -days 365 -newkey rsa:2048 -keyout certs/server.key -out certs/server.crt -subj "/CN=localhost" -addext "subjectAltName=DNS:localhost,IP:127.0.0.1"
chmod 600 certs/server.key
printf "Certificado local creado en certs/. No subir la llave privada al repositorio.\n"