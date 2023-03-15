package com.handikapp.parentbox.RecyclerView

class Gifts {
    private var pname: String = ""
    private var childID: String = ""
    private var childName: String = ""
    private var taskIDOne: String = ""
    private var taskIDTwo: String = ""
    private var taskOneTitle: String = ""
    private var taskTwoTitle: String = ""
    private var giftImg: String = ""
    private var selfieImg: String = ""
    private var timestamp: String = ""
    private var giftID: String = ""



    constructor()
    constructor(
        pname: String,
        childID: String,
        childName: String,
        taskIDOne: String,
        taskIDTwo: String,
        taskOneTitle: String,
        taskTwoTitle: String,
        giftImg: String,
        selfieImg: String,
        timestamp: String,
        giftID: String
    ) {
        this.pname = pname
        this.childID = childID
        this.childName = childName
        this.taskIDOne = taskIDOne
        this.taskIDTwo = taskIDTwo
        this.taskOneTitle = taskOneTitle
        this.taskTwoTitle = taskTwoTitle
        this.giftImg = giftImg
        this.selfieImg = selfieImg
        this.timestamp = timestamp
        this.giftID = giftID
    }


    fun getPName(): String{
        return pname
    }
    fun getChildID(): String{
        return childID
    }
    fun getChildName(): String{
        return childName
    }
    fun getTaskIDOne(): String{
        return taskIDOne
    }
    fun getTaskIDTwo(): String{
        return taskIDTwo
    }
    fun getTaskOneTitle(): String{
        return taskOneTitle
    }
    fun getTaskTwoTitle(): String{
        return taskTwoTitle
    }
    fun getGiftImg(): String{
        return giftImg
    }
    fun getSelfieImg(): String{
        return selfieImg
    }
    fun getTimeStamp(): String{
        return timestamp
    }
    fun getGiftID(): String{
        return giftID
    }


}