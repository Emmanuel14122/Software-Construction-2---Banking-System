# EVALUACIÓN - -Software-Construction-2---Banking-System

## Información General
- **Estudiantes:** Alejandro Espinosa Arboleda, Brayan Alejandro Gonzalez Perez, Emmanuel Calderon Payares
- **Rama evaluada:** main (develop contenía únicamente una aplicación de clínica sin relación con el dominio bancario)
- **Fecha de evaluación:** 2026-03-23

---

## Tabla de Calificación

| # | Criterio | Peso | Puntaje (1–5) | Nota ponderada |
|---|---|---|---|---|
| 1 | Modelado de dominio | 25% | 3 | 0.75 |
| 2 | Relaciones entre entidades | 15% | 2 | 0.30 |
| 3 | Uso de Enums | 15% | 2 | 0.30 |
| 4 | Manejo de estados | 5% | 1 | 0.10 |
| 5 | Tipos de datos | 5% | 2 | 0.20 |
| 6 | Separación Usuario vs Cliente | 10% | 4 | 0.80 |
| 7 | Bitácora | 5% | 1 | 0.10 |
| 8 | Reglas básicas de negocio | 5% | 2 | 0.20 |
| 9 | Estructura del proyecto | 10% | 3 | 0.60 |
| 10 | Repositorio | 10% | 3 | 0.60 |
| **TOTAL** | | **100%** | | **3.95 / 5 (base)** |

> Nota base = (3/5×0.25 + 2/5×0.15 + 2/5×0.15 + 1/5×0.05 + 2/5×0.05 + 4/5×0.10 + 1/5×0.05 + 2/5×0.05 + 3/5×0.10 + 3/5×0.10) × 5 = 0.53 × 5 = **2.65**

---

## Penalizaciones

| Penalización | Descuento | Nota resultante |
|---|---|---|
| Variables mal nombradas (snake_case: `id_titular`, `loan_id`, `type_loan`, `account_type`) | -10% | 2.65 × 0.90 = **2.39** |

---

## Bonus

Ninguno aplicable.

---

## Nota Final: **2.4 / 5.0**

---

## Análisis por Criterio

### 1. Modelado de dominio — 3/5
Se identificaron correctamente las entidades principales: `Client` (abstracta), `NaturalPersonClient`, `CompanyClient`, `UserSystem` (abstracta), `CommercialEmployee`, `CompanyEmployee`, `CompanySupervisor`, `InternalBankAnalist`, `WindowClerk`, `BankAccount`, `Loan` (clase `Loan`, archivo `loan.java`), `Transfer`, `GeneralBankingProduct`.  
**Faltante crítico:** No existe clase `Bitácora` / `OperationsLog`.  
Herencia básica entre `Client` → `NaturalPersonClient` / `CompanyClient` está presente pero la rama `develop` contenía un proyecto completamente distinto (ClinicApplication), lo que muestra falta de control de versiones.

### 2. Relaciones entre entidades — 2/5
Las relaciones se modelaron usando IDs primitivos (`String id_titular`, `String client_requestor_id`, `int creator_user_id`) en lugar de referencias a objetos del dominio. Solo `CommercialEmployee extends UserSystem` muestra una relación de herencia correcta. No se encontraron listas de cuentas en `Cliente` ni referencias directas entre entidades.

### 3. Uso de Enums — 2/5
Solo se creó un enum (`Role`). Los campos `accountType`, `accountStatus`, `currency`, `loan_status`, `transfer_status`, `system_role`, `user_status` se definieron todos como `String`, lo cual es una práctica incorrecta para catálogos de dominio.

### 4. Manejo de estados — 1/5
Sin enums de estado, el manejo de estados es inexistente. No hay constantes que restrinjan los valores posibles de estado en ninguna entidad.

### 5. Tipos de datos — 2/5
Se usó `double` en lugar de `BigDecimal` para montos monetarios (`currentBalance`, `requesting_amount`, `approved_amount`, `amount`). Las fechas se manejan con `java.util.Date` y `java.sql.Date` en lugar de `LocalDate`/`LocalDateTime`. `BankAccount.openingDate` es `String`.

### 6. Separación Usuario vs Cliente — 4/5
`UserSystem` (abstracta) y `Client` (abstracta) son jerarquías separadas. La separación existe y es correcta en concepto. Se pierde un punto porque `UserSystem` no referencia objetos `Client` sino que guarda `id_related` como `String`.

### 7. Bitácora — 1/5
No se implementó ninguna clase de auditoría/bitácora.

### 8. Reglas básicas de negocio — 2/5
Se usaron anotaciones de validación de Bean Validation (`@NotBlank`, `@Email`, `@Size`) sobre campos de `Client`, `NaturalPersonClient`, `CompanyClient`. No existen métodos de negocio (depositar, retirar, aprobar préstamo, ejecutar transferencia).

### 9. Estructura del proyecto — 3/5
Se usa el paquete `app.domain.Models` (con `M` mayúscula inconsistente). No hay separación entre enums, modelos y servicios. Todos los archivos están en una sola carpeta `models`, sin paquetes por capa.

### 10. Repositorio — 3/5
- **Nombre:** Comienza con guion (`-Software-Construction-2---Banking-System`), formato no estándar.
- **README:** Incluye autores, descripción y tecnologías — buena base, pero falta sección "Cómo ejecutar".
- **Commits:** 7 commits; mensajes descriptivos pero sin convención ADD/CHG.
- **Ramas:** Existe `develop` pero **solo contenía código de otro proyecto** (ClinicApplication), no el sistema bancario. El código bancario está únicamente en `main`.
- **Tag:** No existe tag de entrega.

---

## Fortalezas
- Clara separación entre jerarquía de usuarios del sistema (`UserSystem` con subtipos) y clientes bancarios (`Client`).
- Uso de validaciones de Bean Validation en entidades.
- Herencia básica de `Client` implementada con `NaturalPersonClient` y `CompanyClient`.
- Proyecto Spring Boot estructurado con Maven.

## Oportunidades de mejora
- Implementar los enums faltantes: `TipoCuenta`, `EstadoCuenta`, `EstadoPrestamo`, `EstadoTransferencia`, `EstadoUsuario`, `Moneda`.
- Reemplazar IDs primitivos por referencias a objetos (`Client client`, `BankAccount destinationAccount`).
- Crear la clase `Bitacora` con estructura flexible (`Map<String, Object>`).
- Usar `BigDecimal` para montos y `LocalDate`/`LocalDateTime` para fechas.
- Corregir la rama `develop` para que contenga el código bancario.
- El archivo `loan.java` debe llamarse `Loan.java` (convención Java de nombres de archivo).
- Agregar métodos de negocio en las entidades: depositar, retirar, aprobar/rechazar préstamo.
