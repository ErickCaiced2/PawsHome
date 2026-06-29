# Contexto del proyecto PawsHome

Documento de contexto técnico para retomar el trabajo sin perder el hilo entre sesiones.

---

## Estado actual (2026-06-23)

- **Rama activa:** `fernanda-branch`
- **Rama base:** `Develop`
- **Merge pendiente de commit:** `git merge justin-branch` fue ejecutado, los 6 conflictos fueron resueltos, pero el merge commit aún no se ha hecho.

### Para completar el merge pendiente

```bash
git add pawshome/src/main/java/com/example/model/RolUsuario.java \
        pawshome/src/main/java/com/example/repository/MascotaRepository.java \
        pawshome/src/main/java/com/example/service/MascotaService.java \
        pawshome/src/main/java/com/example/controller/MascotaController.java \
        pawshome/src/test/java/com/example/service/MascotaServiceTest.java \
        pawshome/src/test/java/com/example/controller/MascotaControllerTest.java \
        pawshome/src/main/resources/application.properties

git commit -m "Merge justin-branch: integrar catálogo público, gestión admin y listarPorAdministrador"
```

---

## Credenciales de prueba

| Usuario | Correo | Contraseña | Rol |
|---|---|---|---|
| Admin refugio | `admin@pawshome.com` | `admin123` | ADMINISTRADOR_REFUGIO |
| Usuario adoptante | `usuario@pawshome.com` | `usuario123` | USUARIO |

Las contraseñas están hasheadas con BCrypt en la tabla `usuarios` de Azure.

---

## Configuración de conexión a Azure SQL Server

El archivo crítico que NO debe borrarse es `application.properties`:

```properties
# pawshome/src/main/resources/application.properties
spring.main.show-banner=false
server.port=8080
spring.profiles.active=dev
```

Sin este archivo, Spring Boot no activa el perfil `dev` y cae en autoconfiguración H2 en memoria.

`application-dev.properties` contiene la cadena de conexión real:

```properties
spring.datasource.url=jdbc:sqlserver://pawshomesql.database.windows.net:1433;database=petconnect_db;encrypt=true;trustServerCertificate=false;loginTimeout=30;
spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver
spring.datasource.username=sqladmin
spring.datasource.password=@dmin123
spring.jpa.database-platform=org.hibernate.dialect.SQLServerDialect
spring.jpa.hibernate.ddl-auto=update
```

---

## Tareas implementadas

### Task 26 — Catálogo Thymeleaf de mascotas disponibles
- **Archivo:** `templates/mascotas/listado-disponibles.html`
- **Ruta:** `GET /mascotas/disponibles`
- **Muestra:** nombre, especie, raza, edad aproximada, sexo, descripción (2 líneas clamp), imagen o emoji placeholder
- **Estado vacío:** mensaje cuando no hay mascotas
- **Commit:** `feat: crear vista Thymeleaf del catálogo de mascotas disponibles [Task 26]`

### Task 27 — Limitar alcance del catálogo
- **Cambio:** botón "Ver todas" en `index.html` enlaza a `/mascotas/disponibles`
- **Restricciones aplicadas:** sin filtros, sin búsqueda, sin ordenamiento, sin vista de detalle, sin formulario de adopción
- **Commit:** `feat: conectar catálogo público desde la página de inicio [Task 27]`

---

## Conflictos resueltos en el merge de justin-branch

### 1. `RolUsuario.java`
- Se mantuvieron los 3 roles: `ADMINISTRADOR`, `ADMINISTRADOR_REFUGIO`, `USUARIO`
- Se agregó JavaDoc de Justin sobre los últimos dos roles

### 2. `MascotaRepository.java`
- Unión de ambas ramas: `findByIdAndAdministrador`, `findByEstadoDisponibilidad`, `findByAdministradorId`, `findByIdAndAdministradorId`

### 3. `MascotaService.java`
- Se conservó el modelo de seguridad de HEAD (rol checks, `@Transactional`, excepciones personalizadas)
- Se integraron los métodos nuevos de Justin: `listarPorAdministrador`, `findByIdAndAdministrador(Long, Long)`

### 4. `MascotaController.java`
- Se conservó el controlador completo de HEAD (inyección de `UsuarioRepository`, manejo de formulario, seguridad)
- Se agregó el endpoint `GET /mascotas/gestion?administradorId={id}` de Justin

