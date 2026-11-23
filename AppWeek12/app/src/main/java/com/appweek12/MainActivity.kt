package com.appweek12

import android.graphics.Color
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.appweek12.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: CounterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {
        lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.count.collect{
                        count -> binding.textViewCount.text = count.toString()

                    when{
                        count > 0 -> binding.textViewCount.setTextColor(Color.GREEN)
                        count < 0 -> binding.textViewCount.setTextColor(Color.RED)
                        else -> binding.textViewCount.setTextColor(Color.BLACK)
                    }
                }
            }
        }
    }

    private fun setupListeners() {
        binding.buttonPlus.setOnClickListener {
            viewModel.increment()
        }
        binding.buttonMinus.setOnClickListener {
            viewModel.decrement()
        }
        binding.buttonReset.setOnClickListener {
            viewModel.reset()
        }
        binding.buttonPlus10.setOnClickListener {
            viewModel.incrementBy10()
        }
    }
}