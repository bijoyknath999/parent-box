package com.handikapp.parentbox.Utils

import java.util.*

object DOBCoverter {
    @JvmStatic
    fun getAge(year: Int, month: Int): Int {
        val dob = Calendar.getInstance()
        val today = Calendar.getInstance()

        dob.set(year, month,0)

        var age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--
        }

        val ageInt : Int = age + 1

        return ageInt
    }
}