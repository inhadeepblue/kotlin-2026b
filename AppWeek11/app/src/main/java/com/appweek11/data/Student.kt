package com.appweek11.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Student Entity
 * - Room 데이터베이스의 테이블 정의
 * - @Entity: 이 클래스가 데이터베이스 테이블임을 선언
 * - @PrimaryKey: 각 행의 고유 식별자
 * - @ColumnInfo: 열의 이름 정의 (선택사항)
 */
@Entity(tableName = "students")
data class Student(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "student_name")
    val name: String,

    val department: String = "Computer Science",
    val grade: String = "1st Year",
    val email: String = "",

    @ColumnInfo(name = "added_date")
    val addedDate: Long = System.currentTimeMillis()
) {
    override fun toString(): String = name
}