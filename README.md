
# INCOMEX REST API

API REST desarrollada en Spring Boot para la gestión de productos y categorías. Esta API simula una infraestructura empresarial capaz de soportar altas cargas, integraciones en la nube, y una estructura modular, clara y escalable.

---

## 🧾 Requisitos del reto técnico

- Exponer endpoints REST para:
  - Crear categorías (`POST /categories`)
  - Crear productos (`POST /products`)
  - Listar productos con paginación (`GET /products`)
  - Buscar producto por ID e incluir imagen de su categoría (`GET /products/{id}`)
- Crear 2 categorías por defecto: `SERVIDORES` y `CLOUD`
- Insertar 100.000 productos aleatorios asociados a esas categorías
- Desplegar en un servicio de cloud computing
- Documentar el API con Swagger/OpenAPI
- Proveer código fuente en GitHub y una URL pública funcionando

---

## ⚙️ Tecnologías utilizadas

- Java 21
- Spring Boot 3.2
- Gradle
- PostgreSQL (prod)
- H2 (dev)
- Spring Data JPA
- MapStruct
- JWT + Spring Security
- Swagger / OpenAPI
- Docker (opcional para despliegue)

---

## 📁 Estructura del proyecto

Organización del código fuente siguiendo buenas prácticas de arquitectura limpia:

- `controller` – Exposición de endpoints
- `service/contract` – Interfaces de servicios
- `service/impl` – Lógica de negocio
- `entity` – Entidades JPA
- `dto` – Objetos de transferencia de datos
- `mapper` – Conversión entre entidades y DTOs
- `repository` – Acceso a datos con Spring Data JPA
- `config` – Configuración de seguridad, CORS, Swagger, carga inicial, etc.

---

## 📦 Perfiles configurados

### 🔹 `dev` (por defecto)
- Conexión con H2 en memoria para entorno `dev` (no requiere instalación)
- Datos de prueba precargados (categorías + 100.000 productos)
- Acceso a consola: `http://localhost:8080/h2-console`

### 🔹 `prod`
- Configurado para PostgreSQL real
- Las credenciales se definen por variables de entorno
- Usado para despliegue en la nube http://incomex-api.us-east-2.elasticbeanstalk.com/

---

## 📌 Script para la Base de Datos

-- Tabla: Categorias
CREATE TABLE CATEGORY (
CategoryID INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
CategoryName VARCHAR(100) UNIQUE NOT NULL,
Description VARCHAR(1000),
Picture VARCHAR(2000)
);

-- Tabla: Productos
CREATE TABLE PRODUCT (
ProductID INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
ProductName VARCHAR(100) NOT NULL,
SupplierID INTEGER,
CategoryID INTEGER,
QuantityPerUnit VARCHAR(100),
UnitPrice NUMERIC(10, 2),
UnitsInStock INTEGER,
UnitsOnOrder INTEGER,
ReorderLevel INTEGER,
Discontinued BOOLEAN DEFAULT FALSE,
CONSTRAINT fk_products_category FOREIGN KEY (CategoryID) REFERENCES Categories(CategoryID)
);



---

## 📌 Endpoints principales

| Método | Endpoint             | Descripción                                    |
|--------|----------------------|------------------------------------------------|
| POST   | `/auth/login`        | Autenticación con usuario/contraseña          |
| POST   | `/categories`        | Crear una nueva categoría                     |
| POST   | `/products`          | Crear productos con datos aleatorios          |
| GET    | `/products`          | Listar productos con paginación               |
| GET    | `/products/{id}`     | Obtener producto por ID con imagen de categoría |

---

## 🔐 Seguridad y Autenticación
### Autenticación paso a paso

1. Realiza login para obtener un token JWT:
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

2. Copia el token (`accessToken`) de la respuesta.

3. Usa el token para consumir cualquier endpoint protegido. Ejemplo:
```bash
curl -X GET http://localhost:8080/products \
  -H "Authorization: Bearer <TOKEN>"
```


Esta API implementa seguridad mediante **JWT (JSON Web Tokens)**.

### Usuarios configurados

Los usuarios están definidos directamente en el código (`application.yml`) para facilitar pruebas:

| Usuario | Contraseña |
|---------|------------|
| admin   | admin123   |
| user    | user123    |

### ¿Por qué están quemados los usuarios?

Los usuarios están quemados para cumplir con los siguientes objetivos del reto:
1. **Evitar complejidad innecesaria**: No se requería un sistema de autenticación completo con base de datos ni OAuth2.
2. **Simplificar pruebas**: Cualquier tester puede iniciar sesión sin configuración extra.
3. **Mantener enfoque en arquitectura y rendimiento**: La seguridad se demuestra funcionalmente, sin desviar el foco hacia persistencia de usuarios.

