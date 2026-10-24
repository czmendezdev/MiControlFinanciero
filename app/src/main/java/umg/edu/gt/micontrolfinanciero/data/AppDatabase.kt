package umg.edu.gt.micontrolfinanciero.data

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Base de datos local de MiControlFinanciero.
 *
 * Utiliza Room sobre SQLite para almacenar
 * los ingresos y gastos del usuario.
 */
@Database(
    entities = [MovimientoEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // Proporciona acceso a las operaciones de movimientos.
    abstract fun movimientoDao(): MovimientoDao
}