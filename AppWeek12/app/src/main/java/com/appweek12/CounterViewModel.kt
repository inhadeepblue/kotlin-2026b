package com.appweek12

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.ViewModel

class CounterViewModel : ViewModel(){
    private val _count = MutableStateFlow(0)
    val count: StateFlow<Int> = _count.asStateFlow()

    fun increment(){
        _count.value = (_count.value) + 1
    }
    fun decrement(){
        _count.value -= 1
    }
    fun reset(){
        _count.value = 0
    }
    fun incrementBy10(){
        _count.value += 10
    }
}