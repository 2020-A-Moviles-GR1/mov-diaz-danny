package com.diaz_danny.DeusGallet

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecyclerAdaptador(
    private val listaUsuarios: List<UsuarioHttp>,
    private val contexto: RecyclerViewActivity
) : RecyclerView.Adapter<RecyclerAdaptador.MyViewHolder>() {

    inner class MyViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        val nombreTextView: TextView
        val cedulaTextView: TextView
        val accionButton: Button

        init {
            nombreTextView = view.findViewById(R.id.textViewAPNombre)
            cedulaTextView = view.findViewById(R.id.textViewAPCedula)
            accionButton = view.findViewById(R.id.buttonAPAccion)
        }



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(
                R.layout.adaptador_persona,
                parent,
                false
            )
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return listaUsuarios.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val usuario = listaUsuarios.get(position)
        holder.nombreTextView.text = usuario.nombre;
        holder.cedulaTextView.text = usuario.cedula;
        holder.accionButton.text = "Like ${usuario.nombre}"

    }


}