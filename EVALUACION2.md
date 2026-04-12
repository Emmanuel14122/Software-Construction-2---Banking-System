# EVALUACION 2 - Software-Construction-2---Banking-System

## Informacion general
- Estudiante(s): Alejandro Espinosa Arboleda, Brayan Alejandro Gonzalez Perez, Emmanuel Calderon Payares
- Rama evaluada: main
- Commit evaluado: 435cdc26 (origin/main, commit mas reciente del estudiante tras revisar todas las ramas)
- Fecha: 2026-04-11

---

## Tabla de calificacion

| Criterio | Peso | Puntaje (1-5) | Parcial |
|---|---|---|---|
| 1. Modelado de dominio | 20% | 2 | 0.40 |
| 2. Modelado de puertos | 20% | 1 | 0.20 |
| 3. Modelado de servicios de dominio | 20% | 1 | 0.20 |
| 4. Enums y estados | 10% | 1 | 0.10 |
| 5. Reglas de negocio criticas | 10% | 1 | 0.10 |
| 6. Bitacora y trazabilidad | 5% | 1 | 0.05 |
| 7. Estructura interna de dominio | 10% | 1 | 0.10 |
| 8. Calidad tecnica base en domain | 5% | 1 | 0.05 |
| **SUBTOTAL** | 100% | | **1.20** |

### Calculo
Nota base = Î£((puntaje_i / 5) * peso_i) / 20 = 24 / 20 = **1.20**

### Penalizaciones aplicadas
| Penalizacion | Motivo | Reduccion |
|---|---|---|
| Estados en String | accountType, accountStatus, currency, loan_status, transfer_status, system_role, user_status son String | -10% |
| Acoplamiento a framework | Anotaciones @Entity (JPA/Jakarta Persistence) directamente en clases de dominio | -25% |
| Nomenclatura deficiente | Campos en snake_case (loan_id, transfer_id, origin_account), archivo loan.java en minuscula | -5% |

Nota tras penalizaciones: 1.20 Ã— 0.90 Ã— 0.75 Ã— 0.95 = **0.77**

---

## Nota final
**0.6 / 5.0**

---

## Hallazgos

### Positivos
- Existe jerarquia de herencia para Cliente (NaturalPersonClient, CompanyClient) y para usuarios del sistema.
- Entidades principales del banco modeladas: BankAccount, Loan, Transfer, GeneralBankingProduct.
- Enum Role con los roles del sistema definido.

### Negativos graves
- **Sin puertos de dominio:** No existen interfaces para ClientePort, CuentaBancariaPort, PrestamoPort, TransferenciaPort, BitacoraPort. La arquitectura hexagonal no esta implementada.
- **Sin servicios de dominio:** No hay ninguna clase de servicio de dominio. Toda logica dependeria de application/infrastructure.
- **Acoplamiento JPA en dominio:** Todas las entidades usan `@Entity` de Jakarta Persistence, violando el principio de que el dominio no depende de la infraestructura.
- **Estados como String:** accountType, accountStatus, currency, loan_status, transfer_status, system_role, user_status todos como String en lugar de enum.
- **Falta BitacoraOperacion:** No existe entidad de bitacora ni concepto de trazabilidad en el dominio.
- **Nomenclatura Java incorrecta:** Campos en snake_case (loan_id, transfer_id, client_requestor_id), archivo `loan.java` con minuscula, package `app.domain.Models` con M mayuscula inconsistente.
- **Sin logica de negocio:** Las clases son contenedores de datos sin ningun metodo de negocio (depositar, retirar, aprobar, etc.).
- **Loan sin tipo de prestamo enum:** type_loan es String.
- **Transfer.approval_date:** Relacion entre Transfer y users referenciados solo por int ID, no por entidades.

---

## Recomendaciones
1. Eliminar todas las anotaciones @Entity, @Id, @Column del paquete domain. Estas pertenecen a la capa de infraestructura (adaptadores JPA).
2. Crear interfaces de puerto por agregado: ClientePort, CuentaBancariaPort, PrestamoPort, TransferenciaPort, BitacoraPort.
3. Reemplazar todos los campos String que representan estados por enums: EstadoCuenta, EstadoPrestamo, EstadoTransferencia, TipoCuenta, Moneda, EstadoUsuario.
4. Crear servicios de dominio con reglas de negocio: validar saldo suficiente, cuenta no bloqueada, estados de prestamo, aprobacion de transferencias.
5. Agregar entidad BitacoraOperacion con campo Map<String,Object> para datos variables y constructor inmutable.
6. Renombrar campos a camelCase (accountStatus, loanId, originAccount) segun convencion Java.


