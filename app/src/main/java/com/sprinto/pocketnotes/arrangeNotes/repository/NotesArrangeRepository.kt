package com.sprinto.pocketnotes.arrangeNotes.repository

import com.sprinto.pocketnotes.core.model.NoteModel

interface NotesArrangeRepository {

    suspend fun getAllNotes(): List<NoteModel>

}