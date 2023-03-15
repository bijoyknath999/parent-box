package com.handikapp.parentbox.Utils

object AgeCheck {
    fun getCheck(agelimit: Int,childs_age: Int): Boolean{
        var check : Boolean = false
        if(agelimit == childs_age)
            check = true
        else if (agelimit == childs_age+1)
            check = true
        else if (agelimit == childs_age+2)
            check = true
        else if (agelimit == childs_age-1)
            check = true
        else if (agelimit == childs_age-2)
            check = true
        return check
    }
}