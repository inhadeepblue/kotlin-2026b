package com.appweek07

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

enum class AppMode(val displayName: String) {
    STUDENT_LIST("Student List"),
    SHOPPING_CART("Shopping Cart"),
}
