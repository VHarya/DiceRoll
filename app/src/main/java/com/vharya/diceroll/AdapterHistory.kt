package com.vharya.diceroll

import android.app.Activity
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class AdapterHistory(
    private val context: Activity,
    private var rollNumber: MutableList<String>,
    private var rollDate: MutableList<String>
    ) : ArrayAdapter<String>(context, R.layout.list_item, rollNumber) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val rowView = inflater.inflate(R.layout.list_item, null, true)

        val viewRollNumber = rowView.findViewById(R.id.roll_value) as TextView
        val viewRollDate = rowView.findViewById(R.id.roll_date) as TextView

        viewRollNumber.text = rollNumber[position]
        viewRollDate.text = rollDate[position]

        return rowView
    }

    override fun notifyDataSetChanged() {
        super.notifyDataSetChanged()
        Log.d("History_Adapter", "Data set changed!")
        Log.d("History_Adapter", "Roll Number = $rollNumber")
        Log.d("History_Adapter", "Roll Date = $rollDate")
    }

    fun updateReceiptsList(newRollNumber: MutableList<String>, newRollDate: MutableList<String>) {
        rollNumber.clear()
        rollDate.clear()

        Log.d("History_Adapter", "Clear Roll Number = $rollNumber")
        Log.d("History_Adapter", "Clear Roll Date = $rollDate")

        rollNumber.addAll(newRollNumber)
        rollDate.addAll(newRollDate)

        Log.d("History_Adapter", "Update called!")
        this.notifyDataSetChanged()
    }

}