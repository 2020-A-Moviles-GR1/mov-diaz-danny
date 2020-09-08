package com.diaz_danny.DeusGallet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_recycler_view.*

class RecyclerViewActivity : AppCompatActivity() {

    var numeroLikes = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler_view)


        val listaUsuario = arrayListOf<UsuarioHttp>()

        for( i in 0..20){
            listaUsuario.add(UsuarioHttp(
                i, 1231414*i.toLong(), 123142514*i.toLong(),
                "1${i}20254224", "Danny$i",
                "danny$i.diaz@epn.edu.ec",
                "Soltero", "123$i",
                arrayListOf<PokemonHttp>()
                )
            )
        }

        val adaptador = RecyclerAdaptador(
            listaUsuario,
            this
        )
        recyclerView.adapter = adaptador
        recyclerView.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.VERTICAL, false)
        recyclerView.itemAnimator = DefaultItemAnimator()

        adaptador.notifyDataSetChanged()





    }

    fun anadirLikesEnActividad(numero: Int){
        this.numeroLikes += numero
        textViewTitulo.text = this.numeroLikes.toString()

    }
}