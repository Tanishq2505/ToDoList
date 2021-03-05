package com.devtk.todolist


import android.content.Intent
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.SimpleCallback
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator


var database = FirebaseDatabase.getInstance().reference
var small_database = FirebaseDatabase.getInstance().reference
val firebaseAuthCUser = FirebaseAuth.getInstance().currentUser!!.uid
private var onBackPressedToexit = false
class HomepageActivity : AppCompatActivity() {
    val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)
        findViewById<RecyclerView>(R.id.all_tasks_recycler).layoutManager = LinearLayoutManager(this)
        val swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.refreshRecycler)
        val sharedPreferences = getSharedPreferences("sharedPrefs", AppCompatActivity.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val mode = AppCompatDelegate.getDefaultNightMode()
        val isDarkModeOn = sharedPreferences.getBoolean("isDarkModeOn", false)
        onResume().apply {
            if (mode == AppCompatDelegate.MODE_NIGHT_YES) {
                editor.putBoolean("isDarkModeOn", true)
                editor.apply()
            } else {
                editor.putBoolean("isDarkModeOn", false)
                editor.apply()
            }
        }
        if (isDarkModeOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        fetchNotes()
        val b_navbar = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        b_navbar.setOnNavigationItemSelectedListener {
            when (it.itemId) {
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
        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(findViewById(R.id.all_tasks_recycler))
        swipeRefreshLayout.setOnRefreshListener {
            adapter.notifyDataSetChanged()
            swipeRefreshLayout.isRefreshing = false
        }

    }

    val simpleCallback = object : SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }
        
        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val task_name_tv = viewHolder.itemView.findViewById<TextView>(R.id.task_name_cv)
            val noteName = task_name_tv.text
            if(direction == ItemTouchHelper.LEFT){
                val position = viewHolder.adapterPosition
                adapter.removeGroupAtAdapterPosition(position)

                database.child("Users").child(firebaseAuthCUser).child("Notes").addListenerForSingleValueEvent(
                    object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            snapshot.children.forEach {
                                val values = it.getValue(Notes::class.java)
                                if (values != null) {
                                    if (values.task_name == noteName.toString()) {
                                        val key = it.key
                                        val deletedData = it.getValue(Notes::class.java)
                                        database.child("Users").child(firebaseAuthCUser)
                                            .child("Notes").child(
                                            key.toString()
                                        ).removeValue()
                                        Snackbar.make(
                                            findViewById(R.id.all_tasks_recycler),
                                            noteName,
                                            Snackbar.LENGTH_LONG
                                        ).setAction("Undo", View.OnClickListener {
                                            database.child("Users").child(firebaseAuthCUser)
                                                .child("Notes").child(
                                                    deletedData?.key.toString()
                                                ).setValue(deletedData)
                                            if (deletedData != null) {
                                                adapter.add(
                                                    position, tasks_viewholder(
                                                        adapter,
                                                        deletedData
                                                    )
                                                )
                                            }
                                        }).show()
                                    }
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            //TODO("Not yet implemented")
                        }

                    })
                
            }else if (direction == ItemTouchHelper.RIGHT){
                database.child("Users").child(firebaseAuthCUser).child("Notes").addListenerForSingleValueEvent(
                    object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            snapshot.children.forEach {
                                val values = it.getValue(Notes::class.java)
                                if (values != null) {
                                    if (values.task_name == noteName.toString()) {
                                        val key = it.key
                                        if (values.status == "INCOMPLETED") {
                                            database.child("Users").child(firebaseAuthCUser)
                                                .child("Notes").child(
                                                key.toString()
                                            ).child("status").setValue("COMPLETED")
                                            adapter.notifyDataSetChanged()
                                            task_name_tv.paintFlags =
                                                task_name_tv.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                                        } else if (values.status == "COMPLETED") {
                                            database.child("Users").child(firebaseAuthCUser)
                                                .child("Notes").child(
                                                key.toString()
                                            ).child("status").setValue("INCOMPLETED")
                                            adapter.notifyDataSetChanged()
                                            task_name_tv.paintFlags = 0
                                        }
                                    }
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            //TODO("Not yet implemented")
                        }

                    })
            }
        }

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            RecyclerViewSwipeDecorator.Builder(
                c,
                recyclerView,
                viewHolder,
                dX,
                dY,
                actionState,
                isCurrentlyActive
            )
                    .addSwipeLeftBackgroundColor(
                        ContextCompat.getColor(
                            this@HomepageActivity,
                            R.color.red_light
                        )
                    )
                    .addSwipeLeftActionIcon(R.drawable.ic_delete)
                    .addSwipeLeftLabel("Delete")
                    .addSwipeRightBackgroundColor(
                        ContextCompat.getColor(
                            this@HomepageActivity,
                            R.color.green
                        )
                    )
                    .addSwipeRightActionIcon(R.drawable.ic_baseline_done_24)
                    .addSwipeRightLabel("Mark As Read")
                    .create()
                    .decorate()
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
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


                    snapshot.children.forEach {
                        val name_display = it.getValue(Notes::class.java)
                        if (name_display != null) {
                            adapter.add(tasks_viewholder(adapter, name_display))

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
class tasks_viewholder(val adapter: GroupAdapter<GroupieViewHolder>, val user: Notes): Item<GroupieViewHolder>() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val task_name_tv = viewHolder.itemView.findViewById<TextView>(R.id.task_name_cv)
        task_name_tv.text = user.task_name
        if (user.status == "COMPLETED"){
            task_name_tv.paintFlags = task_name_tv.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        }
    }



    override fun getLayout() :Int{
        return R.layout.tasks_layout
    }



}