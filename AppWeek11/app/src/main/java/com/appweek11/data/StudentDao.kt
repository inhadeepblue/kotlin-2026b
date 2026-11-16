package com.appweek11.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * StudentDao (Data Access Object)
 * - 데이터베이스 연산 인터페이스
 * - @Dao: 이 인터페이스가 DAO임을 선언
 * - Flow<>: 코루틴을 사용한 비동기 데이터 흐름
 * - suspend: 코루틴 함수 (비동기 작업)
 */
@Dao
interface StudentDao {

    /**
     * 모든 학생 조회 (Flow 반환 - 실시간 업데이트)
     * - Flow: 데이터가 변경되면 자동으로 새로운 값 발행
     * - ORDER BY id DESC: 최신 순서대로 정렬
     */
    @Query("SELECT * FROM students ORDER BY id DESC")
    fun getAllStudents(): Flow<List<Student>>

    /**
     * 특정 ID의 학생 조회
     */
    @Query("SELECT * FROM students WHERE id = :studentId")
    suspend fun getStudentById(studentId: Int): Student?

    /**
     * 학생 추가
     * - @Insert: INSERT 쿼리 자동 생성
     * - onConflict: 동일한 key 발견 시 기존 데이터 대체
     * - suspend: 코루틴에서만 호출 가능
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: Student)

    /**
     * 여러 학생 한 번에 추가
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllStudents(students: List<Student>)

    /**
     * 학생 업데이트
     * - @Update: 기존 데이터 수정
     */
    @Update
    suspend fun updateStudent(student: Student)

    /**
     * 특정 학생 삭제
     * - @Delete: 특정 객체 삭제
     */
    @Delete
    suspend fun deleteStudent(student: Student)

    /**
     * 모든 학생 삭제
     * - @Query: 커스텀 SQL 쿼리
     */
    @Query("DELETE FROM students")
    suspend fun deleteAllStudents()

    /**
     * 학생명으로 검색
     * - LIKE: SQL 패턴 매칭
     * - %: 와일드카드 (모든 문자)
     */
    @Query("SELECT * FROM students WHERE student_name LIKE '%' || :searchQuery || '%' ORDER BY id DESC")
    fun searchStudents(searchQuery: String): Flow<List<Student>>

    /**
     * 학과별 학생 조회
     */
    @Query("SELECT * FROM students WHERE department = :department ORDER BY student_name ASC")
    fun getStudentsByDepartment(department: String): Flow<List<Student>>

    /**
     * 학년별 학생 조회
     */
    @Query("SELECT * FROM students WHERE grade = :grade ORDER BY student_name ASC")
    fun getStudentsByGrade(grade: String): Flow<List<Student>>

    /**
     * 전체 학생 수 조회
     */
    @Query("SELECT COUNT(*) FROM students")
    fun getStudentCount(): Flow<Int>

    /**
     * 학과별 학생 수 조회
     */
    @Query("SELECT COUNT(*) FROM students WHERE department = :department")
    fun getStudentCountByDepartment(department: String): Flow<Int>
}