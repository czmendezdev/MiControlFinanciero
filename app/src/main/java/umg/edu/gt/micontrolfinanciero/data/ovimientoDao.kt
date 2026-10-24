package umg.edu.gt.micontrolfinanciero.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Define las operaciones de acceso a los movimientos
 * almacenados en la base de datos local.
 */
@Dao
interface MovimientoDao {

    // Guarda un ingreso o gasto en la base de datos.
    @Insert
    suspend fun insertar(movimiento: MovimientoEntity)

    // Obtiene todos los movimientos, del más reciente al más antiguo.
    @Query("SELECT * FROM movimientos ORDER BY fecha DESC, id DESC")
    fun obtenerTodos(): Flow<List<MovimientoEntity>>

    // Obtiene únicamente los ingresos o los gastos.
    @Query("SELECT * FROM movimientos WHERE tipo = :tipo ORDER BY fecha DESC, id DESC")
    fun obtenerPorTipo(tipo: String): Flow<List<MovimientoEntity>>
}