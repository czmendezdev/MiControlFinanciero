package umg.edu.gt.micontrolfinanciero

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import umg.edu.gt.micontrolfinanciero.data.MovimientoEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MovimientoAdapter(
    private var movimientos: List<MovimientoEntity> = emptyList()
) : RecyclerView.Adapter<MovimientoAdapter.MovimientoViewHolder>() {

    // Representa los componentes visuales de una tarjeta.
    class MovimientoViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val txtConcepto: TextView =
            itemView.findViewById(R.id.txtConceptoItem)

        val txtImporte: TextView =
            itemView.findViewById(R.id.txtImporteItem)

        val txtFecha: TextView =
            itemView.findViewById(R.id.txtFechaItem)

        val txtTipo: TextView =
            itemView.findViewById(R.id.txtTipoItem)
    }

    // Crea una tarjeta utilizando nuestro archivo XML.
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MovimientoViewHolder {

        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_movimiento, parent, false)

        return MovimientoViewHolder(vista)
    }

    // Coloca los datos del movimiento en la tarjeta.
    override fun onBindViewHolder(
        holder: MovimientoViewHolder,
        position: Int
    ) {
        val movimiento = movimientos[position]

        holder.txtConcepto.text = movimiento.concepto

        val formatoFecha =
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        holder.txtFecha.text =
            formatoFecha.format(Date(movimiento.fecha))

        val esIngreso = movimiento.tipo == "INGRESO"

        val tipoTexto = if (esIngreso) "Ingreso" else "Gasto"

        holder.txtTipo.text = "$tipoTexto · ${movimiento.moneda}"

        val simbolo = when (movimiento.moneda) {
            "GTQ" -> "Q"
            "USD" -> "$"
            "EUR" -> "€"
            else -> movimiento.moneda
        }

        val signo = if (esIngreso) "+" else "-"

        val importe = movimiento.importeCentavos / 100.0

        holder.txtImporte.text =
            "$signo $simbolo ${"%.2f".format(Locale.US, importe)}"

        val color = if (esIngreso) {
            android.graphics.Color.parseColor("#147D70")
        } else {
            android.graphics.Color.parseColor("#D96C63")
        }

        holder.txtImporte.setTextColor(color)
    }

    // Devuelve la cantidad de movimientos.
    override fun getItemCount(): Int {
        return movimientos.size
    }

    // Actualiza los registros que se muestran en pantalla.
    fun actualizarMovimientos(
        nuevosMovimientos: List<MovimientoEntity>
    ) {
        movimientos = nuevosMovimientos
        notifyDataSetChanged()
    }
}
