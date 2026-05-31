# 🍔 FoodHunter

Bienvenidos a **FoodHunter**, una aplicación web para descubrir, explorar y reservar en los mejores restaurantes.

---

## 🎯 Descripción del Proyecto

FoodHunter es una plataforma de búsqueda y reservación de restaurantes que permite a los usuarios:

- 🔍 Explorar restaurantes cercanos en un mapa interactivo
- 🍽️ Ver menús y detalles de establecimientos
- 📅 Realizar reservaciones en tiempo real
- ⭐ Dejar reseñas y calificaciones
- 👤 Gestionar su perfil de usuario
- 🔐 Sistema seguro de autenticación

---

## 🛠️ Tecnologías Utilizadas

| Tecnología | Versión | Descripción |
|-----------|---------|-------------|
| **Java** | 21 | Lenguaje de programación |
| **Spring Boot** | 3.3.5 | Framework web |
| **Maven** | 3.6+ | Gestor de dependencias |
| **Docker** | Latest | Containerización |
| **Docker Compose** | Latest | Orquestación |
| **Google Maps API** | v3 | Mapas interactivos |
| **Thymeleaf** | 3.1+ | Motor de plantillas |
| **Lombok** | Latest | Generador de código |

---

## 📋 Requisitos Previos

Antes de comenzar, asegúrate de tener instalado:

- ✅ **Docker Desktop** ([Descargar](https://www.docker.com/products/docker-desktop))
- ✅ **Git** ([Descargar](https://git-scm.com/))
- ✅ **Conexión a Internet**


## Clonar repositorio
```bash
git https://github.com/Silverweta20/FoodHunter.git
cd FoodHunter
```

## 🚀 Despliegue
### 1. Levantar los servicios
```bash
docker compose up --build -d
```

### 2. Verificar que todo esté corriendo
```bash
docker compose ps
```

### 3. Acceder a la aplicación
- **Web:** http://localhost:8080