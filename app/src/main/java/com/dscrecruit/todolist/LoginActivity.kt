package com.dscrecruit.todolist

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.InputType
import android.util.Patterns
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
private var onBackPressedToexit = false
class LoginActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val tv_password = findViewById<EditText>(R.id.tv_password)
        val signup_tv = findViewById<TextView>(R.id.signup)
        val login_btn = findViewById<Button>(R.id.btn_log_in)
        signup_tv.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
            finish()
        }
        val forgotpass_tv = findViewById<TextView>(R.id.forgotpassword)
        forgotpass_tv.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Forgot Password")
            val view=layoutInflater.inflate(R.layout.resetpass_dialogue, null)
            val username=view.findViewById<EditText>(R.id.et_username)
            builder.setView(view)
            builder.setPositiveButton("Reset", DialogInterface.OnClickListener { _, _ ->
                resetPassword(username)
            })
            builder.setNegativeButton("Close", DialogInterface.OnClickListener { _, _ ->
                Toast.makeText(this,"No Email Sent",Toast.LENGTH_SHORT).show()
            })
            builder.show()
        }
        firebaseAuth = FirebaseAuth.getInstance()
        login_btn.setOnClickListener {
            doLogin()
        }
        val loginCheckBox = findViewById<CheckBox>(R.id.loginCheckBox)
        loginCheckBox.setOnClickListener {
            if(loginCheckBox.isChecked){
                tv_password.inputType = 1
            }else{
                tv_password.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
        }

    }
    private fun doLogin(){
        val tv_username = findViewById<EditText>(R.id.tv_username)
        val tv_password = findViewById<EditText>(R.id.tv_password)
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
        firebaseAuth.signInWithEmailAndPassword(tv_username.text.toString(), tv_password.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    val user = firebaseAuth.currentUser
                    updateUI(user)
                } else {

                    updateUI(null)
                    // ...
                }

                // ...
            }
    }
    private fun updateUI(currentUser: FirebaseUser?) {
        if(currentUser!=null){
            if(currentUser.isEmailVerified){

                startActivity(Intent(this, HomepageActivity::class.java))

                finish()
            }else{
                Toast.makeText(baseContext, "Please verify your email address",
                    Toast.LENGTH_SHORT).show()
            }

        }

        else{

            Toast.makeText(baseContext, "Authentication failed.",
                Toast.LENGTH_SHORT).show()
        }

    }
    private fun resetPassword(username:EditText){
        if (username.text.toString().isEmpty()) {
            Toast.makeText(this,"Text Field Is Empty!",Toast.LENGTH_SHORT).show()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(username.text.toString()).matches()) {

            return
        }
        firebaseAuth.sendPasswordResetEmail(username.text.toString())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Email sent", Toast.LENGTH_SHORT).show()

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
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = firebaseAuth.currentUser

        updateUI(currentUser)

    }
}