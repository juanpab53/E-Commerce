# E-Commerce API 

Este es un proyecto integral de comercio electrónico que incluye tanto el **Backend** (Spring Boot) como el **Frontend** (React). La plataforma está diseñada para ser escalable, segura y adaptable a diferentes tipos de productos.

## Estructura del Proyecto
El repositorio utiliza una estructura de **Monorepo** para facilitar la gestión del ciclo de vida completo de la aplicación:

- **/backend**: API REST desarrollada con Java 21, Spring Boot 4.x y Spring Data JPA.
- **/frontend**: Interfaz de usuario moderna desarrollada con React y gestión de estado.

## Arquitectura del Sistema
El diseño se basa en una arquitectura multicapa que garantiza la separación de responsabilidades:
- **Capa de Persistencia**: Mapeo relacional (PostgreSQL/Supabase).
- **Capa de Negocio (Services)**: Lógica para procesamiento de pedidos, gestión de inventario y pasarela de pagos.
- **Capa de Exposición (DTOs)**: Objetos optimizados para la comunicación eficiente con el Frontend.

## Stack Tecnológico
- **Backend:** Java 21, Spring Boot, Hibernate, Spring Security (JWT).
- **Frontend:** React, Axios, CSS Modules/Tailwind.
- **Base de Datos:** PostgreSQL (vía Supabase).
- **Herramientas:** Docker, Maven, Git.