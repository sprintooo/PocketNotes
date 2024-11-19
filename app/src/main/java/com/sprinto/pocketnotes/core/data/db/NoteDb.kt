package com.sprinto.pocketnotes.core.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sprinto.pocketnotes.core.data.dao.NoteDao
import com.sprinto.pocketnotes.core.data.entity.NoteEntity
import com.sprinto.pocketnotes.core.data.entity.TaskEntity

@Database(entities = [NoteEntity::class, TaskEntity::class], version = 1, exportSchema = false)
abstract class NoteDb : RoomDatabase() {
    abstract fun noteDao(): NoteDao

    companion object {

        private var INSTANCE: NoteDb? = null

        fun getInstance(context: Context): NoteDb {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        NoteDb::class.java,
                        "todo_list_database"
                    ).fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }

    }

}