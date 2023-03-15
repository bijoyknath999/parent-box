package com.handikapp.parentbox.RecyclerView

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.handikapp.parentbox.R

class GiftingAdapter(private val context: Activity, private val infoText: Array<String>)
    : ArrayAdapter<String>(context, R.layout.gifting_tips_listview, infoText) {



    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val rowView = inflater.inflate(R.layout.gifting_tips_listview, null, true)

        val InfoText = rowView.findViewById(R.id.gifting_tips_listview_text) as TextView

        InfoText.text = infoText[position]

        return rowView
    }
}