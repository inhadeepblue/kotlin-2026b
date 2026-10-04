package com.appweek06

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class GreetingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_greeting)

        val editTextName = findViewById<EditText>(R.id.editTextName)
        val buttonGreet = findViewById<Button>(R.id.buttonGreet)
        val textViewGreeting = findViewById<TextView>(R.id.textViewGreeting)

        buttonGreet.setOnClickListener {
            val name = editTextName.text.toString().trim()

            var greeting: String = ""
            if(name.isNotEmpty()){
                greeting = "안녕하세요, ${name}님!"
            }else{
                greeting = "이름을 입력해주세요"
            }
            textViewGreeting.text = greeting
            textViewGreeting.visibility = View.VISIBLE
            Log.d("KotlinWeek06App", greeting)
        }
    }
}