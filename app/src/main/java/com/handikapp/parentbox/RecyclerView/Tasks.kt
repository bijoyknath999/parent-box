package com.handikapp.parentbox.RecyclerView

class Tasks {
    private var image: String = ""
    private var title: String = ""
    private var content: String = ""
    private var taskid: String = ""
    private var agelimit: Int = 0
    private var publisher: String = ""
    private var uid : String = ""
    private var timestamp : String = ""



    constructor()
    constructor(image: String, title: String, content: String, taskid: String, agelimit: Int, publisher: String, uid: String, timestamp: String) {
        this.image = image
        this.title = title
        this.content = content
        this.taskid = taskid
        this.agelimit = agelimit
        this.publisher = publisher
        this.uid = uid
        this.timestamp = timestamp
    }


    fun getImage(): String{
        return image
    }

    fun setImage(image: String)
    {
        this.image = image
    }

    fun getTitle(): String?{
        return title
    }

    fun setTitle(title: String)
    {
        this.title = title
    }

    fun getContent(): String?{
        return content
    }

    fun setContent(content: String)
    {
        this.content = content
    }

    fun getTaskID(): String?{
        return taskid
    }

    fun setTaskID(taskid: String)
    {
        this.taskid = taskid
    }

    fun getAgeLimit(): Int?{
        return agelimit
    }

    fun setAgeLimit(agelimit: Int)
    {
        this.agelimit = agelimit
    }

    fun getPublisher(): String?{
        return publisher
    }

    fun setPublisher(publisher: String)
    {
        this.publisher = publisher
    }

    fun getUID(): String?{
        return uid
    }

    fun setUID(uid: String)
    {
        this.uid = uid
    }

    fun getTimeStamp(): String?{
        return timestamp
    }

    fun setTimeStamp(timestamp: String)
    {
        this.timestamp = timestamp
    }


}


