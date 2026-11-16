package com.appweek11

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.appweek11.data.Student
import com.appweek11.data.StudentDatabase
import com.appweek11.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Room Database 관련
    private lateinit var database: StudentDatabase
    private lateinit var adapter: StudentAdapter

    // 현재 표시 중인 데이터
    private var currentStudents: List<Student> = emptyList()

    companion object {
        private const val TAG = "KotlinWeek11App"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d(TAG, "onCreate: AppWeek11 Room Database started")

        // Room Database 초기화
        database = StudentDatabase.getDatabase(this)

        setupViews()
        setupRecyclerView()
        setupListeners()
        observeStudents()
    }

    /**
     * UI 요소 초기화
     */
    private fun setupViews() {
        val layouts = arrayOf("Linear", "Grid (2 columns)", "Grid (3 columns)")
        val layoutAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, layouts)
        layoutAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerLayout.adapter = layoutAdapter

        Log.d(TAG, "Views initialized")
    }

    /**
     * RecyclerView 설정
     */
    private fun setupRecyclerView() {
        adapter = StudentAdapter(
            studentList = emptyList(),
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

    /**
     * 이벤트 리스너 설정
     */
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

    /**
     * Room Database에서 데이터 관찰 (실시간 업데이트)
     * - Flow를 사용하여 데이터 변경 감지
     * - 자동으로 adapter 업데이트
     */
    private fun observeStudents() {
        lifecycleScope.launch {
            database.studentDao().getAllStudents().collect { students ->
                currentStudents = students
                adapter.updateList(students)
                updateStudentCount()
                Log.d(TAG, "Students updated: ${students.size}")
            }
        }
    }

    /**
     * 학생 추가 (Room Database에 저장)
     */
    private fun addStudent() {
        val studentName = binding.editTextStudent.text.toString().trim()

        if (studentName.isEmpty()) {
            showToast("Please enter a student name")
            Log.w(TAG, "Empty student name input")
            return
        }

        // 이미 있는 이름인지 확인
        if (currentStudents.any { it.name == studentName }) {
            showToast("Student '$studentName' already exists")
            Log.w(TAG, "Duplicate student: $studentName")
            return
        }

        // 새 학생 객체 생성
        val newStudent = Student(
            name = studentName,
            department = "Computer Science",
            grade = "Year ${(1..4).random()}",
            email = "${studentName.replace(" ", ".")}@university.edu"
        )

        // 비동기로 데이터베이스에 저장
        lifecycleScope.launch {
            database.studentDao().insertStudent(newStudent)
            binding.editTextStudent.text.clear()
            showToast("Added: $studentName")
            Log.d(TAG, "Added student: $studentName")
        }
    }

    /**
     * 학생 삭제
     */
    private fun removeStudent(student: Student) {
        lifecycleScope.launch {
            database.studentDao().deleteStudent(student)
            showToast("Removed: ${student.name}")
            Log.d(TAG, "Removed student: ${student.name}")
        }
    }

    /**
     * 모든 학생 삭제
     */
    private fun clearAllStudents() {
        if (currentStudents.isEmpty()) {
            showToast("List is already empty")
            return
        }

        val count = currentStudents.size

        lifecycleScope.launch {
            database.studentDao().deleteAllStudents()
            showToast("Cleared all $count students")
            Log.d(TAG, "Cleared all students")
        }
    }

    /**
     * 학생 수 업데이트
     */
    private fun updateStudentCount() {
        binding.textViewInfo.text = "Total Students: ${currentStudents.size}"
    }

    /**
     * 아이템 클릭 처리
     */
    private fun handleItemClick(student: Student, position: Int) {
        val message = """
            Name: ${student.name}
            Department: ${student.department}
            Grade: ${student.grade}
            Email: ${student.email}
            Added: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault()).format(student.addedDate)}
        """.trimIndent()

        AlertDialog.Builder(this)
            .setTitle("Student Details")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()

        Log.d(TAG, "Clicked student: ${student.name}")
    }

    /**
     * 아이템 롱클릭 처리
     */
    private fun handleItemLongClick(position: Int) {
        if (position < currentStudents.size) {
            val student = currentStudents[position]

            AlertDialog.Builder(this)
                .setTitle("Delete Student")
                .setMessage("Are you sure you want to delete ${student.name}?")
                .setPositiveButton("Delete") { _, _ ->
                    removeStudent(student)
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    /**
     * LayoutManager 변경
     */
    private fun changeLayoutManager(layoutType: Int) {
        binding.recyclerView.layoutManager = when (layoutType) {
            0 -> LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            1 -> GridLayoutManager(this, 2)
            2 -> GridLayoutManager(this, 3)
            else -> LinearLayoutManager(this)
        }

        Log.d(TAG, "Changed layout to type: $layoutType")
    }

    /**
     * Toast 메시지 표시
     */
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: Current students: ${currentStudents.size}")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: Database operations saved")
    }
}