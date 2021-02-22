package com.devtk.todolist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val animationpic = findViewById<ImageView>(R.id.imageView)
        val animationText = findViewById<TextView>(R.id.splash_text)
        animationpic.startAnimation(AnimationUtils.loadAnimation(this, R.anim.splash_in))
        animationText.startAnimation(AnimationUtils.loadAnimation(this,R.anim.splash_in))
        Handler().postDelayed({
            animationpic.startAnimation(AnimationUtils.loadAnimation(this,R.anim.splash_out))
            animationText.startAnimation(AnimationUtils.loadAnimation(this,R.anim.splash_out))
            Handler().postDelayed({
                animationpic.visibility = View.GONE
                animationText.visibility = View.GONE
                startActivity(Intent(this,LoginActivity::class.java))
            },500)
        },1500)
    }
}