package com.dscrecruit.todolist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.HandlerCompat.postDelayed
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codingninjassrm.letstodoapp.AddItem
import com.google.android.material.bottomnavigation.BottomNavigationMenu
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
                        Log.d("Data?",it.toString())
                        if (name_display != null) {
                            adapter.add(tasks_viewholder(name_display))

                        }
                    }
                    findViewById<RecyclerView>(R.id.all_tasks_recycler).adapter = adapter

//
//                    adapter.setOnItemClickListener { item, view ->
//                        val userData = item as tasks_viewholder
//                        val intent = Intent(view.context, TaskDetailsActivity::class.java)
//                        intent.putExtra(TASK_NAME_KEY, userData.user.name)
//                        intent.putExtra(TASK_HOUR_KEY, userData.user.hour)
//                        intent.putExtra(TASK_MINUTE_KEY, userData.user.minutes)
//                        intent.putExtra(TASK_DATE_KEY, userData.user.date)
//                        intent.putExtra(TASK_NOTE_KEY, userData.user.note)
//                        intent.putExtra(TASK_COMPLETED_KEY, userData.user.status)
//                        intent.putExtra(TASK_UID_KEY, userData.user.key)
//                        finish()
//                        startActivity(intent)
//////
//                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    //TODO("Not yet implemented")
                }
            })
    }

}
class tasks_viewholder(val user: Notes): Item<GroupieViewHolder>() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.task_name_cv).text = user.task_name
    }

    override fun getLayout() :Int{
        return R.layout.tasks_layout
    }

}