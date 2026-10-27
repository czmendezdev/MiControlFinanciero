package umg.edu.gt.micontrolfinanciero

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.app.DatePickerDialog
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.math.BigDecimal
import android.widget.Toast
import com.google.android.material.button.MaterialButtonToggleGroup
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import umg.edu.gt.micontrolfinanciero.data.AppDatabase
import umg.edu.gt.micontrolfinanciero.data.MovimientoEntity
import umg.edu.gt.micontrolfinanciero.data.MovimientoRepository

class RegistrarMovimientoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Conectamos la Activity con su diseño XML
        setContentView(R.layout.activity_registrar_movimiento)

        // Obtenemos la base de datos local
        val baseDatos = AppDatabase.obtenerInstancia(applicationContext)

    // Creamos el repositorio para guardar movimientos
        val repositorio = MovimientoRepository(baseDatos.movimientoDao())


        // Identificador del movimiento que se desea editar........
        val movimientoId = intent.getLongExtra("MOVIMIENTO_ID", -1L)

        // Determina si el formulario está en modo edición..
        val modoEdicion = movimientoId > 0L


    // Conserva el movimiento original durante la edición.
        var movimientoOriginal: MovimientoEntity? = null


        // Recuperamos el tipo de movimiento enviado desde MainActivity...
        val tipoMovimientoRecibido = intent.getStringExtra("TIPO_MOVIMIENTO")
        var tipoMovimiento = tipoMovimientoRecibido ?: ""


        // Validar el tipo solamente al registrar un movimiento nuevo.
        if (!modoEdicion &&
            tipoMovimiento != "INGRESO" &&
            tipoMovimiento != "GASTO"
        ) {
            finish()
            return
        }


        // Identificamos el título del formulario.........
        val txtTituloMovimiento =
            findViewById<TextView>(R.id.txtTituloMovimiento)

        // Cambiamos el título según el tipo de movimiento...............
        when (tipoMovimiento) {

            "INGRESO" -> {
                txtTituloMovimiento.text = "Registrar ingreso"
            }

            "GASTO" -> {
                txtTituloMovimiento.text = "Registrar gasto"
            }

        }

        // Identificamos el botón que mostrará la fecha....
        val btnSeleccionarFecha =
            findViewById<MaterialButton>(R.id.btnSeleccionarFecha)

        // Obtenemos la fecha actual del dispositivo
        val fechaSeleccionada = Calendar.getInstance()

        // Definimos el formato que verá el usuario
        val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        // Función para actualizar el texto del botón
        fun actualizarFecha() {
            btnSeleccionarFecha.text =
                formatoFecha.format(fechaSeleccionada.time)
        }

        // Mostramos inicialmente la fecha actual
        actualizarFecha()

        // Abrimos el calendario al presionar el botón
        btnSeleccionarFecha.setOnClickListener {

            val calendario = DatePickerDialog(
                this,
                { _, anio, mes, dia ->

                    // Guardamos la fecha elegida
                    fechaSeleccionada.set(
                        anio,
                        mes,
                        dia,
                        12,
                        0,
                        0
                    )

                    fechaSeleccionada.set(Calendar.MILLISECOND, 0)

                    // Actualizamos la fecha en pantalla
                    actualizarFecha()
                },
                fechaSeleccionada.get(Calendar.YEAR),
                fechaSeleccionada.get(Calendar.MONTH),
                fechaSeleccionada.get(Calendar.DAY_OF_MONTH)
            )

            calendario.show()
        }


        // Identificamos los componentes del formulario
        val edtConcepto =
            findViewById<TextInputEditText>(R.id.edtConcepto)

        val contenedorConcepto =
            findViewById<TextInputLayout>(R.id.contenedorConcepto)

        val btnGuardarMovimiento =
            findViewById<MaterialButton>(R.id.btnGuardarMovimiento)


        // Identificamos los componentes del importe
        val edtImporte =
            findViewById<TextInputEditText>(R.id.edtImporte)

        val contenedorImporte =
            findViewById<TextInputLayout>(R.id.contenedorImporte)
        // Identificamos el grupo de selección de monedas
        val grupoMonedas =
            findViewById<MaterialButtonToggleGroup>(R.id.grupoMonedas)


        // Cargar los datos originales cuando estamos editando.
        if (modoEdicion) {

            // Evitamos guardar antes de terminar la carga.
            btnGuardarMovimiento.isEnabled = false

            lifecycleScope.launch {

                try {
                    val movimiento =
                        repositorio.obtenerMovimientoPorId(movimientoId)

                    if (movimiento == null) {
                        Toast.makeText(
                            this@RegistrarMovimientoActivity,
                            "No se encontró el movimiento",
                            Toast.LENGTH_SHORT
                        ).show()

                        finish()
                        return@launch
                    }

                    // Conservamos el movimiento original.
                    movimientoOriginal = movimiento

                    // Recuperamos su tipo.
                    tipoMovimiento = movimiento.tipo

                    // Cambiamos el título del formulario.
                    txtTituloMovimiento.text =
                        if (tipoMovimiento == "INGRESO") {
                            "Editar ingreso"
                        } else {
                            "Editar gasto"
                        }

                    // Cargamos el concepto.
                    edtConcepto.setText(movimiento.concepto)

                    // Cargamos el importe sin perder precisión.
                    val importeTexto =
                        BigDecimal.valueOf(movimiento.importeCentavos)
                            .movePointLeft(2)
                            .toPlainString()

                    edtImporte.setText(importeTexto)

                    // Seleccionamos la moneda original.
                    val botonMoneda = when (movimiento.moneda) {
                        "GTQ" -> R.id.btnMonedaGTQ
                        "USD" -> R.id.btnMonedaUSD
                        "EUR" -> R.id.btnMonedaEUR
                        else -> null
                    }

                    if (botonMoneda == null) {
                        Toast.makeText(
                            this@RegistrarMovimientoActivity,
                            "Moneda no válida",
                            Toast.LENGTH_SHORT
                        ).show()

                        finish()
                        return@launch
                    }

                    grupoMonedas.check(botonMoneda)

                    // Recuperamos la fecha original.
                    fechaSeleccionada.timeInMillis = movimiento.fecha
                    actualizarFecha()

                    // Permitimos guardar después de cargar todo.
                    btnGuardarMovimiento.isEnabled = true

                } catch (e: Exception) {
                    Toast.makeText(
                        this@RegistrarMovimientoActivity,
                        "Error al cargar el movimiento",
                        Toast.LENGTH_LONG
                    ).show()

                    finish()
                }
            }
        }


