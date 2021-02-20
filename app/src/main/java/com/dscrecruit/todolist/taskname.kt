package com.dscrecruit.todolist

class taskname(var task_name: String,var date:String,var hour:Int,var min:Int,var status:String,var note:String,val key:Int) {
    constructor():this("","",0,0,"","",0)
}