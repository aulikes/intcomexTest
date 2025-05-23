
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
- Spring Boot 3.4.5
- Gradle
- PostgreSQL (prod)
- H2 (dev)
- Spring Data JPA
- MapStruct
- JWT + Spring Security
- Swagger / OpenAPI
- Docker (opcional para despliegue)

---

## 📦 Perfiles configurados

### 🔹 `dev` (por defecto)
- Conexión con H2 en memoria para entorno `dev` (no requiere instalación)
- Datos de prueba precargados (2 categorías + 100.000 productos)
- Acceso a consola: `http://localhost:8080/h2-console`

### 🔹 `prod`
- Configurado para PostgreSQL real
- Datos de prueba precargados (2 categorías + 100.000 productos)
- Las credenciales se definen por variables de entorno
- Usado para despliegue en la nube http://intcomex-us-east-2-testapi-env.us-east-2.elasticbeanstalk.com/

---


## 📦 Estructura del Proyecto

```text
com.intcomex.rest.api/
├── IntcomexRestApiApplication.java       → Clase principal que arranca la aplicación Spring Boot
│
├── async/                                → Componentes que se ejecutan en segundo plano
│   ├── InitialCategoryLoader.java        → Carga inicial de categorías al arrancar la app
│   ├── InitialProductLoader.java         → Carga masiva inicial de productos
│   ├── ProductBatchWorker.java           → Procesa productos por lotes desde una cola
│   └── RabbitProductCommandListener.java → Listener que consume eventos de RabbitMQ
│
├── config/                               → Configuraciones globales y específicas de Spring
│   ├── AppProperties.java                → Propiedades externas (config `app.*` del YAML)
│   ├── AppSecurityProperties.java        → Propiedades de seguridad (roles, JWT)
│   ├── CacheConfig.java                  → Configuración de caché con Caffeine
│   ├── CorsConfig.java                   → CORS: orígenes permitidos para la API
│   ├── ExecutorConfig.java               → Configuración de hilos para tareas async
│   ├── QueueConfig.java                  → Configura la cola interna para lotes de productos
│   ├── RabbitConfig.java                 → Define colas, exchanges y bindings de RabbitMQ
│   ├── SecurityConfig.java               → Define filtros, rutas públicas y seguridad general
│   ├── StaticResourceConfig.java         → Sirve imágenes estáticas desde rutas específicas
│   ├── TimeZoneFilter.java               → Filtro que inyecta zona horaria desde encabezado
│   └── ZoneContextHolder.java            → Provee acceso a la zona horaria actual del request
│
├── controller/                           → Endpoints expuestos vía HTTP (API REST)
│   ├── AuthController.java               → Login y emisión de tokens JWT
│   ├── CategoryController.java           → CRUD de categorías y carga de imagen
│   └── ProductController.java            → CRUD y paginación de productos
│
├── dto/                                  → Objetos de transferencia (DTOs) de entrada y salida
│   ├── AuthRequest / AuthResponse.java   → Login de usuarios
│   ├── CategoryCreateRequest / Response  → Alta de categorías
│   ├── ProductCreateRequest / Response   → Alta de productos
│   ├── ProductGetResponse.java           → Respuesta detallada de productos
│   ├── PaginationRequest / Response      → Paginación genérica
│   └── ErrorResponse.java                → Estructura de errores de la API
│
├── entity/                               → Entidades JPA mapeadas a tablas de PostgreSQL
│   ├── Category.java
│   └── Product.java
│
├── exception/                            → Manejo de errores centralizado
│   ├── BusinessException.java
│   ├── GlobalExceptionHandler.java       → @ControllerAdvice para errores globales
│   ├── ImagenFormatException.java
│   └── ResourceNotFoundException.java
│
├── mapper/                               → MapStruct: convierte entre entidades y DTOs
│   ├── CategoryMapper.java
│   ├── ProductReqMapper.java
│   └── ProductResMapper.java
│
├── repository/                           → Repositorios JPA para acceso a BD
│   ├── CategoryRepository.java
│   └── ProductRepository.java
│
├── security/                             → Seguridad basada en JWT
│   ├── JwtFilter.java                    → Filtro que valida el token JWT
│   └── JwtUtil.java                      → Emisión y validación de tokens
│
├── service/
│   ├── contract/                         → Interfaces de servicios (Auth, Product, Category)
│   ├── impl/                             → Implementaciones concretas de la lógica
│   ├── queue/                            → Publicadores de eventos a colas internas o Rabbit
│   └── storage/                          → Implementaciones para guardar imágenes (local y S3)
│
├── swagger/                              → Documentación Swagger con respuestas de error comunes
│   ├── DefaultErrApiResponses.java
│   ├── DefaultErrAuthResponses.java
│   └── DefaultErrClientResponses.java
│
└── util/                                 → Utilidades generales de fechas, URLs, etc.
    ├── DateTimeUtils.java
    └── UrlBuilderUtil.java
```

## 🗄️ Estructura de la base de datos 

### 🗂️ Tabla: `category`

```sql
CREATE TABLE category (
    CategoryID INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    CategoryName VARCHAR(100) UNIQUE NOT NULL,     -- Nombre único para la categoría
    Description VARCHAR(1000),                     -- Descripción extensa
    Picture VARCHAR(200)                           -- Ruta relativa de la imagen 
);
```

### 🗂️ Tabla: `product`

```sql
CREATE TABLE product (
    ProductID INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    ProductName VARCHAR(100) NOT NULL,             -- Nombre del producto
    SupplierID INTEGER,                            -- Referencia opcional a proveedor
    CategoryID INTEGER,                            -- FK a categoría
    QuantityPerUnit VARCHAR(100),                  -- Descripción del empaque (ej. 10x1L)
    UnitPrice NUMERIC(10, 2),                      -- Precio por unidad
    UnitsInStock INTEGER,                          -- Unidades actualmente en inventario
    UnitsOnOrder INTEGER,                          -- Unidades pedidas a proveedor
    ReorderLevel INTEGER,                          -- Nivel para generar nuevo pedido
    Discontinued BOOLEAN DEFAULT FALSE,            -- Si está fuera de comercialización
    CONSTRAINT fk_products_category
        FOREIGN KEY (CategoryID)
        REFERENCES category(CategoryID)
);
```


---

## 📌 Endpoints principales

| Método | Endpoint             | Descripción                                     |
|--------|----------------------|-------------------------------------------------|
| POST   | `/auth/login`        | Autenticación con usuario/contraseña            |
| POST   | `/categories`        | Crear una nueva categoría                       |
| GET    | `/categories`        | Listar las categorías                           |
| POST   | `/products`          | Crear productos con datos aleatorios            |
| GET    | `/products`          | Listar productos con paginación                 |
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

Los usuarios para entorno local están definidos directamente en el código (`application-dev.yml`) para facilitar pruebas:

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
