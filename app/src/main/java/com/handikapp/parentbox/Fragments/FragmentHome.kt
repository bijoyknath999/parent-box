package com.handikapp.parentbox.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.handikapp.parentbox.*
import com.handikapp.parentbox.R
import com.handikapp.parentbox.Utils.Constants


class FragmentHome : Fragment() {

    private lateinit var AvailableTasks : CardView
    private lateinit var RandomTasks : CardView
    private lateinit var OthersParentTasks : CardView
    private lateinit var CreateTasks : CardView
    private lateinit var CreateTasksAdmin : CardView

    var personEmail : String = ""
    var AdminMail : String = ""
/*
    lateinit var mAdView : AdView
*/






    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        (activity as MainActivity).setActionBarTitle("Home")

        AvailableTasks = view.findViewById(R.id.topics_our)
        RandomTasks = view.findViewById(R.id.random_our)
        OthersParentTasks = view.findViewById(R.id.other_parents_our)
        CreateTasks = view.findViewById(R.id.create_tasks_our)
        CreateTasksAdmin = view.findViewById(R.id.create_tasks_admin)


        val acct = GoogleSignIn.getLastSignedInAccount(activity)
        if (acct != null) {
            personEmail = acct.email!!
        }

        val sharedPreferences = (activity as MainActivity).getSharedPreferences("sharedPreferences_admin", AppCompatActivity.MODE_PRIVATE)
        AdminMail = sharedPreferences.getString(Constants.AdminMail, "")!!
        if (!AdminMail.isEmpty())
        {
            if (personEmail == AdminMail)
            {
                CreateTasksAdmin.visibility = View.VISIBLE
                CreateTasks.visibility = View.GONE
                CreateTasksAdmin.setOnClickListener {
                    val intent5 = Intent(activity, CreateTask::class.java)
                    intent5.putExtra("publisher", "admin")
                    startActivity(intent5)
                }
            }
        }

        AvailableTasks.setOnClickListener {
            val intent = Intent(activity, OurTasks::class.java)
            startActivity(intent)
        }
        RandomTasks.setOnClickListener {
            val intent3 = Intent(activity, RandomsTask::class.java)
            startActivity(intent3)
        }
        OthersParentTasks.setOnClickListener {
            val intent3 = Intent(activity, ParentsTaskList::class.java)
            startActivity(intent3)

        }
        CreateTasks.setOnClickListener {
            val intent4 = Intent(activity, CreateTask::class.java)
            intent4.putExtra("publisher", "parents")
            startActivity(intent4)
        }
        return view
    }
}