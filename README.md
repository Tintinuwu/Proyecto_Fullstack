🎬 Sistema de Reservas de Cine
Ramo: Desarrollo FullStack I (DSY1103)
Institución: Duoc UC
Año: 2025

👥 Integrantes
NombreMicroservicios a cargoTomas DelgadoMS-Usuarios · MS-CatálogoMartin SotoMS-Reservas · MS-Pagos

📋 Descripción del Proyecto
Sistema integral de gestión de reservas para un cine, desarrollado con una arquitectura de microservicios independientes usando Spring Boot 3.2.5 y Java 21. El sistema cubre el ciclo completo de una reserva de entradas: registro de clientes, catálogo de películas, programación de funciones en salas, procesamiento de reservas y cobro. Cada proceso es responsabilidad de un microservicio autónomo con su propia base de datos MySQL, su propia lógica de negocio y su propia API REST.
El proyecto justifica el uso de microservicios al tener múltiples dominios funcionales que se comunican entre sí: un cambio en el catálogo de películas no afecta al servicio de pagos, y el servicio de reservas puede escalar independientemente del resto sin impactar la disponibilidad de los demás módulos.

🏗️ Arquitectura General
┌─────────────────────────────────────────────────────┐
│                     CLIENTE / POSTMAN               │
└──────────────────────────┬──────────────────────────┘
                           │
                           ▼
              ┌────────────────────────┐
              │      API Gateway       │  Puerto 8080
              │  (punto de entrada)    │
              └────────────┬───────────┘
                           │
        ┌──────────────────┼──────────────────┐
        ▼                  ▼                  ▼
┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│ MS-Usuarios  │  │ MS-Catálogo  │  │   MS-Salas   │
│  Puerto 8081 │  │  Puerto 8082 │  │  Puerto 8083 │
│ cine_usuarios│  │cine_catalogo │  │  cine_salas  │
└──────────────┘  └──────┬───────┘  └──────┬───────┘
                         │                  │
                         └────────┬─────────┘
                                  ▼
                       ┌──────────────────┐
                       │ MS-Programación  │
                       │   Puerto 8084    │
                       │cine_programacion │
                       └────────┬─────────┘
                                │
                                ▼
                       ┌──────────────────┐
        ┌──────────────┤   MS-Reservas    │
        │              │   Puerto 8085    │
        │              │  cine_reservas   │
        │              └────────┬─────────┘
        │                       │
        ▼                       ▼
┌──────────────┐       ┌──────────────────┐
│ MS-Usuarios  │       │    MS-Pagos      │
│ (validación) │       │   Puerto 8086    │
└──────────────┘       │   cine_pagos     │
                       └──────────────────┘

🔄 Flujo Interno de los Microservicios
1. Flujo: Crear una Reserva (flujo principal del negocio)
POST /api/reservas
        │
        ▼
┌─────────────────────────────────────────────────────────────┐
│                        MS-Reservas                          │
│                                                             │
│  1. Recibe la solicitud con usuario_id, funcion_id,         │
│     cantidad_de_asientos y total                            │
│                                                             │
│  2. ── GET /api/usuarios/{id}/existe ──▶ MS-Usuarios        │
│        ◀── { "existe": true/false } ───                     │
│        Si false → lanza error 400, reserva no creada        │
│                                                             │
│  3. ── GET /api/funciones/{id}/disponibilidad ──▶           │
│                              MS-Programación                │
│        ◀── { "disponible": true/false } ───                 │
│        Si false → lanza error 409, sin cupos                │
│                                                             │
│  4. Guarda la reserva con estado "PENDIENTE"                │
│                                                             │
│  5. ── POST /api/pagos ──▶ MS-Pagos                         │
│        body: { reservaId, monto, metodo }                   │
│        ◀── { "estado": "APROBADO" / "RECHAZADO" } ──        │
│                                                             │
│  6a. Si APROBADO  → reserva.estado = "CONFIRMADA" ✅        │
│  6b. Si RECHAZADO → reserva.estado = "CANCELADA"  ❌        │
│  6c. Si timeout   → reserva.estado = "CANCELADA"  ❌        │
│       (capturado en bloque catch con Feign exception)       │
└─────────────────────────────────────────────────────────────┘
        │
        ▼
