# Backend Productos - Similares

Microservicio reactivo con Spring Boot y WebFlux para la consulta de productos similares.

## Requisitos
* Java 21
* Maven
* Docker (para el servidor mock)

## Ejecución

### 1. Iniciar el mock externo (puerto 3001)
Desde la carpeta del repositorio `backendDevTest`:
```bash
docker compose up -d
```

### 2. Arrancar la aplicación (puerto 5000)
```bash
./mvnw spring-boot:run
```

## Pruebas

### Tests unitarios y de integración
```bash
./mvnw test
```

### Test de carga con k6 (desde backendDevTest)
```bash
docker compose run --rm k6 run /scripts/test.js
```
