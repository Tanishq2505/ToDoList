package com.codingninjassrm.letstodoapp

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.dscrecruit.todolist.HomepageActivity
import com.dscrecruit.todolist.R
import com.dscrecruit.todolist.taskname
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class AddItem : AppCompatActivity() {
    var txtView1: TextView? = null
    var txtView2: TextView? = null
    var note:TextView?=null
    var calender: DatePicker? = null
    var imgbutton: ImageButton? = null
    var date_view: TextView? = null
    var timePicker1: TimePicker? = null
    var imgbutton1: ImageButton? = null
    var time: TextView? = null
    var txtView3:TextView?=null
    var submit:Button?=null
    var database = FirebaseDatabase.getInstance().reference



    var count = 0
    var no=0;
    var d=false
    var d1=false
    var editText:EditText?=null
    var editTextnote:EditText?=null



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_item)
        val currentFirebaseUser = FirebaseAuth.getInstance().currentUser
        createNotificationChannel()
        txtView1 = findViewById<View>(R.id.textView) as TextView
        val txtView2 = findViewById<View>(R.id.textView2)
        val txtView3 = findViewById<View>(R.id.textView3)
        val txtView4 = findViewById<View>(R.id.textView4)
        note = findViewById<View>(R.id.note) as TextView
        editText=findViewById<View>(R.id.editText) as EditText
        editTextnote=findViewById<View>(R.id.editTextnote) as EditText

        timePicker1 = findViewById<View>(R.id.timePicker1) as TimePicker
        time = findViewById<View>(R.id.time) as TextView
        imgbutton1 = findViewById<View>(R.id.imageButton2) as ImageButton
        date_view = findViewById<View>(R.id.date_view) as TextView
        imgbutton = findViewById<View>(R.id.imageButton) as ImageButton
        submit = findViewById<View>(R.id.submit) as Button

        calender = findViewById<View>(R.id.calender) as DatePicker



        imgbutton!!.setOnClickListener {
            count++
            d=true
            if (count % 2 != 0){
                txtView2.visibility = View.INVISIBLE
                editText!!.visibility = View.INVISIBLE
                date_view!!.visibility = View.INVISIBLE
                txtView4.visibility = View.INVISIBLE
                calender!!.visibility = View.VISIBLE
                txtView3!!.visibility=View.INVISIBLE
                imgbutton1!!.visibility=View.INVISIBLE
                time!!.visibility=View.INVISIBLE
                submit!!.visibility=View.INVISIBLE
                note!!.visibility=View.INVISIBLE
                editTextnote!!.visibility=View.INVISIBLE



            }
            else if (count % 2 == 0){
                txtView2.visibility = View.VISIBLE
                editText!!.visibility = View.VISIBLE
                txtView4.visibility = View.VISIBLE
                date_view!!.visibility = View.VISIBLE
                calender!!.visibility = View.GONE
                txtView3!!.visibility=View.VISIBLE
                imgbutton1!!.visibility=View.VISIBLE
                submit!!.visibility=View.VISIBLE
                time!!.visibility=View.VISIBLE
                note!!.visibility=View.VISIBLE
                editTextnote!!.visibility=View.VISIBLE




            }
            val Date = (calender!!.dayOfMonth.toString() + "-"
                    + (calender!!.month + 1) + "-" + calender!!.year)
            date_view!!.text = Date

        }
        imgbutton1!!.setOnClickListener {
            count++
            d1=true
            if (count % 2 != 0) {
                txtView2.visibility = View.INVISIBLE
                editText!!.visibility = View.INVISIBLE
                time!!.visibility= View.INVISIBLE
                timePicker1!!.visibility = View.VISIBLE
                txtView3!!.visibility=View.INVISIBLE
                submit!!.visibility=View.INVISIBLE
                note!!.visibility=View.INVISIBLE
                editTextnote!!.visibility=View.INVISIBLE




            }
            else if (count % 2 == 0){
                txtView2.visibility = View.VISIBLE
                editText!!.visibility = View.VISIBLE
                time!!.visibility = View.VISIBLE
                timePicker1!!.visibility = View.GONE
                txtView3!!.visibility=View.VISIBLE
                submit!!.visibility=View.VISIBLE
                note!!.visibility=View.VISIBLE
                editTextnote!!.visibility=View.VISIBLE



            }
            val hour = timePicker1!!.hour
            val min = timePicker1!!.minute
            val time1 = "$hour:$min"
            time!!.text = time1
        }

        submit!!.setOnClickListener{

            var status="INCOMPLETE"

            var n=editTextnote!!.text.toString()
            var task_name= editText!!.text.toString()
            val Date = (calender!!.dayOfMonth.toString() + "-"
                    + (calender!!.month + 1) + "-" + calender!!.year)
            val hour = timePicker1!!.hour
            val min = timePicker1!!.minute
            no= (0..100000).random()

                if(d!=false&& d1!=false && editText!!.text.toString()!=" ") {
                    database.child("Users").child(currentFirebaseUser!!.getUid()).child("Notes").child(no.toString()).setValue(
                        taskname(task_name, Date, hour, min, status, n,no)
                    )
                    Toast.makeText(this, "Reminder set!", Toast.LENGTH_SHORT).show()
                    val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0)
                    val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager


                    val myDate=(calender!!.year.toString() + "/"
                            + (calender!!.month.toString() + 1) + "/" + calender!!.dayOfMonth.toString())+" "+timePicker1!!.currentHour+":"+timePicker1!!.currentMinute+":0"
                    val spdf = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
                    val date = spdf.parse(myDate)
                    val timeInMillis = date.time
                    Log.d("time", timeInMillis.toString())

                    alarmManager[AlarmManager.RTC_WAKEUP, timeInMillis] = pendingIntent
                    finish()
                    startActivity(Intent(this, HomepageActivity::class.java))
                }
                else{
                    var message="You haven't filled the mandatory fields"
                    var toast = Toast.makeText(this, message, Toast.LENGTH_LONG)
                    toast.show()             }

        }

    }


    fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name="NOTIFICATION TITLE"
            val descriptionText ="Notification description"
            val importance= NotificationManager.IMPORTANCE_DEFAULT
            val channel= NotificationChannel("notifyLemubit", name, importance).apply {
                description=descriptionText
            }
            val notificationManager:NotificationManager=getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

        }


    }

}

