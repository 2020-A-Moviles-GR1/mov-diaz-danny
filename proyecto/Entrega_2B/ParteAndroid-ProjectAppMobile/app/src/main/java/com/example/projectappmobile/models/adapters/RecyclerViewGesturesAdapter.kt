package com.example.projectappmobile.models.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.recyclerview.widget.RecyclerView
import com.example.projectappmobile.R
import com.example.projectappmobile.models.database.Function
import com.example.projectappmobile.models.database.Gesture


class RecyclerViewGesturesAdapter(
    private val mContext: Context,
    private val listGestures: ArrayList<Gesture>,
    private val spinnerArray: List<String?>,
    private val presenter: RecyclerViewGesturesAdapterInterface
) : RecyclerView.Adapter<RecyclerViewGesturesAdapter.MyViewHolder>() {

    interface RecyclerViewGesturesAdapterInterface {
        fun onEdit(gesture: Gesture, position: Int)
        fun onDelete(gesture: Gesture, position: Int)
        fun onSpinnerItemClickListener(gesture: Gesture, i: Int, l: Long)
    }

    fun removeGesture(position: Int) {
        this.listGestures.removeAt(position)
    }

    inner class MyViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        val textViewLayoutMGGestureName: TextView
        val textViewLayoutMGFunctionDescription: TextView
        val spinnerLayoutMGFunctionsList: Spinner
        val imageButtonLayoutMGEdit: ImageButton
        val imageButtonLayoutMGDelete: ImageButton


        init {
            textViewLayoutMGGestureName = view.findViewById(R.id.textViewLayoutMGGestureName)
            textViewLayoutMGFunctionDescription = view.findViewById(R.id.textViewLayoutMGFunctionDescription)
            spinnerLayoutMGFunctionsList = view.findViewById(R.id.spinnerLayoutMGFunctionsList)
            imageButtonLayoutMGEdit = view.findViewById(R.id.imageButtonLayoutMGEdit)
            imageButtonLayoutMGDelete = view.findViewById(R.id.imageButtonLayoutMGDelete)
        }



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(
                R.layout.layout_my_gesture,
                parent,
                false
            )
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return listGestures.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val gesture = listGestures.get(position)
        holder.textViewLayoutMGGestureName.text = gesture.name;
        holder.textViewLayoutMGFunctionDescription.text = "${Function.mapFunctionToDescription[gesture.id_function]}";

        val adapter = ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, spinnerArray)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        holder.spinnerLayoutMGFunctionsList.setAdapter(adapter)

        val selected_position = getSelectedPosition(gesture.id_function)
        holder.spinnerLayoutMGFunctionsList.setSelection(selected_position)

        holder.spinnerLayoutMGFunctionsList.setOnItemSelectedListener(object : OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View,
                position: Int,
                id: Long
            ) {
                // your code here
                //todo: validar que la nueva función no sea utilizada por otra y que no es igual a la ya seleccionada, porque al inicio se ejecuta
                Log.d("RecyclerAdapterGesture", "Position: $position, ID: $id")
                presenter.onSpinnerItemClickListener(gesture, position, id)
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // your code here
            }
        })

        holder.imageButtonLayoutMGEdit.setOnClickListener{
            presenter.onEdit(gesture, position)
        }

        holder.imageButtonLayoutMGDelete.setOnClickListener{
            presenter.onDelete(gesture, position)
        }

    }

    private fun getSelectedPosition(id_function: Int): Int {
        var selected_position = -1;

        for (i in 0..(spinnerArray.size-1)) {

            if(spinnerArray[i]?.equals(Function.mapFunctionToName[id_function])!!){
                selected_position = i;
                break;
            }
        }

        return selected_position
    }


}