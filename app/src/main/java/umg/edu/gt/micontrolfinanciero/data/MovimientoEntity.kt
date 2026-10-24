package umg.edu.gt.micontrolfinanciero.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Representa un ingreso o gasto registrado por el usuario.
 *
 * Cada movimiento se almacenará en la tabla "movimientos"
 * de nuestra base de datos local.
 */
@Entity(tableName = "movimientos")
data class MovimientoEntity(

    // Identificador único generado automáticamente.
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // Tipo de movimiento: INGRESO o GASTO.
    val tipo: String,

    // Descripción del movimiento financiero.

    val concepto: String,

    // Cantidad almacenada en centavos para evitar errores decimales.
    val importeCentavos: Long,

    // Código de moneda: GTQ, USD o EUR.
    val moneda: String,

    // Fecha almacenada como marca de tiempo en milisegundos.
    val fecha: Long
)