// Verificación de los campos del formulario
        // Validamos los datos al presionar Guardar
        btnGuardarMovimiento.setOnClickListener {

            val concepto = edtConcepto.text.toString().trim()
            val importeTexto = edtImporte.text.toString().trim()

            // Limpiamos los mensajes de error anteriores
            contenedorConcepto.error = null
            contenedorImporte.error = null

            // Validamos el concepto
            if (concepto.isEmpty()) {
                contenedorConcepto.error =
                    "Debes ingresar el concepto del movimiento"

                edtConcepto.requestFocus()
                return@setOnClickListener
            }

            // Validamos que el importe tenga un formato correcto
            if (!importeTexto.matches(Regex("\\d+(\\.\\d{1,2})?"))) {
                contenedorImporte.error =
                    "Ingresa una cantidad válida con máximo 2 decimales"

                edtImporte.requestFocus()
                return@setOnClickListener
            }

            // Convertimos el texto a un número decimal exacto
            val importe = BigDecimal(importeTexto)

            // Comprobamos que sea mayor que cero
            if (importe <= BigDecimal.ZERO) {
                contenedorImporte.error =
                    "El importe debe ser mayor que cero"

                edtImporte.requestFocus()
                return@setOnClickListener
            }


            // Obtenemos la moneda seleccionada
            val monedaSeleccionada = when (grupoMonedas.checkedButtonId) {

                R.id.btnMonedaGTQ -> "GTQ"

                R.id.btnMonedaUSD -> "USD"

                R.id.btnMonedaEUR -> "EUR"

                else -> {
                    Toast.makeText(
                        this,
                        "Debes seleccionar una moneda",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setOnClickListener
                }
            }

            // Convertimos el importe a centavos sin perder precisión
            val importeCentavos: Long

            try {
                importeCentavos = importe
                    .movePointRight(2)
                    .longValueExact()

            } catch (e: ArithmeticException) {

                // Evitamos guardar cantidades demasiado grandes
                contenedorImporte.error =
                    "El importe supera el límite permitido"

                edtImporte.requestFocus()
                return@setOnClickListener
            }



            // Conservamos el ID cuando estamos editando.
            val idMovimiento = if (modoEdicion) {
                movimientoOriginal?.id ?: return@setOnClickListener
            } else {
                0L
            }

            // Creamos el movimiento con los datos validados.
            val movimiento = MovimientoEntity(
                id = idMovimiento,
                tipo = tipoMovimiento,
                concepto = concepto,
                importeCentavos = importeCentavos,
                moneda = monedaSeleccionada,
                fecha = fechaSeleccionada.timeInMillis
            )


            // Guardamos el movimiento en la base de datos
            lifecycleScope.launch {
                try {

                    if (modoEdicion) {
                        repositorio.actualizarMovimiento(movimiento)
                    } else {
                        repositorio.insertarMovimiento(movimiento)
                    }

                    // Mensaje según la operación realizada.
                    val mensaje = if (modoEdicion) {
                        "Movimiento actualizado correctamente"
                    } else {
                        "Movimiento guardado correctamente"
                    }

                    Toast.makeText(
                        this@RegistrarMovimientoActivity,
                        mensaje,
                        Toast.LENGTH_SHORT
                    ).show()

                    // Regresamos a la pantalla principal
                    finish()

                } catch (e: Exception) {
                    Toast.makeText(
                        this@RegistrarMovimientoActivity,
                        "Error al guardar el movimiento",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }


        }

    }
}
