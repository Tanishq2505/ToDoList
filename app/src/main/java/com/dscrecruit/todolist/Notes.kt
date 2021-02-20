package com.dscrecruit.todolist




class Notes(val key:Int, val task_name: String, val date: String, val hour: Int, val min: Int, val note: String, val status: String){
    constructor():this(0 ,"","",0,0,"","")
}
