package com.handikapp.parentbox.RecyclerView

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.handikapp.parentbox.R
import com.handikapp.parentbox.SingleTaskSelect
import com.handikapp.parentbox.Utils.Constants
import com.squareup.picasso.Picasso

class ParentsTaskAdapter(ptasks: List<PartentsTask>, taskTwo: String) : RecyclerView.Adapter<ParentsTaskAdapter.ViewHolder?>() {

    private val ptasks : List<PartentsTask>
    lateinit var context: Context
    var taskTwo: String = ""

    init {
        this.ptasks = ptasks
        this.taskTwo = taskTwo
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.p_tasks_list_layout, parent, false)
        context = parent.context
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return ptasks.size
    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val ptask : PartentsTask = ptasks[position]
        holder.title.text = ptask.getTitle()
        val cText = ptask.getContent()
        if (cText!!.length >= 144)
        {
            holder.content.text = cText.substring(0, 144) + "....read more"
        }
        else if (cText.isEmpty())
        {
            holder.content.text = "Click here to full view...."
        }
        else
        {
            holder.content.text = cText+"....read more"
        }

        if (!ptask.getImage().isEmpty()) {
            Picasso.get()
                    .load(ptask.getImage())
                    .into(holder.imageView)
        } else {
            Picasso.get()
                    .load(R.drawable.app_logo_1024)
                    .into(holder.imageView)
        }

        holder.itemView.setOnClickListener {
            val sharedPreferences = context.getSharedPreferences("sharedPreferences_tasks",
                    AppCompatActivity.MODE_PRIVATE
            )
            val editor = sharedPreferences.edit()
            editor.putString(Constants.Class, "ParentsTaskList")
            editor.apply()
            val nextIntent = Intent(context, SingleTaskSelect::class.java)
            nextIntent.putExtra("task_two",taskTwo)
            nextIntent.putExtra("task_id",ptask.getTaskID())
            nextIntent.putExtra("img_url",ptask.getImage())
            context.startActivity(nextIntent)
        }

        val face = Typeface.createFromAsset(context.assets, "fonts/FjallaOne-Regular.ttf")
        holder.title.typeface = face
        val face2 = Typeface.createFromAsset(context.assets, "fonts/Abel-Regular.ttf")
        holder.content.typeface = face2
    }
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView : ImageView
        val title : TextView
        val content : TextView
        val textImage : TextView
        init {
            imageView = view.findViewById(R.id.parents_task_image)
            title = view.findViewById(R.id.parents_task_title)
            content = view.findViewById(R.id.parents_task_content)
            textImage = view.findViewById(R.id.parents_task_image_text)
        }
    }
}