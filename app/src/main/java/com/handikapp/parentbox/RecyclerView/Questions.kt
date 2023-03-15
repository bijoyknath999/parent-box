package com.handikapp.parentbox.RecyclerView

class Questions {
    private var ans: Int = 0
    private var optionOne: String = ""
    private var optionTwo: String = ""
    private var optionThree: String = ""
    private var question: String = ""


    constructor()
    constructor(ans: Int, optionOne: String, optionTwo: String, optionThree: String, question: String) {
        this.ans = ans
        this.optionOne = optionOne
        this.optionTwo = optionTwo
        this.optionThree = optionThree
        this.question = question
    }


    fun getAns(): Int
    {
        return ans
    }
    fun getQuestion(): String
    {
        return question
    }
    fun getOptionOne(): String
    {
        return optionOne
    }
    fun getOptionTwo(): String
    {
        return optionTwo
    }
    fun getOptionThree(): String
    {
        return optionThree
    }


}