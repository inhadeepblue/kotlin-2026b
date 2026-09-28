package com.appweek05

import android.util.Log
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val editTextName = findViewById<EditText>(R.id.editTextName)
        val buttonGreet = findViewById<Button>(R.id.buttonGreet)
        val textViewGreeting = findViewById<TextView>(R.id.textViewGreeting)

        buttonGreet.setOnClickListener {
            val name = editTextName.text.toString().trim()

            var greeting: String = ""
            if(name.isNotEmpty()){
                greeting = "안녕, ${name}님~"
            }else{
                greeting = "너의 이름은?"
            }
            textViewGreeting.text = greeting
            textViewGreeting.visibility = View.VISIBLE
            Log.d("KotlinWeek05App", greeting)
        }
    }
}