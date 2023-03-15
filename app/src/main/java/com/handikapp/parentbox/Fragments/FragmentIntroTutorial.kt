package com.handikapp.parentbox.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.handikapp.parentbox.*
import kotlinx.android.synthetic.main.fragment_intro_tutorial.view.*

class FragmentIntroTutorial : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_intro_tutorial, container, false)
        view.fragment_intro_tutorial_btn.setOnClickListener {
            val Lintent = Intent(activity, Tutorial::class.java)
            Lintent.putExtra("class","intro")
            startActivity(Lintent)
        }
        return view
    }
}