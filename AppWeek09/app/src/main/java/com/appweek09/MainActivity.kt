package com.appweek09

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class MainActivity : AppCompatActivity() {

    // UI Components
    private lateinit var radioGroup: RadioGroup
    private lateinit var radioStudentList: RadioButton
    private lateinit var radioTaskManager: RadioButton

    private lateinit var listView: ListView
    private lateinit var editTextInput: EditText
    private lateinit var buttonAdd: Button
    private lateinit var buttonClear: Button
    private lateinit var textViewInfo: TextView

    // Task Manager specific UI
    private lateinit var layoutTaskControls: LinearLayout
    private lateinit var spinnerPriority: Spinner
    private lateinit var editTextDescription: EditText

    // Data Storage
    private lateinit var studentList: ArrayList<Student>
    private lateinit var taskList: ArrayList<Task>

    // Adapters
    private lateinit var studentAdapter: ArrayAdapter<Student>
    private lateinit var taskAdapter: ArrayAdapter<Task>

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
        taskList = ArrayList()

        Log.d(TAG, "Data structures initialized")
    }

    private fun setupViews() {
        // Mode selection
        radioGroup = findViewById(R.id.radioGroup)
        radioStudentList = findViewById(R.id.radioStudentList)
        radioTaskManager = findViewById(R.id.radioTaskManager)

        // Common UI
        listView = findViewById(R.id.listView)
        editTextInput = findViewById(R.id.editTextInput)
        buttonAdd = findViewById(R.id.buttonAdd)
        buttonClear = findViewById(R.id.buttonClear)
        textViewInfo = findViewById(R.id.textViewInfo)

        // Task Manager specific
        layoutTaskControls = findViewById(R.id.layoutTaskControls)
        spinnerPriority = findViewById(R.id.spinnerPriority)
        editTextDescription = findViewById(R.id.editTextDescription)

        setupPrioritySpinner()

        Log.d(TAG, "Views initialized")
    }

    private fun setupPrioritySpinner() {
        val priorities = TaskPriority.values().map { it.displayName }
        val priorityAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, priorities)
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPriority.adapter = priorityAdapter
    }

    private fun setupAdapters() {
        studentAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, studentList)
        taskAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, taskList)

        Log.d(TAG, "Adapters setup completed")
    }

    private fun setupListeners() {
        // Mode selection listeners
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioStudentList -> setMode(AppMode.STUDENT_LIST)
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
                layoutTaskControls.visibility = View.GONE
                listView.adapter = studentAdapter
            }
            AppMode.TASK_MANAGER -> {
                editTextInput.hint = "Enter task title"
                buttonAdd.text = "Add Task"
                layoutTaskControls.visibility = View.VISIBLE
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

    private fun clearAdditionalFields() {
        editTextDescription.text.clear()
        spinnerPriority.setSelection(0)
    }

    private fun clearAll() {
        when (currentMode) {
            AppMode.STUDENT_LIST -> {
                val count = studentList.size
                studentList.clear()
                studentAdapter.notifyDataSetChanged()
                showToast("Cleared all $count students")
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
            AppMode.TASK_MANAGER -> updateTaskInfo()
        }
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
            Student("Kim"),
            Student("Lee"),
            Student("Park")
        ))

        // Add initial tasks
        taskList.addAll(listOf(
            Task("Complete Assignment", "Mobile Programming", false, TaskPriority.HIGH),
            Task("Shopping", "Visit Mart", false, TaskPriority.MEDIUM),
            Task("Tour", "Museum", true, TaskPriority.LOW)
        ))

        // Notify all adapters
        studentAdapter.notifyDataSetChanged()
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