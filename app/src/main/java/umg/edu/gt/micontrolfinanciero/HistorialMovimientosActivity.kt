package umg.edu.gt.micontrolfinanciero

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButtonToggleGroup
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import umg.edu.gt.micontrolfinanciero.data.AppDatabase
import umg.edu.gt.micontrolfinanciero.data.MovimientoRepository

class HistorialMovimientosActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_historial_movimientos)

        // Configuramos la lista de movimientos.
        val recyclerMovimientos =
            findViewById<RecyclerView>(R.id.recyclerMovimientos)

        recyclerMovimientos.layoutManager =
            LinearLayoutManager(this)

        val adaptador = MovimientoAdapter()
        recyclerMovimientos.adapter = adaptador

        // Obtenemos el repositorio de Room.
        val baseDatos = AppDatabase.obtenerInstancia(this)

        val repositorio =
            MovimientoRepository(baseDatos.movimientoDao())

        // Obtenemos el grupo de botones de filtros.
        val grupoFiltros =
            findViewById<MaterialButtonToggleGroup>(
                R.id.grupoFiltrosHistorial
            )

        // Guarda la tarea que observa los movimientos.
        var trabajoFiltro: Job? = null

        // Cambia la consulta según el filtro seleccionado.
        fun aplicarFiltro(tipo: String?) {

            // Detenemos la consulta anterior.
            trabajoFiltro?.cancel()

            trabajoFiltro = lifecycleScope.launch {

                val movimientosFlow = if (tipo == null) {
                    repositorio.obtenerMovimientos()
                } else {
                    baseDatos.movimientoDao().obtenerPorTipo(tipo)
                }

                movimientosFlow.collect { movimientos ->
                    adaptador.actualizarMovimientos(movimientos)
                }
            }
        }

        // Escuchamos los cambios de selección.
        grupoFiltros.addOnButtonCheckedListener {
                _, checkedId, isChecked ->

            if (isChecked) {

                when (checkedId) {

                    R.id.btnFiltroTodos -> {
                        aplicarFiltro(null)
                    }

                    R.id.btnFiltroIngresos -> {
                        aplicarFiltro("INGRESO")
                    }

                    R.id.btnFiltroGastos -> {
                        aplicarFiltro("GASTO")
                    }
                }
            }
        }

        // Mostramos todos los movimientos al abrir.
        grupoFiltros.check(R.id.btnFiltroTodos)
        aplicarFiltro(null)
    }
}
