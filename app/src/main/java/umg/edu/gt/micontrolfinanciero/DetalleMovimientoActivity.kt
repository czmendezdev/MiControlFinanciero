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
import android.content.Intent
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class DetalleMovimientoActivity : AppCompatActivity() {

    // Identificador del movimiento seleccionado.
    private var movimientoId: Long = -1L

    // Componentes que muestran la información del movimiento.
    private lateinit var txtConcepto: TextView
    private lateinit var txtImporte: TextView
    private lateinit var txtTipo: TextView
    private lateinit var txtMoneda: TextView
    private lateinit var txtFecha: TextView

    // Repositorio para consultar los datos en Room.
    private lateinit var repositorio: MovimientoRepository


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_detalle_movimiento)

        // Obtenemos los componentes del diseño XML.

        txtConcepto = findViewById(R.id.txtConceptoDetalle)
        txtImporte = findViewById(R.id.txtImporteDetalle)
        txtTipo = findViewById(R.id.txtTipoDetalle)
        txtMoneda = findViewById(R.id.txtMonedaDetalle)
        txtFecha = findViewById(R.id.txtFechaDetalle)

        // Identificamos el botón para editar el movimiento.
        val btnEditarMovimiento =
            findViewById<MaterialButton>(R.id.btnEditarMovimiento)


        // Identificamos el botón para eliminar movimientos.
        val btnEliminarMovimiento =
            findViewById<MaterialButton>(R.id.btnEliminarMovimiento)


        // Recibimos el identificador del movimiento.
        movimientoId = intent.getLongExtra("MOVIMIENTO_ID", -1L)


        // Abrimos el formulario en modo edición.
        btnEditarMovimiento.setOnClickListener {

            val intentEdicion = Intent(
                this,
                RegistrarMovimientoActivity::class.java
            )

            // Enviamos el ID del movimiento seleccionado.
            intentEdicion.putExtra("MOVIMIENTO_ID", movimientoId)

            startActivity(intentEdicion)
        }


// Solicitamos confirmación antes de eliminar.
        btnEliminarMovimiento.setOnClickListener {

            MaterialAlertDialogBuilder(this)
                .setTitle("Eliminar movimiento")
                .setMessage(
                    "¿Estás seguro de que deseas eliminar este movimiento? " +
                            "Esta acción no se puede deshacer."
                )
                .setNegativeButton("Cancelar", null)

                .setPositiveButton("Eliminar") { _, _ ->

                    lifecycleScope.launch {
                        try {
                            // Buscamos el movimiento que se desea eliminar.
                            val movimiento =
                                repositorio.obtenerMovimientoPorId(movimientoId)

                            if (movimiento == null) {
                                Toast.makeText(
                                    this@DetalleMovimientoActivity,
                                    "No se encontró el movimiento",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@launch
                            }

                            // Eliminamos el movimiento de Room.
                            repositorio.eliminarMovimiento(movimiento)

                            Toast.makeText(
                                this@DetalleMovimientoActivity,
                                "Movimiento eliminado correctamente",
                                Toast.LENGTH_SHORT
                            ).show()

                            // Regresamos al historial.
                            finish()

                        } catch (e: Exception) {
                            Toast.makeText(
                                this@DetalleMovimientoActivity,
                                "Error al eliminar el movimiento",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                .show()
        }


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

        repositorio = MovimientoRepository(
            baseDatos.movimientoDao()
        )

    }


    // Actualiza los datos cada vez que regresamos a esta pantalla.
    override fun onResume() {
        super.onResume()

        if (movimientoId > 0L && ::repositorio.isInitialized) {
            cargarMovimiento()
        }
    }


    // Consulta nuevamente los datos del movimiento.
        private fun cargarMovimiento() {

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

