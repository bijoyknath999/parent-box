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

class TasksLIstAdapter(topics: List<Tasks>, Task_Two: String, mode: String) : RecyclerView.Adapter<TasksLIstAdapter.ViewHolder?>() {

    private val topics : List<Tasks>
    lateinit var context: Context
    var Task_Two: String = ""
    var mode : String = ""

    init {
        this.topics = topics
        this.Task_Two = Task_Two
        this.mode = mode
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.tasks_list_layout, parent, false)
        context = parent.context
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return topics.size
    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val topic : Tasks = topics[position]
        holder.title.text = topic.getTitle()
        val cText = topic.getContent()
        if (cText!!.length >= 144)
        {
            holder.content.text = cText.substring(0, 144) + "....read more"
        }
        else if(cText.isEmpty())
        {
            holder.content.text = "Click here to full view...."
        }
        else
        {
            holder.content.text = cText+"....read more"
        }

        if (!topic.getImage().isEmpty()) {
            Picasso.get()
                    .load(topic.getImage())
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
            editor.putString(Constants.Class, "OurTasks")
            editor.apply()
            val nextIntent = Intent(context,SingleTaskSelect::class.java)
            nextIntent.putExtra("task_two",Task_Two)
            nextIntent.putExtra("task_id",topic.getTaskID())
            nextIntent.putExtra("img_url",topic.getImage())
            nextIntent.putExtra("mode",mode)
            nextIntent.putExtra("publisher_id",topic.getUID())
            context.startActivity(nextIntent)
        }


        val face2 = Typeface.createFromAsset(context.assets, "fonts/Abel-Regular.ttf")
        holder.content.typeface = face2
    }
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView : ImageView
        val title : TextView
        val content : TextView
        val textImage : TextView
        init {
            imageView = view.findViewById(R.id.topics_image)
            title = view.findViewById(R.id.topics_title)
            content = view.findViewById(R.id.topics_content)
            textImage = view.findViewById(R.id.our_tasks_image_text)
        }
    }
}