package com.sprinto.pocketnotes.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sprinto.pocketnotes.core.data.db.NoteDb
import com.sprinto.pocketnotes.arrangeNotes.view.notes_grid_screen.NotesGridScreen
import com.sprinto.pocketnotes.arrangeNotes.view_model.NotesGridScreenViewModelFactory
import com.sprinto.pocketnotes.editNote.repository.impl.NoteEditRepositoryImpl
import com.sprinto.pocketnotes.editNote.util.LastUpdatedDateTimeFormatter
import com.sprinto.pocketnotes.editNote.view.note_edit_screen.NoteEditScreen
import com.sprinto.pocketnotes.editNote.view_model.NoteEditScreenViewModelFactory
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.sprinto.pocketnotes.arrangeNotes.repository.impl.NotesArrangeRepositoryImpl
import com.sprinto.pocketnotes.arrangeNotes.util.last_edit_datetime_formatter.TimeOrDateDateTimeFormatter


@Composable
fun SetupNavGraph(navController: NavHostController) {

    val context = LocalContext.current
    val noteDao = NoteDb.getInstance(context).noteDao()
    val systemUiController = rememberSystemUiController()

    NavHost(navController = navController, startDestination = Screen.NotesGridScreen.route) {

        composable(route = Screen.NotesGridScreen.route) {

            NotesGridScreen(
                systemUiController = systemUiController,
                viewModel = viewModel(
                    factory = NotesGridScreenViewModelFactory(
                        notesArrangeRepository = NotesArrangeRepositoryImpl(noteDao = noteDao),
                        lastEditTimeFormatter = TimeOrDateDateTimeFormatter()
                    )
                ),
                onEditNote = {
                    navController.navigate(Screen.EditNoteScreen.getFullPath(it.toString())) {
                        launchSingleTop = true
                    }
                },
                onCreateNewNote = {
                    navController.navigate(Screen.EditNoteScreen.getFullPath(null)) {
                        launchSingleTop = true
                    }
                }
            )

        }

        composable(
            route = Screen.EditNoteScreen.route,
            arguments = listOf(
                navArgument(Screen.EditNoteScreen.argNoteID) {
                    type = NavType.StringType; nullable = true; defaultValue = null
                },
            )
        ) { backStackEntry ->

            val noteID = backStackEntry.arguments?.getString(Screen.EditNoteScreen.argNoteID)

            NoteEditScreen(
                systemUiController = systemUiController,
                viewModel = viewModel(
                    factory = NoteEditScreenViewModelFactory(
                        noteEditRepository = NoteEditRepositoryImpl(noteDao),
                        noteID = noteID?.toLong(),
                        lastUpdateTimestampFormatter = LastUpdatedDateTimeFormatter()
                    )
                ),
                onBack = {
                    navController.popBackStack()
                },
                onDelete = {
                    navController.popBackStack()
                }
            )


        }

    }
}