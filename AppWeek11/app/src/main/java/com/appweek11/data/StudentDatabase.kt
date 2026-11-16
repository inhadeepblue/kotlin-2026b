package com.appweek11.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * StudentDatabase
 * - Room 데이터베이스 추상 클래스
 * - @Database: 데이터베이스 정의
 * - entities: 포함할 Entity 클래스 목록
 * - version: 데이터베이스 버전 (마이그레이션에 사용)
 * - exportSchema: 버전 관리를 위한 스키마 내보내기 여부
 */
@Database(
    entities = [Student::class],
    version = 1,
    exportSchema = false
)
abstract class StudentDatabase : RoomDatabase() {

    // DAO 추상 메소드
    abstract fun studentDao(): StudentDao

    companion object {
        // volatile: 멀티스레드 환경에서 가시성 보장
        @Volatile
        private var INSTANCE: StudentDatabase? = null

        /**
         * Singleton 패턴으로 데이터베이스 인스턴스 획득
         * - synchronized: 스레드 안전성 보장
         * - 여러 스레드가 동시에 접근해도 하나의 인스턴스만 생성
         */
        fun getDatabase(context: Context): StudentDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StudentDatabase::class.java,
                    "student_database"  // 데이터베이스 파일명
                )
                    // .addMigrations(...)  // 마이그레이션 추가 가능
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}