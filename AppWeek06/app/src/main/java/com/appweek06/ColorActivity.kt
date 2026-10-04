package com.appweek06

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ColorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_color)

        val layoutMain = findViewById<LinearLayout>(R.id.layoutMain)
        val textViewCurrentColor = findViewById<TextView>(R.id.textViewCurrentColor)
        val buttonRed = findViewById<Button>(R.id.buttonRed)
        val buttonGreen = findViewById<Button>(R.id.buttonGreen)
        val buttonBlue = findViewById<Button>(R.id.buttonBlue)
        val buttonReset = findViewById<Button>(R.id.buttonReset)

        // 공통 함수: 배경 변경 + UI 업데이트 + 로그
        fun changeBackground(color: Int, colorName: String) {
            layoutMain.setBackgroundColor(color)
            textViewCurrentColor.text = "현재 색: $colorName"
            Log.d("KotlinWeek06App", "현재 색 $colorName")
        }

        buttonRed.setOnClickListener {
            changeBackground(Color.RED, "Red")
        }

        buttonGreen.setOnClickListener {
            changeBackground(Color.GREEN, "Green")
        }

        buttonBlue.setOnClickListener {
            changeBackground(Color.BLUE, "Blue")
        }

        buttonReset.setOnClickListener {
            changeBackground(Color.WHITE, "White")
        }
    }
}