# Changelog

Todas las novedades y cambios relevantes en este proyecto se documentarán en este archivo.

El formato se inspira en [Keep a Changelog](https://keepachangelog.com/es-ES/1.0.0/)  
y este proyecto sigue [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [sprint-0-done] - 2025-10-01
### Added
- **Docker Compose** con PostgreSQL y pgAdmin para desarrollo local.
- **Backend (Spring Boot)** configurado:
  - Dependencias base (web, data-jpa, security, flyway, postgresql).
  - Migración inicial `V1__init.sql` con esquema de tablas y seed de administrador.
  - Configuración en `application.yml` y `application-prod.yml`.
  - Clase `SecurityConfig` con autenticación básica y CORS de desarrollo.
  - Clase `AdminPasswordBootstrap` para hashear la contraseña del admin inicial.
- **Frontend (Vue 3 + Vite + Tailwind)** inicializado y funcional en `http://localhost:5173`.
- **GitHub Actions (CI/CD)** con workflows independientes para backend y frontend.
- **.gitignore** actualizado para Maven, Node, IDEs y archivos temporales.
- **README.md** con instrucciones claras de arranque para DB, backend y frontend.

### Fixed
- Eliminado `application.properties` malformado que interfería con Spring Boot.

### Notes
- Versión base del proyecto lista para iniciar el desarrollo (Sprint 1).
- Java versión utilizada: **17**.
- Tag creado en GitHub: **`sprint-0-done`**.





### Plantilla
## [sprint-X-done] - YYYY-MM-DD
### Added
- (Nuevas funcionalidades, clases, endpoints, vistas…)

### Changed
- (Modificaciones de funcionalidades ya existentes, refactors, mejoras de rendimiento…)

### Fixed
- (Errores solucionados, fallos de configuración, bugs detectados en pruebas…)

### Removed
- (Cosas eliminadas que ya no se usan: dependencias, archivos, código…)

### Notes
- (Notas especiales del sprint: versiones de librerías, decisiones importantes de arquitectura, pasos extra necesarios…)
