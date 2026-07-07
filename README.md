# Práctica 1 - Spring Boot

## 1. Verificación de Java

![Java Version](assets/java-version.png)

Se verificó la una versión compatible de Java mediante el comando `java -version`.

---

## 2. Ejecución del servidor Spring Boot

![Servidor](assets/servidor.png)

La aplicación inició correctamente utilizando `./gradlew bootRun` y Tomcat quedó escuchando en el puerto 8080.

---

## 3. Endpoint /api/status

![Endpoint](assets/status-endpoint.png)

El endpoint responde correctamente en formato JSON mostrando el estado del servicio.

---

## 4. Verificación del controlador

![Controlador](assets/status-controller.png)

Se verificó la existencia del archivo `StatusController.java`.

---

## 5. Explicación

### Funcionamiento del endpoint

El endpoint `/api/status` permite comprobar que la API se encuentra en ejecución. Cuando recibe una petición GET devuelve un objeto JSON con información del servicio, estado y fecha actual.

### Función de Spring Boot

Spring Boot simplifica el desarrollo de aplicaciones backend mediante configuración automática, dependencias predefinidas y un servidor embebido Tomcat, permitiendo crear APIs REST de manera rápida y organizada.

## 6. Endopoint del controlador de estudiantes

![students](assets/student-controller.png)

Se mostro el resultado del endoint.

## 7. Endpoint del controlador de estudiantes en la ruta de `count`

![count](assets/count.png)

Se mostro el resultado del endoint.

## Práctica 5: Persistencia real con PostgreSQL, JPA y Repositorios

En esta práctica se reemplazó el almacenamiento temporal en memoria por una base de datos real PostgreSQL ejecutada mediante Docker.  
La aplicación Spring Boot se conecta a PostgreSQL usando la configuración definida en `application.yml`.

El flujo de datos inicia cuando el cliente realiza una petición HTTP al controlador REST.  
El controlador delega la operación al servicio, el servicio aplica la lógica de negocio y utiliza un repositorio JPA para comunicarse con PostgreSQL.  
El repositorio trabaja con entidades JPA, las cuales representan las tablas de la base de datos.

Para evitar exponer directamente la estructura de persistencia, se mantiene una separación entre DTOs, modelos y entidades.  
Los DTOs reciben y devuelven datos de la API, los modelos representan los datos dentro de la lógica de negocio y las entidades representan las tablas persistidas en PostgreSQL.

También se creó una superclase `BaseEntity`, la cual contiene campos comunes como `id`, `createdAt`, `updatedAt` y `deleted`.  
Esta clase permite reutilizar información de auditoría en diferentes entidades, como `UserEntity` y `ProductEntity`.

Finalmente, se implementó el módulo `products`, incluyendo entidad, repositorio, mapper, servicio, implementación del servicio y controlador.  
El CRUD completo de productos fue probado mediante API REST y los registros fueron verificados directamente en PostgreSQL con la consulta:

```sql
SELECT * FROM products;
```
![products](assets/products.png)


---

# Práctica 6: Validación de datos y manejo de errores

En esta práctica se implementó la validación de datos de entrada mediante anotaciones de Bean Validation en los DTOs, además del manejo de errores mediante excepciones personalizadas.

Se agregaron validaciones como:

- `@NotBlank` para campos obligatorios.
- `@NotNull` para valores requeridos.
- `@Size` para limitar longitud de cadenas.
- `@Min` para validar valores mínimos.
- `@DecimalMin` para validar valores numéricos.

Cuando una petición contiene datos inválidos, Spring Boot intercepta la excepción `MethodArgumentNotValidException` y devuelve una respuesta estructurada mediante `GlobalExceptionHandler`.

---

## 1. Respuesta de error por datos inválidos

![Validacion invalida](assets/validation-error.png)

Se probó el envío de un producto con información incorrecta.

La API respondió con un error HTTP 400 Bad Request indicando los campos que incumplen las reglas de validación.

---

## 2. CRUD de productos con validaciones

![CRUD validado](assets/products-validation.png)

Se verificó el comportamiento del CRUD de productos aplicando reglas de negocio:

- No permite crear productos con precio negativo.
- No permite actualizar productos eliminados.
- El método `findAll()` únicamente devuelve productos activos.

Los productos eliminados utilizan eliminación lógica mediante el campo:

```java
deleted = true
```

por lo que permanecen almacenados en la base de datos, pero dejan de estar disponibles para las consultas normales.

---

# Práctica 7: Excepciones de dominio y manejo global de errores

En esta práctica se reemplazaron las excepciones genéricas como `IllegalStateException` por excepciones específicas del dominio:

- `NotFoundException`
- `ConflictException`
- `BadRequestException`

Estas excepciones son manejadas centralizadamente mediante `GlobalExceptionHandler`, permitiendo devolver respuestas HTTP uniformes.

---

## 1. Producto inexistente

![Producto inexistente](assets/product-not-found.png)

Se realizó la petición:

```http
GET /api/products/99999
```

La API respondió:

```json
{
    "status": 404,
    "error": "Not Found",
    "message": "Product not found"
}
```

Esto ocurre cuando el producto no existe o se encuentra eliminado lógicamente.

---

