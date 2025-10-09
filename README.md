# TFG_basket_test
Plataforma web para la realización de test para el análisis de la toma de decisiones en jugadores de baloncesto con capacidades diversas.

# Plataforma de test para toma de decisiones en baloncesto

## Arranque rápido

### 1. Base de datos
```bash
docker compose up -d

cd backend
./mvnw spring-boot:run

cd frontend
npm install
npm run dev


### Conexion con db
Name: Local Postgres
Host name/addres: db
Port: 5432
Maintenance database: tfgdb
Username: postgres
Password: postgres