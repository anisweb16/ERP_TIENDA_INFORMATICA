# 🖥️ ERP Tienda Informática

> Sistema de gestión empresarial (ERP) para una tienda de informática/gaming, desarrollado como proyecto académico en el ciclo formativo de **Desarrollo de Aplicaciones Multiplataforma (DAM)**.

---

## 📋 Descripción

Aplicación web ERP completa que simula la gestión interna de una tienda de informática con temática gaming. Permite administrar clientes, ventas, pedidos y visualizar métricas del negocio a través de un dashboard interactivo.

---

## 🚀 Tecnologías utilizadas

| Capa | Tecnología |
|------|-----------|
| Backend | Java · Spring Boot |
| Frontend | Thymeleaf · HTML · CSS |
| Base de datos | MySQL |
| Estilos | Bootstrap |
| Herramienta de construcción | Maven |
| IDE | IntelliJ IDEA |

---

## ✨ Funcionalidades principales

- **Dashboard** — Vista general con métricas y estadísticas del negocio
- **Gestión de clientes** — Alta, consulta, edición y baja de clientes
- **Gestión de ventas** — Registro y seguimiento de ventas realizadas
- **Gestión de pedidos** — Control del estado de los pedidos
- **Interfaz gaming** — Diseño visual progresivo con estética gaming mediante Bootstrap

---

## 🗂️ Estructura del proyecto

```
ERP_TIENDA_INFORMATICA/
├── ERP_TIENDA-master/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── ...          # Controladores, servicios, repositorios, modelos
│   │   │   └── resources/
│   │   │       ├── templates/   # Vistas Thymeleaf (HTML)
│   │   │       ├── static/      # CSS, imágenes, JS
│   │   │       └── application.properties
│   │   └── test/
│   └── pom.xml
└── .idea/
```

---

## ⚙️ Requisitos previos

Antes de ejecutar el proyecto, asegúrate de tener instalado:

- [Java JDK 17+](https://www.oracle.com/java/technologies/downloads/)
- [Maven 3.8+](https://maven.apache.org/)
- [MySQL 8.0+](https://dev.mysql.com/downloads/)
- [IntelliJ IDEA](https://www.jetbrains.com/idea/) *(recomendado)*

---

## 🛠️ Instalación y configuración

### 1. Clonar el repositorio

```bash
git clone https://github.com/anisweb16/ERP_TIENDA_INFORMATICA.git
cd ERP_TIENDA_INFORMATICA/ERP_TIENDA-master
```

### 2. Configurar la base de datos

Crea una base de datos en MySQL:

```sql
CREATE DATABASE erp_tienda_informatica;
```

### 3. Configurar `application.properties`

Edita el archivo `src/main/resources/application.properties` con tus credenciales:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/erp_tienda_informatica
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### 4. Compilar y ejecutar

```bash
mvn clean install
mvn spring-boot:run
```

### 5. Acceder a la aplicación

Abre el navegador y ve a:

```
http://localhost:8080
```

---

---

## 🧱 Arquitectura

El proyecto sigue el patrón **MVC (Modelo-Vista-Controlador)**:

- **Modelo** — Entidades JPA mapeadas a tablas MySQL
- **Vista** — Plantillas Thymeleaf renderizadas en el servidor
- **Controlador** — Controladores Spring MVC que gestionan las peticiones HTTP

---

## 📚 Contexto académico

Proyecto desarrollado durante el primer año del ciclo formativo de grado superior **DAM (Desarrollo de Aplicaciones Multiplataforma)** en Madrid, con enfoque en backend, bases de datos y arquitectura de aplicaciones.

---

## 👤 Autor

**Anis** — [@anisweb16](https://github.com/anisweb16)

---

## 📄 Licencia

Este proyecto es de uso académico. Todos los derechos reservados al autor.
