package com.handikapp.parentbox.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import com.handikapp.parentbox.MainActivity
import com.handikapp.parentbox.OurTasks
import com.handikapp.parentbox.R
import com.handikapp.parentbox.RandomsTask
import com.handikapp.parentbox.Utils.Constants

class FragmentPlaymode : Fragment() {
    lateinit var FreeMode : CardView
    lateinit var QuickMode : CardView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_playmode, container, false)

        (activity as MainActivity).setActionBarTitle("Play Mode")
        FreeMode = view.findViewById(R.id.play_mode_free)
        QuickMode = view.findViewById(R.id.play_mode_quick)


        FreeMode.setOnClickListener {
            val freeintent = Intent(activity,OurTasks::class.java)
            freeintent.putExtra("mode","free")
            startActivity(freeintent)
        }

        QuickMode.setOnClickListener {
            val quickintent = Intent(activity,RandomsTask::class.java)
            quickintent.putExtra("mode","quick")
            startActivity(quickintent)
        }
        return view
    }
}