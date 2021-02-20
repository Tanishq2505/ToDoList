package com.dscrecruit.todolist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import com.codingninjassrm.letstodoapp.AddItem
import com.google.android.material.bottomnavigation.BottomNavigationMenu
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

var database = FirebaseDatabase.getInstance().reference
var small_database = FirebaseDatabase.getInstance().reference
val firebaseAuthCUser = FirebaseAuth.getInstance().currentUser!!.uid
private var onBackPressedToexit = false
class HomepageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)


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
}