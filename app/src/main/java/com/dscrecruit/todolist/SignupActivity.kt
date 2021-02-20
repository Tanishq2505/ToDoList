package com.dscrecruit.todolist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import org.w3c.dom.Text

class SignupActivity : AppCompatActivity() {
    var database = FirebaseDatabase.getInstance().reference
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        val checkbox_signup = findViewById<CheckBox>(R.id.checkBox_signup)
        val tv_password = findViewById<EditText>(R.id.tv_password_signup)
        val sign_in_tv = findViewById<TextView>(R.id.textView8)
        val sign_up_btn = findViewById<Button>(R.id.btn_sign_up)
        checkbox_signup.setOnClickListener {
            if(checkbox_signup.isChecked){
                tv_password.inputType = 1
            }else{
                tv_password.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
        }
        sign_in_tv.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
        }
        firebaseAuth = FirebaseAuth.getInstance()
        sign_up_btn.setOnClickListener {
            doSignup()
        }

    }
    private fun doSignup(){

        firebaseAuth = FirebaseAuth.getInstance()
        val tv_username = findViewById<EditText>(R.id.tv_email_signup)
        val tv_password = findViewById<EditText>(R.id.tv_password_signup)
        val tv_name = findViewById<EditText>(R.id.name_signup)
        if (tv_username.text.toString().isEmpty()) {
            tv_username.error = "Please enter email"
            tv_username.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(tv_username.text.toString()).matches()) {
            tv_username.error = "Please enter valid email"
            tv_username.requestFocus()
            return
        }

        if (tv_password.text.toString().isEmpty()) {
            tv_password.error = "Please enter password"
            tv_password.requestFocus()
            return
        }
        firebaseAuth.createUserWithEmailAndPassword(
            tv_username.text.toString(),
            tv_password.text.toString()
        )
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    val user = firebaseAuth.currentUser
                    user!!.sendEmailVerification()
                        .addOnCompleteListener { task ->
                            val currentUID = FirebaseAuth.getInstance().currentUser!!.uid
                            if (task.isSuccessful) {
                                database.child("Users").child(currentUID).child("UserData").setValue(userData(tv_name.text.toString(),tv_username.text.toString()))
                                startActivity(Intent(this, LoginActivity::class.java))
                                finish()
                            }

                        }

                } else {
                    Toast.makeText(
                        baseContext, "Sign Up failed. Try again after some time.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

    }

    override fun onBackPressed() {
        finish()
        startActivity(Intent(this,LoginActivity::class.java))
    }
}
