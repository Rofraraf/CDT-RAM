package com.example.clocktestdigital.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface InputEventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(events: List<InputEventEntity>)

    @Query("SELECT * FROM input_events WHERE sessionId = :sessionId ORDER BY eventTimeMs ASC")
    suspend fun getEventsBySession(sessionId: Long): List<InputEventEntity>

    @Query("SELECT COUNT(*) FROM input_events WHERE sessionId = :sessionId")
    suspend fun getEventCountBySession(sessionId: Long): Int

    @Query("SELECT COUNT(*) FROM input_events WHERE sessionId = :sessionId AND isHoverEvent = 1")
    suspend fun getHoverEventCountBySession(sessionId: Long): Int
}