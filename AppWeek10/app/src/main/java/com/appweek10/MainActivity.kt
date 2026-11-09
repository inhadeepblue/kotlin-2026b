package com.appweek10

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.appweek10.data.Student
import com.appweek10.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var studentList: ArrayList<Student>
    private lateinit var adapter: StudentAdapter

    companion object {
        private const val TAG = "KotlinWeek09App"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d(TAG, "onCreate: AppWeek09 RecyclerView started")

        setupViews()
        setupRecyclerView()
        setupListeners()
        addInitialData()
    }

    private fun setupViews() {
        studentList = ArrayList()

        val layouts = arrayOf("Linear", "Grid (2 columns)", "Grid (3 columns)")
        val layoutAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, layouts)
        layoutAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerLayout.adapter = layoutAdapter

        Log.d(TAG, "Views initialized")
    }

    private fun setupRecyclerView() {
        adapter = StudentAdapter(
            studentList,
            onItemClick = { student, position ->
                handleItemClick(student, position)
            },
            onItemLongClick = { position ->
                handleItemLongClick(position)
            }
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        )

        Log.d(TAG, "RecyclerView setup completed")
    }

    private fun setupListeners() {
        binding.buttonAdd.setOnClickListener {
            addStudent()
        }

        binding.buttonClear.setOnClickListener {
            clearAllStudents()
        }

        binding.spinnerLayout.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                changeLayoutManager(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        Log.d(TAG, "Event listeners setup completed")
    }

    private fun addStudent() {
        val studentName = binding.editTextStudent.text.toString().trim()

        if (studentName.isEmpty()) {
            showToast("Please enter a student name")
            Log.w(TAG, "Empty student name input")
            return
        }

        if (studentList.any { it.name == studentName }) {
            showToast("Student '$studentName' already exists")
            Log.w(TAG, "Duplicate student: $studentName")
            return
        }

        val student = Student(
            name = studentName,
            department = "Computer Science",
            grade = "Year ${(Math.random() * 4 + 1).toInt()}",
            email = "${studentName.replace(" ", ".")}@university.edu"
        )

        studentList.add(student)
        adapter.notifyItemInserted(studentList.size - 1)
        binding.editTextStudent.text.clear()
        updateStudentCount()

        showToast("Added: $studentName")
        Log.d(TAG, "Added student: $studentName (Total: ${studentList.size})")
    }

    private fun removeStudent(position: Int) {
        if (position >= 0 && position < studentList.size) {
            val removedStudent = studentList.removeAt(position)
            adapter.notifyItemRemoved(position)
            updateStudentCount()

            showToast("Removed: ${removedStudent.name}")
            Log.d(TAG, "Removed student: ${removedStudent.name} at position $position")
        }
    }

    private fun clearAllStudents() {
        if (studentList.isEmpty()) {
            showToast("List is already empty")
            return
        }

        val count = studentList.size
        studentList.clear()
        adapter.notifyDataSetChanged()
        updateStudentCount()

        showToast("Cleared all $count students")
        Log.d(TAG, "Cleared all students")
    }

    private fun updateStudentCount() {
        binding.textViewInfo.text = "Total Students: ${studentList.size}"
    }

    private fun handleItemClick(student: Student, position: Int) {
        val message = """
            Name: ${student.name}
            ID: ${student.id.take(8)}...
            Department: ${student.department}
            Grade: ${student.grade}
            Email: ${student.email}
            Position: ${position + 1}
        """.trimIndent()

        AlertDialog.Builder(this)
            .setTitle("Student Details")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()

        Log.d(TAG, "Clicked student: ${student.name} at position $position")
    }

    private fun handleItemLongClick(position: Int) {
        AlertDialog.Builder(this)
            .setTitle("Delete Student")
            .setMessage("Are you sure you want to delete this student?")
            .setPositiveButton("Delete") { _, _ ->
                removeStudent(position)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun changeLayoutManager(layoutType: Int) {
        binding.recyclerView.layoutManager = when (layoutType) {
            0 -> LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            1 -> GridLayoutManager(this, 2)
            2 -> GridLayoutManager(this, 3)
            else -> LinearLayoutManager(this)
        }

        Log.d(TAG, "Changed layout to type: $layoutType")
    }

    private fun addInitialData() {
        val initialStudents = listOf(
            Student("KIM", department = "Computer Science", grade = "3rd Year",
                email = "kim.cs@univ.edu"),
            Student("LEE", department = "Software Engineering", grade = "2nd Year",
                email = "lee.se@univ.edu"),
            Student("PARK", department = "Computer Science", grade = "1st Year",
                email = "park.cs@univ.edu"),
            Student("CHOI", department = "Data Science", grade = "4th Year",
                email = "choi.ds@univ.edu"),
            Student("JUNG", department = "Computer Science", grade = "2nd Year",
                email = "jung.cs@univ.edu"),
            Student("WOO", department = "Software Engineering", grade = "3rd Year",
                email = "woo.se@univ.edu")
        )

        studentList.addAll(initialStudents)
        adapter.notifyDataSetChanged()
        updateStudentCount()

        Log.d(TAG, "Added initial data: ${initialStudents.size} students")
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: Current student count: ${studentList.size}")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: Saving state with ${studentList.size} students")
    }
}