package com.devtk.todolist

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.devtk.todolist.*
import com.google.firebase.database.FirebaseDatabase

var database_task_details = FirebaseDatabase.getInstance().reference

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class TaskDetailsActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_task_details)



        val task_name = intent.getStringExtra(HomepageActivity.TASK_NAME_KEY)
        val task_date = intent.getStringExtra(HomepageActivity.TASK_DATE_KEY)
        val task_hour = intent.getIntExtra(HomepageActivity.TASK_HOUR_KEY,0)
        val task_minutes = intent.getIntExtra(HomepageActivity.TASK_MINUTE_KEY,0)
        val task_note = intent.getStringExtra(HomepageActivity.TASK_NOTE_KEY)
        val task_status = intent.getStringExtra(HomepageActivity.TASK_COMPLETED_KEY)
        val task_key = intent.getIntExtra(HomepageActivity.TASK_UID_KEY,0)

        val tv_task_name_var=findViewById<EditText>(R.id.task_details_task_name)
        val tv_date_view=findViewById<EditText>(R.id.date_view)
        val tv_hour_view=findViewById<EditText>(R.id.hour_view)
        val tv_minutes_view=findViewById<EditText>(R.id.minutes_view)
        val tv_task_notes=findViewById<EditText>(R.id.note_textview)
        val task_details_mac = findViewById<Button>(R.id.task_details_mac)
        val edit_btn = findViewById<Button>(R.id.task_details_edit_b)
        val save_btn = findViewById<Button>(R.id.save_btn)
        val exit_btn = findViewById<Button>(R.id.exit_btn)
        val task_details_delete_btn = findViewById<Button>(R.id.task_details_delete_btn)

        var updated_task_name = task_name
        var updated_task_date = task_date
        var updated_task_hour = task_hour
        var updated_task_minutes = task_minutes
        var updated_task_notes = task_note
        var updated_task_status = task_status

        if(task_status == "COMPLETED"){
            task_details_mac.text = "Mark As Incomplete"
        }else{
            task_details_mac.text = "Mark As Complete"
        }
        tv_hour_view.isEnabled = false
        tv_task_name_var.isEnabled = false
        tv_minutes_view.isEnabled = false
        tv_task_notes.isEnabled = false
        tv_date_view.isEnabled = false
        tv_task_name_var.setText(task_name)
        if (task_date == ""){
            tv_date_view.setText("No Date Added")
            tv_date_view.setTextAppearance(android.R.style.TextAppearance_DeviceDefault_Medium)
        } else{
            tv_date_view.setText(task_date)
        }
        tv_hour_view.setText(task_hour.toString())
        tv_minutes_view.setText(task_minutes.toString())
        tv_task_notes.setText(task_note)
        if (task_status == "COMPLETED"){
            Toast.makeText(this,"Task Completed",Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this,"Task InComplete",Toast.LENGTH_SHORT).show()

        }


        edit_btn.setOnClickListener {
            Toast.makeText(this,"You can now edit!",Toast.LENGTH_SHORT).show()
            tv_date_view.isEnabled = true
            tv_hour_view.isEnabled = true
            tv_task_name_var.isEnabled = true
            tv_minutes_view.isEnabled = true
            tv_task_notes.isEnabled = true
            task_details_mac.visibility = View.GONE
            save_btn.visibility = View.VISIBLE
            exit_btn.visibility = View.VISIBLE
            edit_btn.visibility = View.GONE
            task_details_delete_btn.visibility = View.GONE

        }

        save_btn.setOnClickListener {
            val task_name_edited = tv_task_name_var.text.toString()
            val task_date_edited = tv_date_view.text.toString()
            val task_hour_edited = tv_hour_view.text.toString().toInt()
            val task_minutes_edited = tv_minutes_view.text.toString().toInt()
            val task_note_edited = tv_task_notes.text.toString()
            if(task_name_edited.equals(updated_task_name) && task_date_edited.equals(updated_task_date) && task_hour_edited.equals(updated_task_hour)
                    && task_minutes_edited.equals(updated_task_minutes) && task_note_edited.equals(updated_task_notes)){
                Toast.makeText(this,"No Changes Made", Toast.LENGTH_SHORT).show()
            } else{
                updated_task_name = task_name_edited
                updated_task_date = task_date_edited
                updated_task_hour = task_hour_edited
                updated_task_minutes = task_minutes_edited
                updated_task_notes = task_note_edited
                val updated_data = updated_task_status?.let { it1 ->
                    updateNote(task_key,updated_task_name,updated_task_date,updated_task_hour,updated_task_minutes,updated_task_notes,
                            it1
                    )
                }
                database.child("Users").child(firebaseAuthCUser).child("Notes").child(task_key.toString()).setValue(updated_data)
                Toast.makeText(this,"Changes Saved", Toast.LENGTH_SHORT).show()

            }
            save_btn.visibility = View.GONE
            exit_btn.visibility = View.GONE
            task_details_mac.visibility = View.VISIBLE
            edit_btn.visibility = View.VISIBLE
            tv_date_view.isEnabled = false
            tv_hour_view.isEnabled = false
            tv_task_name_var.isEnabled = false
            tv_minutes_view.isEnabled = false
            tv_task_notes.isEnabled = false
            task_details_delete_btn.visibility = View.VISIBLE
        }
        val exit_button =  findViewById<Button>(R.id.exit_btn)
        exit_button.setOnClickListener {
            Toast.makeText(this,"No Field Edited",Toast.LENGTH_SHORT).show()
            save_btn.visibility = View.GONE
            exit_btn.visibility = View.GONE
            edit_btn.visibility = View.VISIBLE
            task_details_mac.visibility = View.VISIBLE
            task_details_delete_btn.visibility = View.VISIBLE
            tv_date_view.isEnabled = false
            tv_hour_view.isEnabled = false
            tv_task_name_var.isEnabled = false
            tv_minutes_view.isEnabled = false
            tv_task_notes.isEnabled = false
        }
        val delete_btn =  findViewById<Button>(R.id.task_details_delete_btn)
        delete_btn.setOnClickListener {
            Toast.makeText(this,"Task Deleted",Toast.LENGTH_SHORT).show()
            database.child("Users").child(firebaseAuthCUser).child("Notes").child(task_key.toString()).removeValue()
            finish()
            startActivity(Intent(this,HomepageActivity::class.java))
        }
        val mac_btn = findViewById<Button>(R.id.task_details_mac)
        mac_btn.setOnClickListener {
            Toast.makeText(this,"Task Completed",Toast.LENGTH_SHORT).show()
            if (task_status == "COMPLETED"){
                mac_btn.text = "Mark As InComplete"
                val task_status = "INCOMPLETED"
                updated_task_name = task_name
                updated_task_date = task_date
                updated_task_hour = task_hour
                updated_task_minutes = task_minutes
                updated_task_notes = task_note
                val updated_data = updateNote(task_key,updated_task_name,updated_task_date,updated_task_hour,updated_task_minutes,updated_task_notes,task_status)
                database.child("Users").child(firebaseAuthCUser).child("Notes").child(task_key.toString()).setValue(updated_data)
                mac_btn.text = "Mark As Completed"
            }else{
                val task_status = "COMPLETED"
                updated_task_name = task_name
                updated_task_date = task_date
                updated_task_hour = task_hour
                updated_task_minutes = task_minutes
                updated_task_notes = task_note
                val updated_data = updateNote(task_key,updated_task_name,updated_task_date,updated_task_hour,updated_task_minutes,updated_task_notes,task_status)
                database.child("Users").child(firebaseAuthCUser).child("Notes").child(task_key.toString()).setValue(updated_data)
                mac_btn.text = "Mark As Incomplete"
            }
        }
    }



}


