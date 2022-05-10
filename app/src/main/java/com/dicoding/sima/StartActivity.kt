package com.dicoding.sima

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class StartActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        val button: Button = findViewById(R.id.button)
        val button1: Button = findViewById(R.id.button1)
        val button2: Button = findViewById(R.id.button2)
        val button3: Button = findViewById(R.id.button3)

        button.setOnClickListener(this)
        button1.setOnClickListener(this)
        button2.setOnClickListener(this)
        button3.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.button -> {
                val moveIntent = Intent(this@StartActivity, MainActivity::class.java)
                startActivity(moveIntent)
            }
            R.id.button1 -> {
                val moveIntent = Intent(this@StartActivity, FirstAct::class.java)
                startActivity(moveIntent)
            }
            R.id.button2 -> {
                val moveIntent = Intent(this@StartActivity, SecondAct::class.java)
                startActivity(moveIntent)
            }
            R.id.button3 -> {
                val moveIntent = Intent(this@StartActivity, InfoActivity::class.java)
                startActivity(moveIntent)
            }
        }
    }
}
