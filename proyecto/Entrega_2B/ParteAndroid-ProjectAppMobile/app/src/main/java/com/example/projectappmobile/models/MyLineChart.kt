package com.example.projectappmobile.models

import android.graphics.Color
import android.util.Log
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet


class MyLineChart(
    public var lineChart: LineChart, text: String
) {



    init {
        this.lineChart.description.isEnabled = true
        this.lineChart.description.text = text
        this.lineChart.setTouchEnabled(true)
        this.lineChart.isDragEnabled = true
        this.lineChart.setScaleEnabled(true)
        this.lineChart.setDrawGridBackground(false)
        this.lineChart.setPinchZoom(true)
        this.lineChart.setBackgroundColor(Color.BLACK)

        val data = LineData()
        // data.setValueTextColor(Color.RED)
        // data.setDrawValues(false)
        this.lineChart.data = data
        // get the legend (only possible after setting data)

        val l: Legend = this.lineChart.getLegend()

        // modify the legend ...
        l.form = Legend.LegendForm.LINE
        l.textColor = Color.WHITE

        val xl: XAxis = this.lineChart.getXAxis()
        xl.textColor = Color.WHITE
        xl.setDrawGridLines(false)
        xl.setAvoidFirstLastClipping(true)
        xl.isEnabled = true

        val leftAxis: YAxis = this.lineChart.getAxisLeft()
        leftAxis.textColor = Color.WHITE
        leftAxis.setDrawGridLines(false)
        leftAxis.axisMaximum = 12f
        leftAxis.axisMinimum = -12f
        leftAxis.setDrawGridLines(false)

        val rightAxis: YAxis = this.lineChart.getAxisRight()
        rightAxis.isEnabled = false

        this.lineChart.getAxisLeft().setDrawGridLines(false)
        this.lineChart.getXAxis().setDrawGridLines(false)
        this.lineChart.setDrawBorders(false)
    }

    private fun createSet(label: String, color: Int): LineDataSet {
        val set = LineDataSet(null, label)
        set.axisDependency = YAxis.AxisDependency.LEFT
        set.lineWidth = 1f
        set.setColor(color)
        set.setDrawCircles(false)
        set.setDrawValues(false)
        set.mode = LineDataSet.Mode.LINEAR
        return set
    }

    fun addEntry(value: Float, label: String, color: Int = Color.BLUE, index: Int = 0, view_all:Boolean = false) {
        val data: LineData? = this.lineChart.data

        if (data != null){
            var set: ILineDataSet? = data.getDataSetByIndex(index)

            if(set == null){
                set = createSet(label, color)
                data.addDataSet(set)
            }

            data.addEntry(Entry(set.entryCount.toFloat(), value), index)
            data.notifyDataChanged()
            this.lineChart.notifyDataSetChanged()
            this.lineChart.setMaxVisibleValueCount(Parameters.GESTURE_NUMBER_MEASURES)  // con un número pequeño se desliza automáticamente
            this.lineChart.moveViewToX(data.entryCount.toFloat())

            if(!view_all){
                this.lineChart.setVisibleXRange(0f, Parameters.GESTURE_NUMBER_MEASURES.toFloat() * 2)
            }
            // Log.d("MyLineChart", "addEntry: actualizado y avisado")
        }else{
            Log.e("MyLineChart", "addEntry: La data es null")
        }

        
    }

}