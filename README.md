# MiControlFinanciero

## Descripción del proyecto

MiControlFinanciero es una aplicación móvil desarrollada para dispositivos Android que permite administrar ingresos y gastos personales de forma sencilla y organizada.

La aplicación facilita el registro, consulta, modificación y eliminación de movimientos financieros, además de proporcionar un resumen de los ingresos, gastos y saldos disponibles.

## Objetivo

Desarrollar una aplicación Android que permita llevar un control básico de las finanzas personales mediante el almacenamiento local de movimientos y la actualización automática de los saldos.

## Tecnologías utilizadas

- **Kotlin:** lenguaje de programación principal.
- **Android Studio:** entorno de desarrollo.
- **XML:** diseño de las interfaces de usuario.
- **Room:** almacenamiento local de los movimientos financieros.
- **Coroutines y Flow:** operaciones asíncronas y actualización de información.
- **Material Design:** componentes visuales de la aplicación.
- **Git y GitHub:** control de versiones del proyecto.

## Funcionalidades implementadas

### Registro de movimientos
- Registrar ingresos y gastos.
- Ingresar concepto, importe, moneda y fecha.
- Validar los datos antes de guardar.
- Almacenar los registros localmente.

### Administración de movimientos
- Consultar el historial de movimientos.
- Filtrar registros por ingresos o gastos.
- Visualizar los detalles de cada movimiento.
- Editar movimientos existentes.
- Eliminar movimientos mediante confirmación.

### Control financiero
- Consultar el saldo disponible.
- Manejar quetzales (GTQ), dólares (USD) y euros (EUR).
- Calcular ingresos y gastos por separado.
- Mostrar un resumen financiero por moneda.
- Actualizar los saldos automáticamente al modificar los registros.

## Almacenamiento de datos

La aplicación utiliza Room para administrar una base de datos local. Los movimientos registrados se conservan en el dispositivo y pueden consultarse posteriormente.

## Requisitos

- Dispositivo Android compatible con Android 7.0 o superior.
- Android Studio para compilar o modificar el código fuente.

## Estado del proyecto

Versión funcional con registro, historial, edición, eliminación y resumen financiero.

## Información académica

**Universidad Mariano Gálvez de Guatemala**

**Curso:** Programación II

**Proyecto:** MiControlFinanciero

**Desarrollador:** Cades Naamán Bulum Méndez
