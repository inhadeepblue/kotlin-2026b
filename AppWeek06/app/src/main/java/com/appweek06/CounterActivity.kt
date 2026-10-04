package com.appweek06

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class CounterActivity : AppCompatActivity() {
    private var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_counter)

        val textViewCount = findViewById<TextView>(R.id.textViewCount)
        val buttonMinus = findViewById<Button>(R.id.buttonMinus)
        val buttonPlus = findViewById<Button>(R.id.buttonPlus)
        val buttonReset = findViewById<Button>(R.id.buttonReset)

        buttonMinus.setOnClickListener {
            count--
            textViewCount.text = count.toString()
            Log.d("KotlinWeek06App", "${count}")
        }
        buttonPlus.setOnClickListener {
            count++
            textViewCount.text = count.toString()
            Log.d("KotlinWeek06App", "${count}")
        }
        buttonReset.setOnClickListener {
            count = 0
            textViewCount.text = count.toString()
            Log.d("KotlinWeek06App", "${count}")
        }
    }
}