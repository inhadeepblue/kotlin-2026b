package com.appweek09

import android.graphics.Color
import java.util.*

data class Student(
    val name: String,
    val id: String = UUID.randomUUID().toString(),
    val addedDate: Date = Date()
)

data class CartItem(
    val name: String,
    var quantity: Int = 1,
    val price: Double,
    val id: String = UUID.randomUUID().toString(),
    val addedDate: Date = Date()
) {
    fun getTotalPrice(): Double = quantity * price

    override fun toString(): String {
        return "$name (x$quantity) - $%.2f".format(getTotalPrice())
    }
}

data class Task(
    val title: String,
    val description: String = "",
    var isCompleted: Boolean = false,
    val priority: TaskPriority,
    val dueDate: Date? = null,
    val id: String = UUID.randomUUID().toString(),
    val createdDate: Date = Date()
) {
    override fun toString(): String {
        val status = if (isCompleted) "✓" else "○"
        val priorityIcon = when (priority) {
            TaskPriority.HIGH -> "!!!"
            TaskPriority.MEDIUM -> "!!"
            TaskPriority.LOW -> "!"
        }
        return "$status $priorityIcon $title"
    }
}

enum class TaskPriority(val displayName: String, val color: Int){
    HIGH("High", Color.RED),
    MEDIUM("Medium", Color.BLUE),
    LOW("Low", Color.GREEN)
}

enum class AppMode(val displayName: String) {
    STUDENT_LIST("Student List"),
    SHOPPING_CART("Shopping Cart"),
    TASK_MANAGER("Task Manager"),
}