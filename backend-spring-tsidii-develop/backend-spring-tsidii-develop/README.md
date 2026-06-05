# Guia del proyecto

Este proyecto es una aplicación backend desarrollada con Spring Boot que expone servicios REST, utiliza JPA para persistencia y está configurada para ejecutarse con H2 en memoria (entornos de desarrollo/pruebas) o PostgreSQL (producción). Incluye pruebas unitarias con Mockito y pruebas de integración con H2, además de cobertura de código con JaCoCo.

## Requisitos previos

Se recomienda tener la version 17 del JDK antes de manipular el proyecto para evitar problemas a futuro

## Compila y ejecuta la aplicación con el wrapper de Maven

**La aplicación arrancará en el puerto 8081 con el contexto /bu-app.**
Para ejecutar el proyecto desde el archivo raiz, se usa el siguiente comando:
./mvnw clean spring-boot:run

## Ejecutar las pruebas

Para ejecutar las prubeas uzando el "plugin" de Jacoco, es cuetion de ejecutar el sieguiente comando:
*./mvnw clean test*
Si solamente se quiere ejecutar las pruebas unitarias, usar este comando:
./mvnw clean test -Dtest=com.icesi.bu_app.mock.*
Finalemente, si desea generar el archivo informativo de parte de Jacoco (Que ya se encuentra dentro del proyecto) Puede hacer uso de este comando
*./mvnw clean test jacoco:report*

## Tecnologías utilizadas

Spring Boot 4.0.3
Spring Data JPA
H2 Database (runtime)
PostgreSQL (runtime)
Lombok
JUnit 5 + Mockito
JaCoCo (cobertura)
Maven (con wrapper)

## Usuarios

| Correo | Contraseña |
| --- | --- |
| `admin@icesi.edu.co` | `admin123` |
| `juan.trainer@icesi.edu.co` | `trainer123` |
| `maria.trainer@icesi.edu.co` | `trainer123` |
| `carlos@icesi.edu.co` | `pass123` |
| `ana@icesi.edu.co` | `pass123` |
| `luis@icesi.edu.co` | `pass123` |

## Video de espliegue (visualización de documentación swagger y un método get de la controller rest de recommendation)

<https://youtu.be/IyqCH48k2ZY>

### Recomendacions por parte del profesor

- Excelente la entidad de roles, pero para tener un control más a detalle, generen una tabla de permisos para gestionar las accioes que puede realizar cada rol
- Las alertas deberían de tener una fecha de creacion o de envio
- Las rutinas son un conjunto de ejericcios selecionados por los usuarios de la lista disponible de ejercicios, por ende el diseño de rutinas y ejercicio necesita un poco más de revisión
- Los progresos están relacionados a las rutinas o a los ejercicios de alguna manera, para que permita al usuario al finalizar un ejercicio saber qué tanto avance a tenido con la rutina especificada
- Los lugares y eventos deben tener fechas y eso no será posible espeicificarlo a través del diseño actual.

## Manejo de excepciones (REST)

La aplicación centraliza el manejo de errores HTTP usando excepciones personalizadas y un handler global. Esto permite respuestas consistentes y semánticamente correctas para los clientes.

- Excepciones principales:
  - `ResourceNotFoundException` → 404 Not Found. Usar cuando un recurso solicitado no existe.
  - `BadRequestException` → 400 Bad Request. Usar para errores de validación o entradas inválidas.
  - `ConflictException` → 409 Conflict. Usar para condiciones de conflicto (por ejemplo, violaciones de unicidad o restricciones de integridad referencial al eliminar).

- Handler global: `ApiExceptionHandler` (@RestControllerAdvice) captura estas excepciones y construye respuestas JSON con el código HTTP apropiado, mensaje y detalles de error. También maneja errores de validación y excepciones no esperadas (500).

- Buenas prácticas en el proyecto:
  - Lanzar las excepciones desde la capa de servicio (business logic) para mantener control centralizado de errores.
  - Evitar `new RuntimeException(...)` genérico; usar las excepciones personalizadas para una semántica HTTP correcta.
  - Capturar `DataIntegrityViolationException` y volver a lanzar `ConflictException` cuando la causa es una restricción de base de datos (p. ej. al eliminar una entidad referenciada).
  - Los mappers (MapStruct) deberían encargarse solo de transformación; la validación y la decisión de lanzar excepciones queda en servicios/controladores.

- Ejemplos de uso:

```java
// desde un servicio
if (entity == null) throw new ResourceNotFoundException("Recurso no encontrado");
if (invalidInput) throw new BadRequestException("Campo X es obligatorio");
try {
  repository.deleteById(id);
} catch (DataIntegrityViolationException e) {
  throw new ConflictException("No se puede eliminar: está referenciado");
}
```

- Pruebas: las pruebas unitarias e integradas verifican tanto la lógica de servicio como que `ApiExceptionHandler` transforme las excepciones en los códigos HTTP y payloads esperados.
