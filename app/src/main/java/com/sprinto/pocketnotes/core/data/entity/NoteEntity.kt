package com.sprinto.pocketnotes.core.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val title: String,
    val body: String,

    val coverImage: String?,
    val lastModifiedAt: String,

    val color: Long?

)