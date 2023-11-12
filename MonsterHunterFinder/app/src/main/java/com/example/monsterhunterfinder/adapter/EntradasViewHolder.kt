package com.example.monsterhunterfinder.adapter

import android.content.DialogInterface
import android.content.Intent
import android.os.Parcelable
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.monsterhunterfinder.Entrada
import com.example.monsterhunterfinder.R
import com.example.monsterhunterfinder.activity_diario_ver
import com.example.monsterhunterfinder.databinding.ItemEntradaBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.io.Serializable

/**
 *  Clase encargada de ubicar los datos de un elemento
 *  individual (aquellos que aparecerán listados en la
 *  RecyclerView). Extiende de RecyclerView.ViewHolder.
 *
 * @param view Layout inflado para poder bindearlo (enlazarlo)
 * y ubicar los datos en las views de que disponga
 * @author Jaime
 */
class EntradasViewHolder (view: View): RecyclerView.ViewHolder (view) {
    val binding = ItemEntradaBinding.bind(view)


    /**
     * Método que renderizará cada elemento del RecyclerView,
     * asignando los datos de cada elemento a las views presentes
     * en el layout que se esté utilizando.
     *
     * @param entradaDiario La entrada del diario a mostrar
     * @param borrarRegistro Método para eliminar registros; su implementación se
     * encuentra en activity_diario_vista
     * @param lanzarActivityModificar Método para abrir actividad del detalle; su
     * implementación se encuentra en activity_diario_vista
     */
    fun render(entradaDiario: Entrada,
               borrarRegistro:(Int, Int) -> Unit,
               lanzarActivityModificar:(Int, Entrada) -> Unit) {


        // Los datos de la entrada del diario recibida se
        // asignan a las views que conforman el layout
        binding.textoListaArma.text = entradaDiario.arma
        binding.textoListaTitulo.text = entradaDiario.titulo



        // Se establece un listener para el botón de ver en detalle.
        // Este listener envía un intent que contiene como putExtra
        // un identificador del registro que se desee ver.
        // Dicho dato será recogido en la activity_diario_ver, se accederá
        // al documento de la Firestore al que corresponda y se mostrarán
        // sus datos.
        binding.botonVer.setOnClickListener {
            val contexto = itemView.context
            val intent = Intent(contexto, activity_diario_ver::class.java)

            var identificador: String = entradaDiario.numEntrada.toString()
            intent.putExtra("Identificador", identificador)

            contexto.startActivity(intent)
        }


        // Se establece un listener para el botón de borrado. Cuando se acciona,
        // se abre un cuadro de diálogo que pregunta al usuario si está seguro,
        // dado que es una acción destructiva. Si se presiona en la confirmación,
        // se invoca el método de borrado, el cual se ha pasado por parámetro y se
        // ha desarrollado en activity_diario_vista.
        binding.botonEliminar.setOnClickListener {
            val builder = AlertDialog.Builder(itemView.context)

            builder.setTitle(R.string.cuidado)
                .setMessage(R.string.seguroBorrado)
                .setCancelable(false)
                .setPositiveButton(
                    R.string.vaciarFiltroSi,
                    DialogInterface.OnClickListener{
                            dialog, id -> borrarRegistro(adapterPosition, entradaDiario.numEntrada)
                    }
                )
                .setNegativeButton(
                    R.string.vaciarFiltroNo,
                    DialogInterface.OnClickListener{
                            dialog, id -> dialog.cancel()
                    }
                )
                .show()
        }

        // Se establece un listener para el botón de ediciones y modificaciones.
        // Se indica que el método al que se llama cuando se acciona el botón es
        // el necesario para modificar registros, el cual se ha pasado por parámetro
        // y se ha desarrollado en activity_diario_vista.
        binding.botonEditar.setOnClickListener {
            lanzarActivityModificar(adapterPosition, entradaDiario)
        }
    }
}