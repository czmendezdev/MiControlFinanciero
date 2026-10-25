
package umg.edu.gt.micontrolfinanciero.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
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

    // Permite acceder a las operaciones de movimientos.
    abstract fun movimientoDao(): MovimientoDao

    companion object {

        // Mantiene una única instancia de la base de datos.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Obtiene la base de datos existente o crea una nueva.
         */
        fun obtenerInstancia(context: Context): AppDatabase {

            return INSTANCE ?: synchronized(this) {

                val instancia = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mi_control_financiero_db"
                ).build()

                INSTANCE = instancia
                instancia
            }
        }
    }
}
