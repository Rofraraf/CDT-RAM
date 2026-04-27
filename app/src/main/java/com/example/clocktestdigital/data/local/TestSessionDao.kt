package com.example.clocktestdigital.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TestSessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: TestSessionEntity): Long

    @Query("SELECT * FROM test_sessions ORDER BY testDateTime DESC")
    suspend fun getAllSessions(): List<TestSessionEntity>

    @Query("SELECT * FROM test_sessions WHERE patientCode = :patientCode ORDER BY testDateTime DESC")
    suspend fun getSessionsByPatient(patientCode: String): List<TestSessionEntity>

    @Query("SELECT * FROM test_sessions WHERE localId = :sessionId LIMIT 1")
    suspend fun getSessionById(sessionId: Long): TestSessionEntity?
}