Retorna ReservaResponseDTO con estado final al cliente

2. Flujo: Programar una Función
POST /api/funciones
        │
        ▼
┌─────────────────────────────────────────────────────────────┐
│                     MS-Programación                         │
│                                                             │
│  1. Recibe pelicula_id, sala_id, fechaHora y precio         │
│                                                             │
│  2. ── GET /api/peliculas/{id}/existe ──▶ MS-Catálogo       │
│        ◀── { "existe": true/false } ───                     │
│        Si false → error 404, película no encontrada         │
│                                                             │
│  3. ── GET /api/salas/{id}/existe ──▶ MS-Salas              │
│        ◀── { "existe": true/false } ───                     │
│        Si false → error 404, sala no encontrada             │
│                                                             │
│  4. Persiste la función con capacidad inicial de la sala    │
└─────────────────────────────────────────────────────────────┘
        │
        ▼
Retorna FuncionResponseDTO al cliente

3. Flujo: Registro de Usuario
POST /api/usuarios
        │
        ▼
┌─────────────────────────────────────────────────────────────┐
│                       MS-Usuarios                           │
│                                                             │
│  1. Recibe nombre, apellido, email, password, rol           │
│  2. Bean Validation valida los campos (@NotBlank, @Email)   │
│  3. Verifica que el email no esté duplicado en BD           │
│     → Si existe: lanza IllegalArgumentException (409)       │
│  4. Persiste el usuario con activo = true                   │
│  5. @PrePersist asigna fechaRegistro automáticamente        │
└─────────────────────────────────────────────────────────────┘
        │
        ▼
Retorna UsuarioResponseDTO (sin password) con HTTP 201

4. Flujo: Agregar Película al Catálogo
POST /api/peliculas
        │
        ▼
┌─────────────────────────────────────────────────────────────┐
│                       MS-Catálogo                           │
│                                                             │
│  1. Recibe titulo, genero, clasificacion, duracionMin       │
│  2. Bean Validation valida campos                           │
│     (@NotBlank, @Pattern para clasificación MPAA)           │
│  3. Verifica que el título no esté duplicado                │
│     (insensible a mayúsculas — existsByTituloIgnoreCase)    │
│     → Si existe: lanza IllegalArgumentException (409)       │
│  4. Persiste la película con activo = true                  │
└─────────────────────────────────────────────────────────────┘
        │
        ▼
Retorna PeliculaResponseDTO con HTTP 201

5. Flujo: Cancelar una Reserva
PUT /api/reservas/{id}/cancelar
        │
        ▼
┌─────────────────────────────────────────────────────────────┐
│                       MS-Reservas                           │
│                                                             │
│  1. Busca la reserva por ID                                 │
│     → Si no existe: retorna HTTP 404                        │
│  2. Cambia estado a "CANCELADA"                             │
│  3. Persiste el cambio en BD                                │
└─────────────────────────────────────────────────────────────┘
        │
        ▼
Retorna ReservaResponseDTO con estado "CANCELADA"

🧩 Microservicios
ServicioPuertoBase de DatosResponsableMS-Usuarios8081cine_usuariosTomas DelgadoMS-Catálogo8082cine_catalogoTomas DelgadoMS-Salas8083cine_salasMartin SotoMS-Programación8084cine_programacionMartin SotoMS-Reservas8085cine_reservasMartin SotoMS-Pagos8086cine_pagosMartin Soto

⚙️ Requisitos Previos

Java 21
Gradle 8+
MySQL 8+
IntelliJ IDEA
Postman
