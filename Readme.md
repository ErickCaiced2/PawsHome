# PawsHome

Plataforma web para conectar mascotas de refugios con posibles adoptantes. Los administradores de refugio publican mascotas disponibles y los usuarios pueden explorar el catálogo público.

## Tecnologías

| Capa | Tecnología |
|---|---|
| Lenguaje | Java 21 (target 17) |
| Framework | Spring Boot 3.3.5 |
| Vistas | Thymeleaf + thymeleaf-extras-springsecurity6 |
| Seguridad | Spring Security 6 (BCrypt) |
| Persistencia | Spring Data JPA + Hibernate |
| Base de datos | Azure SQL Server (Microsoft SQL Server) |
| Build | Maven |
| Utilidades | Lombok 1.18.38 |
| Tests | JUnit 5 + Mockito + MockMvc |

## Requisitos previos

- Java 21
- Maven 3.8+
- Acceso a la instancia Azure SQL Server `pawshomesql.database.windows.net`

## Ejecución local

```bash
cd pawshome
mvn spring-boot:run
```

La app inicia en `http://localhost:8080` usando el perfil `dev` (Azure SQL Server).

## Perfiles

| Perfil | Base de datos | Activación |
|---|---|---|
| `dev` | Azure SQL Server | `application.properties` → `spring.profiles.active=dev` |
| `prod` | Azure SQL Server (password por env var) | Variable de entorno `SPRING_PROFILES_ACTIVE=prod` |

> **Importante:** `application.properties` debe existir con `spring.profiles.active=dev`. Si se borra (por ejemplo en un merge), Spring Boot cae al autoconfig H2 en memoria.

## Estructura del proyecto

```
pawshome/
├── src/main/java/com/example/
│   ├── App.java                          # Entry point
│   ├── config/
│   │   └── DevDataInitializer.java       # Seed data en perfil dev
│   ├── controller/
│   │   ├── HomeController.java
│   │   └── MascotaController.java        # /mascotas/*
│   ├── dto/
│   │   └── MascotaForm.java
│   ├── exception/
│   │   ├── AccesoDenegadoException.java
│   │   └── MascotaNoEncontradaException.java
│   ├── model/
│   │   ├── EstadoMascota.java            # DISPONIBLE, EN_PROCESO, ADOPTADO, NO_DISPONIBLE
│   │   ├── Mascota.java
│   │   ├── RolUsuario.java               # ADMINISTRADOR, ADMINISTRADOR_REFUGIO, USUARIO
│   │   └── Usuario.java
│   ├── repository/
│   │   ├── MascotaRepository.java
│   │   └── UsuarioRepository.java
│   ├── security/
│   │   ├── SecurityConfig.java
│   │   └── UsuarioDetailsService.java
│   └── service/
│       ├── DbConnectionService.java
│       └── MascotaService.java
└── src/main/resources/
    ├── application.properties            # Activa perfil dev
    ├── application-dev.properties        # Conexión Azure SQL Server
    ├── application-prod.properties
    ├── static/
    │   ├── css/
    │   │   ├── estilosGenerales.css
    │   │   ├── inicio.css
    │   │   ├── login.css
    │   │   └── registro-mascota.css
    │   ├── images/
    │   └── js/interacciones.js
    └── templates/
        ├── acceso-denegado.html
        ├── index.html
        ├── login.html
        ├── registro-mascota.html
        └── mascotas/
            └── listado-disponibles.html  # Catálogo público
```

## Roles y permisos

| Rol | Acceso |
|---|---|
| `ADMINISTRADOR_REFUGIO` | Registrar mascotas (`/mascotas/nueva`), gestión (`/mascotas/gestion`), cambiar disponibilidad |
| `USUARIO` | Ver catálogo público (`/mascotas/disponibles`) |
| `ADMINISTRADOR` | Reservado para administración general del sistema |
| Anónimo | Inicio (`/`), login (`/login`), catálogo público |

## Endpoints principales

