package com.appweek11

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.appweek11.databinding.ItemStudentBinding
import com.appweek11.data.Student

class StudentAdapter(
    private var studentList: List<Student>,
    private val onItemClick: (Student, Int) -> Unit,
    private val onItemLongClick: (Int) -> Unit
) : RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {

    inner class StudentViewHolder(private val binding: ItemStudentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(student: Student) {
            binding.apply {
                textViewName.text = student.name
                textViewDept.text = student.department
                textViewGrade.text = "Grade: ${student.grade}"
                textViewEmail.text = student.email

                root.setOnClickListener {
                    onItemClick(student, adapterPosition)
                }

                root.setOnLongClickListener {
                    onItemLongClick(adapterPosition)
                    true
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val binding = ItemStudentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return StudentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        holder.bind(studentList[position])
    }

    override fun getItemCount(): Int = studentList.size

    /**
     * 리스트 업데이트 메소드
     * - Flow에서 새로운 데이터를 받으면 호출
     */
    fun updateList(newList: List<Student>) {
        studentList = newList
        notifyDataSetChanged()
    }
}