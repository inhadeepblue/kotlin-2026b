package com.appweek05

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val etDan = findViewById<EditText>(R.id.etDan)
        val btnCalculate = findViewById<Button>(R.id.btnCalculate)
        val tvResult = findViewById<TextView>(R.id.tvResult)

        btnCalculate.setOnClickListener{
            val inputText = etDan.text.toString()

            if(inputText.isEmpty())
            {
                Toast.makeText(this, "숫자를 입력해주세요!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val dan = inputText.toInt()
            val result = StringBuilder()
            result.append("==== $dan 단 ====\n\n")

            for(i in 1..9){
                result.append("$dan x $i = ${dan * i}\n")
            }

            tvResult.text = result.toString()
        }
    }
}