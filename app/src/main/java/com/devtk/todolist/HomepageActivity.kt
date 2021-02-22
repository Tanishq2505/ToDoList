package com.devtk.todolist

import android.content.ComponentName
import android.content.DialogInterface
import android.content.Intent
import android.content.Intent.*
import android.graphics.Paint
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item


var database = FirebaseDatabase.getInstance().reference
var small_database = FirebaseDatabase.getInstance().reference
val firebaseAuthCUser = FirebaseAuth.getInstance().currentUser!!.uid
private var onBackPressedToexit = false
class HomepageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)
        findViewById<RecyclerView>(R.id.all_tasks_recycler).layoutManager = LinearLayoutManager(this)
        val sharedPreferences = getSharedPreferences("sharedPrefs", AppCompatActivity.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val mode = AppCompatDelegate.getDefaultNightMode()
        val isDarkModeOn = sharedPreferences.getBoolean("isDarkModeOn", false)
        onResume().apply {
            if (mode == AppCompatDelegate.MODE_NIGHT_YES){
                editor.putBoolean("isDarkModeOn", true)
                editor.apply()
            } else {
                editor.putBoolean("isDarkModeOn", false)
                editor.apply()
            }
        }
        if (isDarkModeOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)}

        fetchNotes()
        val b_navbar = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        b_navbar.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.addTask_button -> {
                    startActivity(Intent(this, AddItem::class.java))
                    true
                }
                R.id.profile_button -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                R.id.home_button -> {
                    startActivity(Intent(this, HomepageActivity::class.java))
                    finish()
                    true
                }


                else -> false
            }
        }

    }

    override fun onBackPressed() {
        if (onBackPressedToexit){
            finishAffinity()
            super.onBackPressed()
            return
        }
        onBackPressedToexit = true
        Toast.makeText(this, "Please press BACK again to exit", Toast.LENGTH_SHORT).show()
        Handler().postDelayed(Runnable { onBackPressedToexit = false }, 2000)
    }
    companion object{
        val TASK_NAME_KEY = "TASK_NAME_KEY"
        val TASK_DATE_KEY = "TASK_DATE_KEY"
        val TASK_HOUR_KEY = "TASK_HOUR_KEY"
        val TASK_MINUTE_KEY = "TASK_MINUTE_KEY"
        val TASK_NOTE_KEY = "TASK_NOTE_KEY"
        val TASK_COMPLETED_KEY = "TASK_COMPLETED_KEY"
        val TASK_UID_KEY = "TASK_KEY"
    }
    private fun fetchNotes(){
        database.child("Users").child(firebaseAuthCUser).child("Notes").addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val adapter = GroupAdapter<GroupieViewHolder>()

                        snapshot.children.forEach {
                            val name_display = it.getValue(Notes::class.java)
                            Log.d("Data?", it.toString())
                            if (name_display != null) {
                                adapter.add(tasks_viewholder(adapter,name_display))

                            }
                        }
                        findViewById<RecyclerView>(R.id.all_tasks_recycler).adapter = adapter


                        adapter.setOnItemClickListener { item, view ->
                            val userData = item as tasks_viewholder
                            val intent = Intent(view.context, TaskDetailsActivity::class.java)
                            intent.putExtra(TASK_NAME_KEY, userData.user.task_name)
                            intent.putExtra(TASK_HOUR_KEY, userData.user.hour)
                            intent.putExtra(TASK_MINUTE_KEY, userData.user.min)
                            intent.putExtra(TASK_DATE_KEY, userData.user.date)
                            intent.putExtra(TASK_NOTE_KEY, userData.user.note)
                            intent.putExtra(TASK_COMPLETED_KEY, userData.user.status)
                            intent.putExtra(TASK_UID_KEY, userData.user.key)
                            startActivity(intent)

                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        //TODO("Not yet implemented")
                    }
                })
    }

}
class tasks_viewholder(val adapter: GroupAdapter<GroupieViewHolder>,val user: Notes): Item<GroupieViewHolder>() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val task_name_tv = viewHolder.itemView.findViewById<TextView>(R.id.task_name_cv)
        val display_name = "${user.task_name} - ${user.date} at ${user.hour}:${user.min}"
        val mac_btn = viewHolder.itemView.findViewById<ImageButton>(R.id.mac_btn_task)
        val mai_btn = viewHolder.itemView.findViewById<ImageButton>(R.id.mai_btn_task)
        val delete_btn = viewHolder.itemView.findViewById<ImageButton>(R.id.del_btn_task)
        task_name_tv.text = display_name
        if (user.status == "COMPLETED"){
            task_name_tv.paintFlags = task_name_tv.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            mac_btn.visibility = View.GONE
            mai_btn.visibility = View.VISIBLE
        }

        mac_btn.setOnClickListener {

                task_name_tv.paintFlags = task_name_tv.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                database.child("Users").child(firebaseAuthCUser).child("Notes").child(user.key.toString()).child("status").setValue("COMPLETED")
                mac_btn.visibility = View.GONE
                mai_btn.visibility = View.VISIBLE
        }
        mac_btn.setOnLongClickListener {
            Toast.makeText(viewHolder.itemView.context,"Mark As Complete",Toast.LENGTH_SHORT).show()
            true
        }
        mai_btn.setOnClickListener {
            task_name_tv.paintFlags = 0
            database.child("Users").child(firebaseAuthCUser).child("Notes").child(user.key.toString()).child("status").setValue("INCOMPLETED")
            mac_btn.visibility = View.VISIBLE
            mai_btn.visibility = View.GONE
        }
        mai_btn.setOnLongClickListener {
            Toast.makeText(viewHolder.itemView.context,"Mark As InComplete",Toast.LENGTH_SHORT).show()
            true
        }
        delete_btn.setOnClickListener {
            Toast.makeText(viewHolder.itemView.context, "Task Deleted", Toast.LENGTH_SHORT).show()
            adapter.removeGroupAtAdapterPosition(position)
            database.child("Users").child(firebaseAuthCUser).child("Notes").child(user.key.toString()).removeValue()



            }
        delete_btn.setOnLongClickListener {
            Toast.makeText(viewHolder.itemView.context,"Delete Task",Toast.LENGTH_SHORT).show()
            true
        }
    }



    override fun getLayout() :Int{
        return R.layout.tasks_layout
    }



}
