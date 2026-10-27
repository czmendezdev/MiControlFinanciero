package umg.edu.gt.micontrolfinanciero

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import umg.edu.gt.micontrolfinanciero.data.AppDatabase
import umg.edu.gt.micontrolfinanciero.data.MovimientoRepository

class HistorialMovimientosActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Abrimos el diseño de la pantalla del historial.
        setContentView(R.layout.activity_historial_movimientos)

        // Obtenemos el RecyclerView del archivo XML.
        val recyclerMovimientos =
            findViewById<RecyclerView>(R.id.recyclerMovimientos)

        // Organizamos los movimientos en una lista vertical.
        recyclerMovimientos.layoutManager =
            LinearLayoutManager(this)

        // Creamos y conectamos el adaptador.
        val adaptador = MovimientoAdapter()

        recyclerMovimientos.adapter = adaptador

        // Accedemos a la base de datos local.
        val baseDatos = AppDatabase.obtenerInstancia(this)

        val repositorio =
            MovimientoRepository(baseDatos.movimientoDao())

        // Observamos los movimientos almacenados en Room.
        lifecycleScope.launch {

            repositorio.obtenerMovimientos().collect { movimientos ->

                // Actualizamos los datos mostrados en pantalla.
                adaptador.actualizarMovimientos(movimientos)
            }
        }
    }
}
