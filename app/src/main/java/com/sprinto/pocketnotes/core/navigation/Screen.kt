package com.sprinto.pocketnotes.core.navigation

sealed class Screen(val route: String) {
    data object NotesGridScreen : Screen("notes_grid_screen")

    data object EditNoteScreen : Screen("edit_note_screen?id={id}") {

        const val argNoteID = "id"

        fun getFullPath(noteID: String?): String {
            return if (noteID == null) "edit_note_screen"
            else "edit_note_screen?id=$noteID"
        }


    }
}
