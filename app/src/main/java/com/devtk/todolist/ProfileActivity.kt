package com.devtk.todolist

import android.content.Intent
import android.media.Image
import android.opengl.Visibility
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class ProfileActivity : AppCompatActivity() {
private lateinit var original_name: String
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        val nm_btn = findViewById<Button>(R.id.nm_btn)
        val name_et = findViewById<EditText>(R.id.name_et)
        val edit_btn_profile = findViewById<ImageButton>(R.id.edit_btn_profile)
        val save_btn_profile = findViewById<ImageButton>(R.id.save_btn_profile)
        val email_et = findViewById<EditText>(R.id.email_et)
        val logout_btn = findViewById<Button>(R.id.logout_btn)

        name_et.isEnabled = false
        email_et.isEnabled = false


        val sharedPreferences = getSharedPreferences("sharedPrefs", AppCompatActivity.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val isDarkModeOn = sharedPreferences.getBoolean("isDarkModeOn", false)

        if (isDarkModeOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            nm_btn.text = "Day Mode"
        }
        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            nm_btn.setText("Night Mode")
        }
        nm_btn.setOnClickListener {
            if (isDarkModeOn){
                nm_btn.text = "Night Mode"
                editor.putBoolean("isDarkModeOn", false)
                editor.apply()
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }else{

                nm_btn.text = "Day Mode"
                editor.putBoolean("isDarkModeOn", true)
                editor.apply()
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
        }
        edit_btn_profile.setOnClickListener{
            save_btn_profile.visibility = View.VISIBLE
            name_et.isEnabled = true
            edit_btn_profile.visibility = View.GONE
            original_name = name_et.text.toString()
        }
        save_btn_profile.setOnClickListener {
            var updatedName = name_et.text
            if (updatedName.equals(original_name)){
                Toast.makeText(this,"Nothing Updated!",Toast.LENGTH_SHORT).show()
                name_et.isEnabled = false
                save_btn_profile.visibility = View.GONE
                edit_btn_profile.visibility = View.VISIBLE
            } else if(updatedName.equals("")){
                Toast.makeText(this,"Nothing Updated!",Toast.LENGTH_SHORT).show()
                name_et.isEnabled = false
                name_et.setText(original_name)
                save_btn_profile.visibility = View.GONE
                edit_btn_profile.visibility = View.VISIBLE
            } else{
                updatedName = name_et.text
                updateName(updatedName.toString())
                name_et.isEnabled = false
                save_btn_profile.visibility = View.GONE
                edit_btn_profile.visibility = View.VISIBLE
                Toast.makeText(this,"Name Updated",Toast.LENGTH_SHORT).show()
            }
        }
        logout_btn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(this,"User Signed Out",Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
        }

        database.child("Users").child(firebaseAuthCUser).child("UserData").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val userName = snapshot.child("name").value
                val userEmail = snapshot.child("email").value
                name_et.setText(userName.toString())
                email_et.setText(userEmail.toString())
            }

            override fun onCancelled(error: DatabaseError) {
                //TODO("Not yet implemented")
            }

        })
    }
    private fun updateName(name:String){
        database.child("Users").child(firebaseAuthCUser).child("UserData").child("name").setValue(name)
    }
    companion object{
        val KEY_DARK_MODE = "KEY_DARK_MODE"
    }


}