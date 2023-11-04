package com.example.monsterhunterfinder.adapter

import android.content.Intent
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.monsterhunterfinder.Entrada
import com.example.monsterhunterfinder.activity_diario_ver
import com.example.monsterhunterfinder.databinding.ItemEntradaBinding
import com.google.firebase.firestore.FirebaseFirestore

class EntradasViewHolder (view: View): RecyclerView.ViewHolder (view) {
    val binding = ItemEntradaBinding.bind(view)



    fun render(entrada: Entrada) {
        binding.textoListaArma.text = entrada.arma
        binding.textoListaTitulo.text = entrada.titulo

        binding.botonVer.setOnClickListener {
            val contexto = itemView.context
            val intent = Intent(contexto, activity_diario_ver::class.java)

            var identificador: String = entrada.numEntrada.toString()
            intent.putExtra("Identificador", identificador)
            contexto.startActivity(intent)
        }
    }
}