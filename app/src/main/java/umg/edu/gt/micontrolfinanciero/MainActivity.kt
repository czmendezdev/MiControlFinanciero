
package umg.edu.gt.micontrolfinanciero

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import umg.edu.gt.micontrolfinanciero.data.AppDatabase
import umg.edu.gt.micontrolfinanciero.data.MovimientoRepository
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch //Inicia una corrutina, que utilizaremos para observar los movimientos almacenados en Room.//

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Obtenemos la instancia de la base de datos local.
        val baseDatos = AppDatabase.obtenerInstancia(applicationContext)

        // Creamos el repositorio utilizando el DAO de movimientos.
        val repositorio = MovimientoRepository(baseDatos.movimientoDao())

        // Referencias a los textos que mostrarán los saldos por moneda.
        val txtSaldoGTQ = findViewById<TextView>(R.id.txtSaldoGTQ)
        val txtSaldoUSD = findViewById<TextView>(R.id.txtSaldoUSD)
        val txtSaldoEUR = findViewById<TextView>(R.id.txtSaldoEUR)

        // Observamos los movimientos guardados en la base de datos.
        lifecycleScope.launch {

            repositorio.obtenerMovimientos().collect { movimientos ->

                // Calculamos el saldo de cada moneda por separado.
                var saldoGTQ = 0L
                var saldoUSD = 0L
                var saldoEUR = 0L

                movimientos.forEach { movimiento ->

                    // Los ingresos suman y los gastos restan.
                    val importe = when (movimiento.tipo) {
                        "INGRESO" -> movimiento.importeCentavos
                        "GASTO" -> -movimiento.importeCentavos
                        else -> 0L
                    }

                    // Acumulamos el importe según su moneda.
                    when (movimiento.moneda) {
                        "GTQ" -> saldoGTQ += importe
                        "USD" -> saldoUSD += importe
                        "EUR" -> saldoEUR += importe
                    }
                }

                // Convertimos los centavos a una cantidad con dos decimales.
                txtSaldoGTQ.text = "Q %.2f".format(java.util.Locale.US, saldoGTQ / 100.0)
                txtSaldoUSD.text = "$ %.2f".format(java.util.Locale.US, saldoUSD / 100.0)
                txtSaldoEUR.text = "€ %.2f".format(java.util.Locale.US, saldoEUR / 100.0)
            }
        }

        // Ajustamos el contenido a las barras del sistema.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }
    }
}
