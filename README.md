# API Bancaria para Cajeros Automáticos

Este repositorio es un **fork** del proyecto base entregado para la prueba técnica de desarrollo backend en **Java / Spring Boot**.  
El objetivo es implementar una primera versión funcional de la API que permita a los clientes del banco realizar operaciones desde cajeros automáticos propios y de otras entidades.

## 🛠️ Tecnologías
- Java 21 (LTS)
- Spring Boot 3.1
- Spring Data JPA
- Base de datos H2 (para desarrollo y pruebas)
- Git Flow (rama `develop` + ramas `feature/*`)
- JUnit / Spring Boot Test
- Docker (para despliegue)
- GitHub Actions (CI/CD)

## 📌 Flujo de trabajo
- La rama principal es **main** (código estable).
- Se crea la rama **develop** para integrar nuevas funcionalidades.
- Cada funcionalidad se desarrolla en ramas **feature/** independientes y se integra mediante Pull Requests hacia `develop`.

## 🚀 Cómo ejecutar
1. Clonar el repositorio:
   ```bash
   git clone https://github.com/TU_USUARIO/spring-boot-template.git
   cd spring-boot-template
   ```

2. Arrancar la aplicación:
   ```bash
   mvn spring-boot:run
   ```

3. Acceder a la consola H2:

    - URL: http://localhost:8080/h2-console

    - JDBC URL: jdbc:h2:mem:bankdb

## 📑 Especificaciones de negocio
La API debe permitir a un cliente del banco:

- Consultar movimientos de sus cuentas (ingresos, retiradas, comisiones, transferencias).

- Retirar dinero según el tipo de tarjeta (débito/crédito) y límites configurados.

- Ingresar dinero únicamente en cajeros del mismo banco.

- Realizar transferencias a cuentas del mismo o de otros bancos (validando IBAN y comisiones).

- Activar su tarjeta en el primer uso (requisito previo a cualquier operación).

- Cambiar su código PIN (obligatorio tras la activación inicial).

- Consultar y modificar la configuración de su tarjeta (límite de retiro entre 500 y 6.000 €).


## 🔒 Consideraciones técnicas
- El PIN no debe almacenarse en texto plano.

- Se implementarán tests unitarios e integración.

- Se valorará la integración continua (CI/CD) y despliegue con Docker.

- Se priorizarán las funcionalidades de mayor valor en el tiempo disponible.


## ✅ Estado actual
- Proyecto base con configuración inicial de Spring Boot.

- Configuración de base de datos H2.

- Flujo Git Flow preparado para desarrollo.

- Actualizado a Java 21.


