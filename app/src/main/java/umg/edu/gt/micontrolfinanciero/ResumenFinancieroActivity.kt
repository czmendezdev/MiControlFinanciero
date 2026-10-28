package umg.edu.gt.micontrolfinanciero

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import umg.edu.gt.micontrolfinanciero.data.AppDatabase
import umg.edu.gt.micontrolfinanciero.data.MovimientoRepository


class ResumenFinancieroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_resumen_financiero)


// Identificamos los campos del resumen en quetzales.
        val txtIngresosGTQ = findViewById<TextView>(R.id.txtIngresosGTQ)
        val txtGastosGTQ = findViewById<TextView>(R.id.txtGastosGTQ)
        val txtBalanceGTQ = findViewById<TextView>(R.id.txtBalanceGTQ)

// Identificamos los campos del resumen en dólares.
        val txtIngresosUSD = findViewById<TextView>(R.id.txtIngresosUSD)
        val txtGastosUSD = findViewById<TextView>(R.id.txtGastosUSD)
        val txtBalanceUSD = findViewById<TextView>(R.id.txtBalanceUSD)

// Identificamos los campos del resumen en euros.
        val txtIngresosEUR = findViewById<TextView>(R.id.txtIngresosEUR)
        val txtGastosEUR = findViewById<TextView>(R.id.txtGastosEUR)
        val txtBalanceEUR = findViewById<TextView>(R.id.txtBalanceEUR)

// Preparamos el acceso a la base de datos Room.
        val baseDatos = AppDatabase.obtenerInstancia(this)
        val repositorio = MovimientoRepository(baseDatos.movimientoDao())



        // Observamos los movimientos almacenados en Room.
        lifecycleScope.launch {

            repositorio.obtenerMovimientos().collect { movimientos ->

                // Calculamos los totales de cada moneda.
                val monedas = listOf("GTQ", "USD", "EUR")

                for (moneda in monedas) {

                    val ingresos = movimientos
                        .filter { it.moneda == moneda && it.tipo == "INGRESO" }
                        .sumOf { it.importeCentavos }

                    val gastos = movimientos
                        .filter { it.moneda == moneda && it.tipo == "GASTO" }
                        .sumOf { it.importeCentavos }

                    val saldo = ingresos - gastos

                    // Convertimos los centavos a unidades monetarias.
                    val simbolo = when (moneda) {
                        "GTQ" -> "Q"
                        "USD" -> "$"
                        "EUR" -> "€"
                        else -> ""
                    }

                    val ingresosTexto = "$simbolo ${"%.2f".format(java.util.Locale.US, ingresos / 100.0)}"
                    val gastosTexto = "$simbolo ${"%.2f".format(java.util.Locale.US, gastos / 100.0)}"
                    val saldoTexto = "$simbolo ${"%.2f".format(java.util.Locale.US, saldo / 100.0)}"

                    // Actualizamos los campos de la moneda correspondiente.
                    when (moneda) {
                        "GTQ" -> {
                            txtIngresosGTQ.text = "Ingresos: $ingresosTexto"
                            txtGastosGTQ.text = "Gastos: $gastosTexto"
                            txtBalanceGTQ.text = "Saldo: $saldoTexto"
                        }

                        "USD" -> {
                            txtIngresosUSD.text = "Ingresos: $ingresosTexto"
                            txtGastosUSD.text = "Gastos: $gastosTexto"
                            txtBalanceUSD.text = "Saldo: $saldoTexto"
                        }

                        "EUR" -> {
                            txtIngresosEUR.text = "Ingresos: $ingresosTexto"
                            txtGastosEUR.text = "Gastos: $gastosTexto"
                            txtBalanceEUR.text = "Saldo: $saldoTexto"
                        }
                    }
                }
            }
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}