## 2. Producto duplicado

![Producto duplicado](assets/product-conflict.png)

Se intentó crear un producto utilizando un nombre ya registrado:

```http
POST /api/products
```

La API respondió:

```json
{
    "status": 409,
    "error": "Conflict",
    "message": "Product name already registered"
}
```

La validación se realiza dentro del servicio antes de guardar el producto en PostgreSQL.

---

## 3. Error de validación DTO

![Error validacion DTO](assets/dto-validation.png)

Se envió información inválida:

```json
{
  "name": "",
  "price": -5,
  "stock": -1
}
```

---

# Práctica 8: Relaciones entre entidades JPA

En esta práctica se implementaron relaciones entre entidades utilizando JPA e Hibernate.

El modelo de productos fue ampliado para relacionar cada producto con:

- Un usuario propietario.
- Una categoría asociada.

Estas relaciones permiten devolver información relacionada dentro de la respuesta REST.

---

## 1. Estructura de la tabla products

Se verificó la estructura de la tabla `products` en PostgreSQL, donde se almacenan los datos del producto y las referencias hacia las entidades relacionadas.

---

## 2. Creación de producto con relaciones

![Producto relaciones](assets/product-relations.png)

Se creó un producto mediante API REST:

```json
{
    "name": "Laptop Gaming",
    "price":1200.0,
    "stock":5,
    "userId":1,
    "categoryId":1
}
```

La respuesta incluye información relacionada:

```json
{
    "id":1,
    "name":"Laptop Gaming",
    "owner":{
        "id":1,
        "name":"Usuario"
    },
    "category":{
        "id":1,
        "name":"Tecnología"
    },
    "createdAt":"2026-07-01T10:40:17"
}
```

Se evidencia:

- Objeto anidado `owner`.
- Objeto anidado `category`.
- Campos de auditoría como `createdAt` y `updatedAt`.

---

## 3. Consulta de productos por categoría

![Productos categoria](assets/products-category.png)

Consulta realizada:

```http
GET /api/products/category/1
```

La API devuelve únicamente los productos relacionados con la categoría indicada.

---

## Explicación de relaciones

La relación entre `ProductEntity` y `UserEntity` se implementa mediante:

```java
@ManyToOne
@JoinColumn(name="user_id")
private UserEntity owner;
```

Esto indica que:

- Un usuario puede tener varios productos.
- Cada producto pertenece a un único usuario.

La relación con categoría utiliza:

```java
@ManyToOne
@JoinColumn(name="category_id")
private CategoryEntity category;
```

Esto permite asociar cada producto con una categoría específica.

---

# Práctica 9: Consultas avanzadas y relaciones ManyToMany

En esta práctica se modificó la relación entre productos y categorías.

Anteriormente se tenía:

```
Product N -------- 1 Category
```

donde un producto pertenecía únicamente a una categoría.

La nueva implementación utiliza:

```
Product N -------- N Category
```

permitiendo que:

- Un producto tenga varias categorías.
- Una categoría tenga varios productos.

Para implementar esta relación se utilizó:

```java
@ManyToMany
@JoinTable(
    name="product_categories"
)
private Set<CategoryEntity> categories;
```

Hibernate genera una tabla intermedia:

```
product_categories
```

que almacena las relaciones entre ambas entidades.

---

## 1. Producto con varias categorías

![Producto varias categorias](assets/product-many-categories.png)

Se creó un producto enviando varias categorías:

```json
{
  "name": "Laptop Gaming",
  "price":1200.0,
  "stock":5,
  "userId":1,
  "categoryIds":[1,2,3]
}
```

La respuesta muestra las categorías asociadas al producto.

---

## 2. Consulta de productos filtrados por usuario

![Filtro usuario](assets/products-user-filter.png)

Consulta realizada:

```http
GET /api/users/1/products?name=laptop&minPrice=700
```

El endpoint permite obtener productos pertenecientes a un usuario aplicando filtros opcionales:

- Nombre.
- Precio mínimo.
- Precio máximo.
- Categoría.

---

## 3. Consulta de productos filtrados por categoría

![Filtro categoria](assets/products-category-filter.png)

Consulta realizada:

```http
GET /api/categories/2/products?userId=7
```

Permite consultar productos asociados a una categoría aplicando filtros adicionales.

---

## Explicación de arquitectura

Aunque los endpoints se encuentran dentro del contexto:

```
/users/{id}/products
/categories/{id}/products
```

la lógica de consulta permanece en:

```
ProductService
        |
        |
ProductRepository
```

Esto mantiene la responsabilidad correctamente separada:

- El controlador únicamente recibe peticiones HTTP.
- El servicio contiene la lógica de negocio.
- El repositorio realiza consultas a PostgreSQL.

De esta manera, aunque la ruta pertenezca al contexto de usuarios o categorías, los datos consultados pertenecen al dominio de productos.

---

## Cambio de relación Product - Category

Antes:

```
Product N -------- 1 Category
```

Cada producto solo podía tener una categoría.

Después:

```
Product N -------- N Category
```

Un producto puede pertenecer a varias categorías y una categoría puede contener varios productos.

Esto requiere una tabla intermedia:

```
product_categories
```

que almacena las asociaciones entre productos y categorías.