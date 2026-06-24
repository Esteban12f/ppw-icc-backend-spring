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
