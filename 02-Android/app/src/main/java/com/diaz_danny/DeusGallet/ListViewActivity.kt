package com.diaz_danny.DeusGallet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_list_view.*

class ListViewActivity : AppCompatActivity() {

    val TAG = "ListViewActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_view)

        val listaEntrenadores = arrayListOf<Entrenador>()
        listaEntrenadores.add(Entrenador("Adrian", "Eguez"))
        listaEntrenadores.add(Entrenador("Vicente", "Sarzosa"))
        listaEntrenadores.add(Entrenador("Wendy", "Rojas"))
        listaEntrenadores.add(Entrenador("Juan", "Buran"))
        listaEntrenadores.add(Entrenador("Ivan", "Parra"))
        listaEntrenadores.add(Entrenador("Andrea", "Lara"))
        listaEntrenadores.add(Entrenador("Lisa", "Guerrero"))

        val adaptador = ArrayAdapter(
            this,  // contexto
            android.R.layout.simple_list_item_1,  // nombre de layout
            listaEntrenadores  // lista
        )
        listViewNumeros.adapter = adaptador

        listViewNumeros.onItemClickListener = AdapterView.OnItemClickListener{
            parent, view, position, id ->
            Log.i(TAG, "Posicion $position")
        }

        buttonAdd.setOnClickListener({
            listaEntrenadores.add(Entrenador("Nuevo", "Dato"))
            
            adaptador.notifyDataSetChanged()

            listViewNumeros.setSelection(listaEntrenadores.size-1)

        })


    }
}
