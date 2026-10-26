package com.appweek07

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.appweek07.AppMode
import com.appweek07.CartItem
import com.appweek07.R
import com.appweek07.Student
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    // UI Components
    private lateinit var radioGroup: RadioGroup
    private lateinit var radioStudentList: RadioButton
    private lateinit var radioShoppingCart: RadioButton

    private lateinit var listView: ListView
    private lateinit var editTextInput: EditText
    private lateinit var buttonAdd: Button
    private lateinit var buttonClear: Button
    private lateinit var textViewInfo: TextView

    // Shopping Cart specific UI
    private lateinit var layoutCartControls: LinearLayout
    private lateinit var editTextPrice: EditText
    private lateinit var editTextQuantity: EditText

    // Data Storage
    private lateinit var studentList: ArrayList<Student>
    private lateinit var cartItemList: ArrayList<CartItem>

    // Adapters
    private lateinit var studentAdapter: ArrayAdapter<Student>
    private lateinit var cartAdapter: ArrayAdapter<CartItem>

    // Current Mode
    private var currentMode = AppMode.STUDENT_LIST

    companion object {
        private const val TAG = "KotlinWeek07App"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(TAG, "onCreate: AppWeek07 started")

        initializeData()
        setupViews()
        setupAdapters()
        setupListeners()

        // Set initial mode
        setMode(AppMode.STUDENT_LIST)
        addInitialData()
    }

    private fun initializeData() {
        studentList = ArrayList()
        cartItemList = ArrayList()

        Log.d(TAG, "Data structures initialized")
    }

    private fun setupViews() {
        // Mode selection
        radioGroup = findViewById(R.id.radioGroup)
        radioStudentList = findViewById(R.id.radioStudentList)
        radioShoppingCart = findViewById(R.id.radioShoppingCart)

        // Common UI
        listView = findViewById(R.id.listView)
        editTextInput = findViewById(R.id.editTextInput)
        buttonAdd = findViewById(R.id.buttonAdd)
        buttonClear = findViewById(R.id.buttonClear)
        textViewInfo = findViewById(R.id.textViewInfo)

        // Shopping Cart specific
        layoutCartControls = findViewById(R.id.layoutCartControls)
        editTextPrice = findViewById(R.id.editTextPrice)
        editTextQuantity = findViewById(R.id.editTextQuantity)


        Log.d(TAG, "Views initialized")
    }

    private fun setupAdapters() {
        studentAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, studentList)
        cartAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, cartItemList)

        Log.d(TAG, "Adapters setup completed")
    }

    private fun setupListeners() {
        // Mode selection listeners
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioStudentList -> setMode(AppMode.STUDENT_LIST)
                R.id.radioShoppingCart -> setMode(AppMode.SHOPPING_CART)
            }
        }

        // Common button listeners
        buttonAdd.setOnClickListener { addItem() }
        buttonClear.setOnClickListener { clearAll() }

        // ListView listeners
        listView.setOnItemClickListener { _, _, position, _ ->
            handleItemClick(position)
        }

        listView.setOnItemLongClickListener { _, _, position, _ ->
            handleItemLongClick(position)
            true
        }

        Log.d(TAG, "Event listeners setup completed")
    }

    private fun setMode(mode: AppMode) {
        currentMode = mode

        // Update UI visibility
        when (mode) {
            AppMode.STUDENT_LIST -> {
                editTextInput.hint = "Enter student name"
                buttonAdd.text = "Add Student"
                layoutCartControls.visibility = View.GONE

                listView.adapter = studentAdapter
            }
            AppMode.SHOPPING_CART -> {
                editTextInput.hint = "Enter item name"
                buttonAdd.text = "Add Item"
                layoutCartControls.visibility = View.VISIBLE

                listView.adapter = cartAdapter
                updateCartInfo()
            }
        }

        updateInfoDisplay()
        Log.d(TAG, "Mode changed to: ${mode.displayName}")
    }

    private fun addItem() {
        val input = editTextInput.text.toString().trim()

        if (input.isEmpty()) {
            showToast("Please enter a ${currentMode.displayName.lowercase().dropLast(5)}")
            return
        }

        when (currentMode) {
            AppMode.STUDENT_LIST -> addStudent(input)
            AppMode.SHOPPING_CART -> addCartItem(input)

        }

        editTextInput.text.clear()
        clearAdditionalFields()
        updateInfoDisplay()
    }

    private fun addStudent(name: String) {
        if (studentList.any { it.name == name }) {
            showToast("Student '$name' already exists")
            return
        }

        val student = Student(name)
        studentList.add(student)
        studentAdapter.notifyDataSetChanged()

        showToast("Added student: $name")
        Log.d(TAG, "Added student: $name (Total: ${studentList.size})")
    }

    private fun addCartItem(name: String) {
        val priceText = editTextPrice.text.toString()
        val quantityText = editTextQuantity.text.toString()

        if (priceText.isEmpty()) {
            showToast("Please enter price")
            return
        }

        val price = priceText.toDoubleOrNull() ?: run {
            showToast("Invalid price format")
            return
        }

        val quantity = if (quantityText.isEmpty()) 1 else {
            quantityText.toIntOrNull() ?: run {
                showToast("Invalid quantity format")
                return
            }
        }

        if (price < 0 || quantity <= 0) {
            showToast("Price and quantity must be positive")
            return
        }

        val existingItem = cartItemList.find { it.name == name }
        if (existingItem != null) {
            existingItem.quantity += quantity
            cartAdapter.notifyDataSetChanged()
            showToast("Updated quantity for: $name")
        } else {
            val cartItem = CartItem(name, quantity, price)
            cartItemList.add(cartItem)
            cartAdapter.notifyDataSetChanged()
            showToast("Added to cart: $name")
        }

        updateCartInfo()
        Log.d(TAG, "Added cart item: $name x$quantity @ $$price")
    }


    private fun clearAdditionalFields() {
        editTextPrice.text.clear()
        editTextQuantity.text.clear()


    }

    private fun clearAll() {
        when (currentMode) {
            AppMode.STUDENT_LIST -> {
                val count = studentList.size
                studentList.clear()
                studentAdapter.notifyDataSetChanged()
                showToast("Cleared all $count students")
            }
            AppMode.SHOPPING_CART -> {
                val count = cartItemList.size
                cartItemList.clear()
                cartAdapter.notifyDataSetChanged()
                showToast("Cleared all $count items from cart")
                updateCartInfo()
            }
        }

        updateInfoDisplay()
        Log.d(TAG, "Cleared all items in mode: ${currentMode.displayName}")
    }

    private fun handleItemClick(position: Int) {
        when (currentMode) {
            AppMode.STUDENT_LIST -> {
                val student = studentList[position]
                showToast("Selected: ${student.name}")
            }
            AppMode.SHOPPING_CART -> {
                val item = cartItemList[position]
                showItemDetailsDialog(item)
            }
        }
    }

    private fun handleItemLongClick(position: Int) {
        AlertDialog.Builder(this)
            .setTitle("Delete Item")
            .setMessage("Are you sure you want to delete this item?")
            .setPositiveButton("Delete") { _, _ ->
                removeItem(position)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun removeItem(position: Int) {
        when (currentMode) {
            AppMode.STUDENT_LIST -> {
                if (position < studentList.size) {
                    val student = studentList.removeAt(position)
                    studentAdapter.notifyDataSetChanged()
                    showToast("Removed: ${student.name}")
                }
            }
            AppMode.SHOPPING_CART -> {
                if (position < cartItemList.size) {
                    val item = cartItemList.removeAt(position)
                    cartAdapter.notifyDataSetChanged()
                    showToast("Removed: ${item.name}")
                    updateCartInfo()
                }
            }
        }
        updateInfoDisplay()
    }

    private fun showItemDetailsDialog(item: CartItem) {
        val message = """
            Item: ${item.name}
            Quantity: ${item.quantity}
            Unit Price: $${String.format("%.2f", item.price)}
            Total: $${String.format("%.2f", item.getTotalPrice())}
            Added: ${SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.getDefault()).format(item.addedDate)}
        """.trimIndent()

        AlertDialog.Builder(this)
            .setTitle("Item Details")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun updateInfoDisplay() {
        when (currentMode) {
            AppMode.STUDENT_LIST -> {
                textViewInfo.text = "Total Students: ${studentList.size}"
            }
            AppMode.SHOPPING_CART -> updateCartInfo()
        }
    }

    private fun updateCartInfo() {
        val totalItems = cartItemList.sumOf { it.quantity }
        val totalValue = cartItemList.sumOf { it.getTotalPrice() }
        textViewInfo.text = "Items: $totalItems | Total: $${String.format("%.2f", totalValue)}"
    }

    private fun addInitialData() {
        // Add initial students
        studentList.addAll(listOf(
            Student("KIM"),
            Student("LEE"),
            Student("PARK")
        ))

        // Add initial cart items
        cartItemList.addAll(listOf(
            CartItem("Apple", 3, 2.0),
            CartItem("Banana", 2, 1.0),
            CartItem("Milk", 1, 3.0)
        ))


        // Notify all adapters
        studentAdapter.notifyDataSetChanged()
        cartAdapter.notifyDataSetChanged()

        Log.d(TAG, "Initial data added to all modes")
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: Current mode: ${currentMode.displayName}")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: Saving state in mode: ${currentMode.displayName}")
    }
}