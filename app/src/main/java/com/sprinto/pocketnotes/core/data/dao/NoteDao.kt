package com.sprinto.pocketnotes.core.data.dao

import androidx.room.*
import com.sprinto.pocketnotes.core.data.entity.NoteEntity
import com.sprinto.pocketnotes.core.data.entity.NoteWithTasksEntity
import com.sprinto.pocketnotes.core.data.entity.TaskEntity

@Dao
interface NoteDao {

    @Transaction
    @Query("SELECT * FROM notes WHERE id=:id")
    suspend fun getNote(id: Long): NoteWithTasksEntity?

    @Transaction
    @Query("SELECT * FROM notes ORDER BY lastModifiedAt DESC")
    suspend fun getNotes(): List<NoteWithTasksEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveNote(note: NoteEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveTasks(tasks: List<TaskEntity>)

    @Transaction
    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteNote(id: Long)

}