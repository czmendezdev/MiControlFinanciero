package umg.edu.gt.micontrolfinanciero

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import umg.edu.gt.micontrolfinanciero.data.AppDatabase
import umg.edu.gt.micontrolfinanciero.data.MovimientoRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DetalleMovimientoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_detalle_movimiento)

        // Obtenemos los componentes del diseño XML.
        val txtConcepto = findViewById<TextView>(R.id.txtConceptoDetalle)
        val txtImporte = findViewById<TextView>(R.id.txtImporteDetalle)
        val txtTipo = findViewById<TextView>(R.id.txtTipoDetalle)
        val txtMoneda = findViewById<TextView>(R.id.txtMonedaDetalle)
        val txtFecha = findViewById<TextView>(R.id.txtFechaDetalle)

        // Recibimos el identificador del movimiento.
        val movimientoId = intent.getLongExtra("MOVIMIENTO_ID", -1L)

        if (movimientoId <= 0L) {
            Toast.makeText(
                this,
                "Movimiento no válido",
                Toast.LENGTH_SHORT
            ).show()
            finish()
            return
        }

        // Preparamos el acceso a Room.
        val baseDatos = AppDatabase.obtenerInstancia(this)
        val repositorio =
            MovimientoRepository(baseDatos.movimientoDao())

        // Consultamos el movimiento seleccionado.
        lifecycleScope.launch {

            val movimiento =
                repositorio.obtenerMovimientoPorId(movimientoId)

            if (movimiento == null) {
                Toast.makeText(
                    this@DetalleMovimientoActivity,
                    "No se encontró el movimiento",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
                return@launch
            }

            // Mostramos el concepto.
            txtConcepto.text = movimiento.concepto

            // Identificamos el tipo.
            val esIngreso = movimiento.tipo == "INGRESO"

            txtTipo.text = if (esIngreso) "Ingreso" else "Gasto"

            // Identificamos la moneda.
            val simbolo = when (movimiento.moneda) {
                "GTQ" -> "Q"
                "USD" -> "$"
                "EUR" -> "€"
                else -> movimiento.moneda
            }

            txtMoneda.text = when (movimiento.moneda) {
                "GTQ" -> "GTQ - Quetzales"
                "USD" -> "USD - Dólares"
                "EUR" -> "EUR - Euros"
                else -> movimiento.moneda
            }

            // Mostramos el importe.
            val signo = if (esIngreso) "+" else "-"
            val importe = movimiento.importeCentavos / 100.0

            txtImporte.text =
                "$signo $simbolo ${"%.2f".format(Locale.US, importe)}"

            val color = if (esIngreso) {
                Color.parseColor("#147D70")
            } else {
                Color.parseColor("#D96C63")
            }

            txtImporte.setTextColor(color)

            // Mostramos la fecha.
            val formatoFecha = SimpleDateFormat(
                "dd/MM/yyyy",
                Locale.getDefault()
            )

            txtFecha.text =
                formatoFecha.format(Date(movimiento.fecha))
        }
    }
}
