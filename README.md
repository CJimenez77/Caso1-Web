# StockFlow

Sistema de gestión de inventario, clientes y pedidos desarrollado con Spring Boot, Thymeleaf, Spring Data JPA y PostgreSQL.

---

## Requisitos previos

* **Java:** JDK 17 o superior
* **Docker & Docker Compose** (para la base de datos)

---

## 1. Levantar el proyecto

### Paso 1: Iniciar la base de datos (PostgreSQL)

En la raíz del proyecto, ejecuta el contenedor con Docker Compose:

```bash
docker compose up -d
```

> Esto inicia un PostgreSQL 16 en el puerto `5433` con el esquema y datos iniciales cargados automáticamente.

### Paso 2: Iniciar la aplicación

Ejecuta el servidor con el wrapper de Gradle:

```bash
./gradlew bootRun
```

Una vez que termine de iniciar, accede desde el navegador a:

* **Aplicación web:** [http://localhost:8085](http://localhost:8085) (redirige al catálogo en `/productos`)

---

## 2. Ejecutar los tests

Para correr toda la suite de pruebas automatizadas:

```bash
./gradlew test
```

Los resultados se mostrarán directamente por consola detallando cada endpoint y prueba:

```text
APROBADA [✔] -> ClienteControladorTest :: GET /api/clientes -> Debe listar clientes
APROBADA [✔] -> FlujoTransaccionalPedidosTest :: POST /api/pedidos -> Debe crear pedido y descontar stock cuando hay disponibilidad
...
```

### Ejecutar una prueba específica

Si deseas ejecutar solo una clase de test puntual:

```bash
# Ejemplo: Solo pruebas de pedidos y transaccionalidad
./gradlew test --tests FlujoTransaccionalPedidosTest

# Ejemplo: Solo pruebas de clientes
./gradlew test --tests ClienteControladorTest

# Ejemplo: Solo pruebas de productos
./gradlew test --tests ProductoControladorTest
```

Para ver el reporte completo en HTML, ábrelo en tu navegador desde:
`build/reports/tests/test/index.html`
