package com.handikapp.parentbox.RecyclerView

class Childs {
    private var name: String = ""
    private var age: Int = 0
    private var dobmonth: Int = 0
    private var dobyear: Int = 0
    private var id: String = ""
    private var trophies: Int = 0

    constructor()
    constructor(name: String, age: Int, dobmonth: Int, dobyear: Int, id: String, trophies: Int) {
        this.name = name
        this.age = age
        this.dobmonth = dobmonth
        this.dobyear = dobyear
        this.id = id
        this.trophies = trophies
    }


    fun getName(): String{
        return name
    }
    fun setName(name : String)
    {
        this.name = name
    }

    fun getAge(): Int{
        return age
    }
    fun setAge(age : Int)
    {
        this.age = age
    }

    fun getDobMonth(): Int{
        return dobmonth
    }
    fun setDobMonth(dobmonth: Int)
    {
        this.dobmonth = dobmonth
    }

    fun getDobYear(): Int{
        return dobyear
    }
    fun setDobYear(dobyear: Int)
    {
        this.dobyear = dobyear
    }

    fun getID(): String{
        return id
    }
    fun setID(id : String)
    {
        this.id = id
    }
    fun getTrophies(): Int{
        return trophies
    }
    fun setTrophies(trophies: Int)
    {
        this.trophies = trophies
    }

}