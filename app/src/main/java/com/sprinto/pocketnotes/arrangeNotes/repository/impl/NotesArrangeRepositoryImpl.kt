package com.sprinto.pocketnotes.arrangeNotes.repository.impl

import com.sprinto.pocketnotes.core.data.dao.NoteDao
import com.sprinto.pocketnotes.core.model.NoteModel
import com.sprinto.pocketnotes.core.model.TaskListItemModel
import com.sprinto.pocketnotes.arrangeNotes.repository.NotesArrangeRepository

class NotesArrangeRepositoryImpl(private val noteDao: NoteDao) :
    NotesArrangeRepository {

    override suspend fun getAllNotes(): List<NoteModel> {

        return noteDao.getNotes().map {
            val note = it.note
            val tasks = it.tasks
            NoteModel(
                id = note.id,
                title = note.title,
                body = note.body,
                coverImage = note.coverImage,
                lastModifiedAt = note.lastModifiedAt,
                tasks = tasks.map { t ->
                    TaskListItemModel(t.task, t.complete)
                },
                color = note.color,
            )
        }
    }


}