### 5. `MascotaServiceTest.java`
- Se fusionaron las pruebas de HEAD (`cambiarDisponibilidad`) con las de Justin (`listarDisponibles`, `listarPorAdministrador`)
- **Fix importante:** Justin usaba `EstadoMascota.NO_DISPONIBLE` en `@EnumSource` pero ese valor no existe en el enum — se reemplazó con `EN_PROCESO` y `ADOPTADO`

### 6. `MascotaControllerTest.java`
- Se conservó el enfoque MockMvc standalone de HEAD con proxy `UsuarioRepository`
- Se agregaron pruebas de `GET /mascotas/gestion` y overrides de `listarDisponibles`/`listarPorAdministrador` en `CapturingMascotaService`

---

## Problemas conocidos y soluciones

### App usa H2 en vez de Azure SQL Server
**Síntoma:** Los logs de Hibernate muestran `create table` en lugar de conectar a Azure.  
**Causa más común:** `application.properties` fue eliminado en un merge.  
**Solución:** Recrear `application.properties` con `spring.profiles.active=dev`.

### Error CHECK constraint en columna `rol`
**Error:** `The INSERT statement conflicted with the CHECK constraint "CK__usuarios__rol__..."`  
**Causa:** La tabla fue creada antes de que se agregara `ADMINISTRADOR_REFUGIO` al enum.  
**Solución (Azure Portal Query Editor):**
```sql
ALTER TABLE usuarios DROP CONSTRAINT CK__usuarios__rol__05D8E0BE;
ALTER TABLE usuarios ADD CONSTRAINT CK_usuarios_rol
  CHECK (rol IN ('ADMINISTRADOR', 'ADMINISTRADOR_REFUGIO', 'USUARIO'));
```

### Falsos positivos de Lombok en VSCode
**Síntoma:** "cannot find symbol: method getRol()" en el IDE.  
**Causa:** El language server de VSCode no resuelve bien los métodos generados por Lombok post-merge.  
**Solución:** No es un error real. Verificar con `mvn test-compile` — solo genera warnings de deprecación de Lombok, no errores.

### Push rechazado (non-fast-forward)
```bash
git pull origin fernanda-branch --rebase
git push origin fernanda-branch
```

---

## Ramas del equipo

| Rama | Responsable | Contenido |
|---|---|---|
| `main` | — | Producción |
| `Develop` | — | Integración |
| `fernanda-branch` | Fernanda (fernygeek) | Task 26, Task 27, merge de justin-branch |
| `justin-branch` | Justin | `listarPorAdministrador`, `gestionAdministrador`, tests |

---

## Seed data para Azure SQL Server

Si necesitas poblar la base de datos desde cero, ejecutar en el Query Editor de Azure Portal:

```sql
-- Usuarios (passwords hasheados con BCrypt)
-- admin123  → $2a$10$...
-- usuario123 → $2a$10$...
INSERT INTO usuarios (nombre, correo, password_hash, rol, activo, fecha_creacion) VALUES
('Admin Refugio', 'admin@pawshome.com',
 '$2a$10$<hash_de_admin123>', 'ADMINISTRADOR_REFUGIO', 1, GETDATE()),
('Usuario Adoptante', 'usuario@pawshome.com',
 '$2a$10$<hash_de_usuario123>', 'USUARIO', 1, GETDATE());

-- Mascotas (sin foto)
INSERT INTO mascotas (nombre, especie, raza, edad_aproximada, sexo, descripcion, estado_disponibilidad, fecha_publicacion, administrador_id) VALUES
('Max',  'Perro', 'Labrador',     '3 años',   'Macho',  'Muy amigable y juguetón.', 'DISPONIBLE', GETDATE(), 1),
('Luna', 'Gato',  'Siamés',       '2 años',   'Hembra', 'Tranquila y cariñosa.',    'DISPONIBLE', GETDATE(), 1),
('Milo', 'Perro', 'Mestizo',      '1 año',    'Macho',  'Energético y curioso.',    'DISPONIBLE', GETDATE(), 1),
('Nala', 'Gato',  'Angora',       '4 años',   'Hembra', 'Independiente y elegante.','DISPONIBLE', GETDATE(), 1);
```

> Generar los hashes reales con BCrypt antes de insertar. En sesiones anteriores se usó un test temporal `HashGenTest.java` para obtenerlos.
