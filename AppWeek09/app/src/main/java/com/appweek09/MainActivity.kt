package com.appweek09

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    // UI Components
    private lateinit var radioGroup: RadioGroup
    private lateinit var radioStudentList: RadioButton
    private lateinit var radioShoppingCart: RadioButton
    private lateinit var radioTaskManager: RadioButton

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
    private lateinit var taskList: ArrayList<Task>

    // Adapters
    private lateinit var studentAdapter: ArrayAdapter<Student>
    private lateinit var cartAdapter: ArrayAdapter<CartItem>
    private lateinit var taskAdapter: ArrayAdapter<Task>

    // Task Manager specific UI
    private lateinit var layoutTaskControls: LinearLayout
    private lateinit var spinnerPriority: Spinner
    private lateinit var editTextDescription: EditText

    // Current Mode
    private var currentMode = AppMode.STUDENT_LIST

    companion object {
        private const val TAG = "KotlinWeek09App"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(TAG, "onCreate: AppWeek09 started")

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
        taskList = ArrayList()

        Log.d(TAG, "Data structures initialized")
    }

    private fun setupViews() {
        // Mode selection
        radioGroup = findViewById(R.id.radioGroup)
        radioStudentList = findViewById(R.id.radioStudentList)
        radioShoppingCart = findViewById(R.id.radioShoppingCart)
        radioTaskManager = findViewById(R.id.radioTaskManager)

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

        // Task Manager specific
        layoutTaskControls = findViewById(R.id.layoutTaskControls)
        spinnerPriority = findViewById(R.id.spinnerPriority)
        editTextDescription = findViewById(R.id.editTextDescription)

        setupPrioritySpinner()

        Log.d(TAG, "Views initialized")
    }

    private fun setupAdapters() {
        studentAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, studentList)
        cartAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, cartItemList)
        taskAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, taskList)

        Log.d(TAG, "Adapters setup completed")
    }

    private fun setupPrioritySpinner() {
        val priorities = TaskPriority.values().map { it.displayName }
        val priorityAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, priorities)
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPriority.adapter = priorityAdapter
    }

    private fun setupListeners() {
        // Mode selection listeners
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioStudentList -> setMode(AppMode.STUDENT_LIST)
                R.id.radioShoppingCart -> setMode(AppMode.SHOPPING_CART)
                R.id.radioTaskManager -> setMode(AppMode.TASK_MANAGER)
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
                layoutTaskControls.visibility = View.GONE
                listView.adapter = studentAdapter
            }
            AppMode.SHOPPING_CART -> {
                editTextInput.hint = "Enter item name"
                buttonAdd.text = "Add Item"
                layoutCartControls.visibility = View.VISIBLE
                layoutTaskControls.visibility = View.GONE
                listView.adapter = cartAdapter
                updateCartInfo()
            }
            AppMode.TASK_MANAGER -> {
                editTextInput.hint = "Enter task title"
                buttonAdd.text = "Add Task"
                layoutTaskControls.visibility = View.VISIBLE
                layoutCartControls.visibility = View.GONE
                listView.adapter = taskAdapter
                updateTaskInfo()
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
            AppMode.TASK_MANAGER -> addTask(input)
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
        editTextDescription.text.clear()
        spinnerPriority.setSelection(0)
    }

    private fun addTask(title: String) {
        val description = editTextDescription.text.toString().trim()
        val priorityIndex = spinnerPriority.selectedItemPosition
        val priority = TaskPriority.values()[priorityIndex]

        val task = Task(title, description, false, priority)
        taskList.add(task)
        taskAdapter.notifyDataSetChanged()

        updateTaskInfo()
        showToast("Added task: $title")
        Log.d(TAG, "Added task: $title with priority: ${priority.displayName}")
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
            AppMode.TASK_MANAGER -> {
                val count = taskList.size
                taskList.clear()
                taskAdapter.notifyDataSetChanged()
                showToast("Cleared all $count tasks")
                updateTaskInfo()
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
            AppMode.TASK_MANAGER -> {
                val task = taskList[position]
                toggleTaskCompletion(task, position)
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
            AppMode.TASK_MANAGER -> {
                if (position < taskList.size) {
                    val task = taskList.removeAt(position)
                    taskAdapter.notifyDataSetChanged()
                    showToast("Removed: ${task.title}")
                    updateTaskInfo()
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

    private fun toggleTaskCompletion(task: Task, position: Int) {
        task.isCompleted = !task.isCompleted
        taskAdapter.notifyDataSetChanged()
        updateTaskInfo()

        val status = if (task.isCompleted) "completed" else "pending"
        showToast("Task marked as $status")
        Log.d(TAG, "Task '${task.title}' marked as $status")
    }

    private fun updateInfoDisplay() {
        when (currentMode) {
            AppMode.STUDENT_LIST -> {
                textViewInfo.text = "Total Students: ${studentList.size}"
            }
            AppMode.SHOPPING_CART -> updateCartInfo()
            AppMode.TASK_MANAGER -> updateTaskInfo()
        }
    }

    private fun updateCartInfo() {
        val totalItems = cartItemList.sumOf { it.quantity }
        val totalValue = cartItemList.sumOf { it.getTotalPrice() }
        textViewInfo.text = "Items: $totalItems | Total: $${String.format("%.2f", totalValue)}"
    }

    private fun updateTaskInfo() {
        val completed = taskList.count { it.isCompleted }
        val pending = taskList.size - completed
        val highPriority = taskList.count { it.priority == TaskPriority.HIGH && !it.isCompleted }

        textViewInfo.text = "Tasks: $pending pending, $completed completed | High Priority: $highPriority"
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
        // Add initial tasks
        taskList.addAll(listOf(
            Task("Complete Assignment", "Mobile Programming", false, TaskPriority.HIGH),
            Task("Shopping", "Visit Mart", false, TaskPriority.MEDIUM),
            Task("Tour", "Museum", true, TaskPriority.LOW)
        ))

        // Notify all adapters
        studentAdapter.notifyDataSetChanged()
        cartAdapter.notifyDataSetChanged()
        taskAdapter.notifyDataSetChanged()

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