> ⚠️ En un entorno real, estos usuarios serían reemplazados por una base de datos, LDAP, IdP externo, etc.

---

## 🎯 Ejemplo de sesión con JWT

### Paso 1: Login
Recibe el Token por medio del login
```http
POST /auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

### Paso 2: Consumir un endpoint protegido
Utiliza el Token antes generado. Colocarlo en el Header de la petición.
```http
GET /products
Authorization: Bearer <token_jwt_obtenido>
```

---

## 📄 Documentación Swagger (OpenAPI)

La API cuenta con documentación generada automáticamente por `springdoc-openapi`. Accede a la interfaz web en:

```
http://localhost:8080/swagger-ui/index.html
```

Incluye:
- Descripción de todos los endpoints
- Ejemplos de uso
- Códigos de respuesta HTTP
- Modelos de entrada y salida

---


## ▶️ Ejecución local y prueba rápida

### Requisitos
- Java 21
- Gradle (incluye wrapper)

### Pasos para levantar en entorno de desarrollo

```bash
git clone https://github.com/aulikes/intcomexTest.git
cd testIntcomex
./gradlew bootRun --args='--spring.profiles.active=dev'
```

La aplicación quedará disponible en: [http://localhost:8080](http://localhost:8080)

> El entorno `dev` usa una base de datos H2 en memoria y no requiere instalación adicional.

### ¿Qué se carga automáticamente?
- Dos categorías: `SERVIDORES` y `CLOUD`.
- 100.000 productos generados aleatoriamente asociados a esas categorías.

La aplicación estará disponible en:
```
http://localhost:8080
```

Consola H2:
```
http://localhost:8080/h2-console
```
- JDBC URL: `jdbc:h2:mem:intcomex-db`
- Usuario: `sa`
- Contraseña: *(vacía)*


---

## ✅ Criterios de calidad aplicados

- Arquitectura limpia y desacoplada
- Uso de DTOs y mappers para separar capas
- Pruebas unitarias con cobertura de servicios
- Código comentado y legible
- Documentación automática con Swagger
- Seguridad básica con JWT
- Generación de carga masiva de productos eficiente

---

---

## 🌐 Pruebas rápidas con cURL

### 🔐 Login
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

### 📂 Crear categoría
```bash
curl -X POST http://localhost:8080/categories \
  -H "Authorization: Bearer <TOKEN>" \
  -F "name=NUEVA" \
  -F "description=Ejemplo" \
  -F "image=@ruta/local/imagen.jpg"
```

### 📋 Listar productos
```bash
curl -X GET "http://localhost:8080/products?page=0&size=20" \
  -H "Authorization: Bearer <TOKEN>"
```

### 🔍 Obtener producto por ID
```bash
curl -X GET http://localhost:8080/products/1 \
  -H "Authorization: Bearer <TOKEN>"
```

---

## ⚡️ Decisiones Técnicas y Arquitectura

### Procesamiento masivo y concurrencia controlada
El sistema usa **ArrayBlockingQueue** y un pool de workers para manejar la creación masiva y las cargas concurrentes de productos, permitiendo manejar altas cantidades de requests por segundo.

### Carga inicial optimizada
Los productos aleatorios se generan y encolan por lotes, nunca toda la data en memoria a la vez, lo que permite escalar a 100.000 productos o más sin afectar la estabilidad.

### Paginación híbrida Page/Slice y control de totales
El endpoint `/products` le permite decidir al frontend si requiere totales (`withTotals=true` para `Page`, de lo contrario usa `Slice`), optimizando el rendimiento en navegación incremental o scroll infinito.

### Cacheo inteligente de endpoints GET
Se utiliza Spring Cache con Caffeine para cachear productos por ID y páginas, y se aplica `@CacheEvict` al crear/editar/eliminar para evitar inconsistencias.

### Validaciones y logs
Todos los endpoints usan DTOs validados con `@Valid`.  
Logs explícitos de rechazos por cola llena y de procesamiento exitoso.

### Carga y almacenamiento de imágenes
En desarrollo, las imágenes se guardan localmente.  
En producción, se usó S3.

---
## 🚦 Cómo correr las pruebas

Ejecuta los tests unitarios del proyecto con:

```bash
./gradlew test
```

- Los resultados aparecen en consola y en `build/reports/tests/test/index.html`.
- Puedes ejecutarlos desde IntelliJ IDEA o cualquier IDE compatible con Gradle.