| Método | Ruta | Descripción | Acceso |
|---|---|---|---|
| GET | `/` | Página de inicio | Público |
| GET | `/login` | Formulario de login | Público |
| GET | `/mascotas/disponibles` | Catálogo de mascotas disponibles | Público |
| GET | `/mascotas/nueva` | Formulario registro de mascota | ADMINISTRADOR_REFUGIO |
| POST | `/mascotas` | Guardar nueva mascota | ADMINISTRADOR_REFUGIO |
| GET | `/mascotas/gestion?administradorId={id}` | Listado de mascotas del admin | ADMINISTRADOR_REFUGIO |

## Base de datos (Azure SQL Server)

**Servidor:** `pawshomesql.database.windows.net:1433`  
**Base de datos:** `petconnect_db`  
**DDL:** gestionado por Hibernate (`ddl-auto=update`)

### Tablas principales

**`usuarios`**
```sql
id, nombre, correo (unique), password_hash, rol, activo, fecha_creacion
```

**`mascotas`**
```sql
id, nombre, especie, raza, edad_aproximada, sexo, descripcion,
estado_disponibilidad, imagen_url, fecha_publicacion, administrador_id (FK → usuarios)
```

> Si aparece el error `CK__usuarios__rol__...`, ejecutar en Azure Portal:
> ```sql
> ALTER TABLE usuarios DROP CONSTRAINT CK__usuarios__rol__05D8E0BE;
> ALTER TABLE usuarios ADD CONSTRAINT CK_usuarios_rol
>   CHECK (rol IN ('ADMINISTRADOR', 'ADMINISTRADOR_REFUGIO', 'USUARIO'));
> ```

## Tests

```bash
mvn test
```

- `MascotaServiceTest` — pruebas unitarias con Mockito
- `MascotaControllerTest` — pruebas de controlador con MockMvc standalone

## Notas de desarrollo

- **Lombok + Java 21:** el IDE (VSCode) puede mostrar falsos positivos ("cannot find symbol") para métodos generados por Lombok. La compilación con `mvn test-compile` es la fuente de verdad.
- **Imágenes de mascotas:** si `imagenUrl` es null o vacío, se muestra un emoji placeholder (🐶 para perros, 🐱 para gatos).
- **Sesión:** cookie `JSESSIONID`, se elimina al hacer logout.
# 🐾 PawsHome - Sistema Web para Adopciones en Línea

<p align="center">
  <img src="https://img.shields.io/badge/Spring--Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" alt="Spring Boot">
  <img src="https://img.shields.io/badge/Azure--DevOps-0078D7?style=for-the-badge&logo=azuredevops&logoColor=white" alt="Azure DevOps">
  <img src="https://img.shields.io/badge/Java--17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 17">
  <img src="https://img.shields.io/badge/Linux--Server-FCC624?style=for-the-badge&logo=linux&logoColor=black" alt="Linux">
</p>

---

## 📊 Indicadores del Proyecto

### Estado del Pipeline de Integración (CI)
![Build Status](https://img.shields.io/badge/Azure_Pipeline-passing-success?style=flat-square&logo=azure-pipelines)
![Deployment](https://img.shields.io/badge/Azure_App_Service-active-blue?style=flat-square&logo=microsoft-azure)

### Distribución del Código (Métricas estimadas)
![Gráfica de Tecnologías](https://quickchart.io/chart?c={type:'bar',data:{labels:['Backend','Base%20de%20Datos','DevOps','Frontend'],datasets:[{label:'Progreso%20%25',data:[90,85,100,75],backgroundColor:'rgba(54,%20162,%20235,%200.6)'}]}})

---

## 🗺️ Diagrama del Flujo de Automatización (CI/CD)

```mermaid
graph LR
    A[Código Local] -->|Git Push| B(Repositorio Azure)
    B -->|Trigger Automático| C{Pipeline CI}
    C -->|Compilación exitosa .jar| D[Generación de Artefacto]
    D -->|Release Trigger main| E{Pipeline CD}
    E -->|Agente Local PipelinePaws| F[Despliegue Cloud]
    F -->|Hosting| G[Azure App Service Linux]