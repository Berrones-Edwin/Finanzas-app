# Levantar la base de datos (desde la carpeta del proyecto)
docker compose up -d

# Bajarla sin perder datos
docker compose down

# Bajarla borrando todo (útil si quieres empezar desde cero)
docker compose down -v

# Ver logs si algo falla
docker compose logs mysql


# Flujo dia a dia

# 1. Levanta MySQL
docker compose up -d

# 2. Verifica que esté listo (opcional pero útil al principio)
docker compose logs mysql

# 3. Arranca tu app de Spring Boot
./mvnw spring-boot:run

sudo systemctl start postgresql


sudo systemctl stop postgresql

ALTER USER postgres PASSWORD 'password';


# OPEN API
http://localhost:8080/swagger-ui/index.html
http://localhost:8080/v3/api-docs