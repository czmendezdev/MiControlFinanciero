
package umg.edu.gt.micontrolfinanciero.data

import kotlinx.coroutines.flow.Flow

/**
 * Repositorio encargado de comunicar la aplicación
 * con las operaciones de la base de datos.
 *
 * Centraliza el acceso a los movimientos financieros.
 */
class MovimientoRepository(
    private val movimientoDao: MovimientoDao
) {

    // Guarda un ingreso o gasto.
    suspend fun insertarMovimiento(movimiento: MovimientoEntity) {
        movimientoDao.insertar(movimiento)
    }

    // Obtiene todos los movimientos financieros.
    fun obtenerMovimientos(): Flow<List<MovimientoEntity>> {
        return movimientoDao.obtenerTodos()
    }

    // Obtiene movimientos según su tipo: INGRESO o GASTO.
    fun obtenerMovimientosPorTipo(tipo: String): Flow<List<MovimientoEntity>> {
        return movimientoDao.obtenerPorTipo(tipo)
    }


    // Obtiene un movimiento específico por su ID.
    suspend fun obtenerMovimientoPorId(id: Long): MovimientoEntity? {
        return movimientoDao.obtenerPorId(id)
    }


    // Actualiza un movimiento existente en la base de datos.
    suspend fun actualizarMovimiento(movimiento: MovimientoEntity) {
        movimientoDao.actualizarMovimiento(movimiento)
    }


    // Elimina un movimiento existente de la base de datos.
    suspend fun eliminarMovimiento(movimiento: MovimientoEntity) {
        movimientoDao.eliminarMovimiento(movimiento)
    }


}
