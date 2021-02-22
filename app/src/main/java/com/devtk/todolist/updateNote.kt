package com.devtk.todolist

class updateNote(val key:Int, val task_name: String?, val date: String?, val hour: Int, val minutes: Int, val note: String?, val status: String?) {
    constructor():this(0,"","",0,0,"","INCOMPLETED")
}