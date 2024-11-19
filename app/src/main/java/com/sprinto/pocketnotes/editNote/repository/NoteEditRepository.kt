package com.sprinto.pocketnotes.editNote.repository

import com.sprinto.pocketnotes.core.model.NoteModel

interface NoteEditRepository {

    suspend fun saveNote(note: NoteModel): Long
    suspend fun loadNote(id: Long): NoteModel?
    suspend fun deleteNote(id: Long)

}