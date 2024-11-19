package com.sprinto.pocketnotes.arrangeNotes.view.notes_grid_screen

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.wear.compose.material.ContentAlpha
import com.google.accompanist.systemuicontroller.SystemUiController
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import com.sprinto.pocketnotes.arrangeNotes.view.notes_grid_screen.ui_model.NoteUIModel
import com.sprinto.pocketnotes.arrangeNotes.view.notes_grid_screen.ui_model.TaskUIModel
import com.sprinto.pocketnotes.arrangeNotes.view_model.NotesGridScreenUIState
import com.sprinto.pocketnotes.arrangeNotes.view_model.NotesGridScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesGridScreen(
    lifecycleOwner: LifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current,
    viewModel: NotesGridScreenViewModel,
    systemUiController: SystemUiController,
    onEditNote: (id: Long) -> Unit,
    onCreateNewNote: () -> Unit,
) {

    val backgroundColor = MaterialTheme.colorScheme.background

    SideEffect {
        systemUiController.setSystemBarsColor(backgroundColor, darkIcons = false)
    }

    DisposableEffect(lifecycleOwner) {

        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadAllNotes()
                systemUiController.setSystemBarsColor(backgroundColor, darkIcons = false)
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }

    }

    val uiState = viewModel.uiState.collectAsState().value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "NoteIt", fontWeight = FontWeight.Bold) },
                //backgroundColor = MaterialTheme.colorScheme.background,
                //contentColor = MaterialTheme.colorScheme.onBackground,
                //elevation = 0.dp
            )
        },
        floatingActionButton = {
            if (uiState is NotesGridScreenUIState.Loaded) {
                FloatingActionButton(onClick = { onCreateNewNote() }) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = "New note"
                    )
                }
            }
        }
    ) {
        Box(modifier = Modifier.padding(it)) {

            when (uiState) {

                is NotesGridScreenUIState.Empty -> {}

                is NotesGridScreenUIState.Loaded -> {

                    val notes = uiState.notes

                    LazyVerticalStaggeredGrid(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        columns = StaggeredGridCells.Fixed(2),
                        userScrollEnabled = true,
                    ) {
                        items(notes.size) { index ->

                            val col = index % 2

                            val padding =
                                if (col == 0) PaddingValues(end = 8.dp, top = 8.dp, bottom = 8.dp)
                                else PaddingValues(start = 8.dp, top = 8.dp, bottom = 8.dp)

                            Note(
                                note = notes[index],
                                paddingValues = padding
                            ) { onEditNote(notes[index].id) }

                        }
                    }
                }

                is NotesGridScreenUIState.Loading -> {
                    Loading()
                }

                is NotesGridScreenUIState.NoNotes -> {
                    NoNotes { onCreateNewNote() }
                }
            }


        }
    }

}


@Composable
private fun NoNotes(onCreateNewNote: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "You have no saved notes. Create your first one by tapping the button below",
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = onCreateNewNote) {
                Text(text = "Create new note")
            }
        }

    }
}

@Composable
private fun Loading() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun Note(note: NoteUIModel, paddingValues: PaddingValues, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxWidth(0.5f)
            .background(
                if (note.color != null) Color(note.color) else MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(13)
            )
            .clickable { onClick() }
    ) {
        Column {
            if (note.coverImage != null) {
                CoverImage(note.coverImage)
            }
            Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 32.dp)) {

                Text(
                    note.title,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (note.body != null) {
                    Text(
                        note.body, maxLines = 5, overflow = TextOverflow.Ellipsis,
                        lineHeight = 22.sp,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = ContentAlpha.medium)
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                if (!note.tasks.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    TaskList(note.tasks)
                    Spacer(modifier = Modifier.height(24.dp))
                }
                Text(
                    note.lastModifiedAt,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End,
                    style = MaterialTheme.typography.headlineSmall
                        .copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(
                                alpha = ContentAlpha.disabled
                            )
                        )
                )
            }

        }
    }
}

@Composable
private fun TaskListItem(taskUIModel: TaskUIModel) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        CheckCircle(taskUIModel.complete)
        Text(
            text = taskUIModel.task,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface.copy(
                    alpha = if (taskUIModel.complete) ContentAlpha.medium
                    else ContentAlpha.high
                ),
                textDecoration = if (taskUIModel.complete) TextDecoration.LineThrough
                else TextDecoration.None
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun CheckCircle(isChecked: Boolean) {
    val modifier = if (isChecked) {

        Modifier
            .height(12.dp)
            .aspectRatio(1f)
            .background(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = ContentAlpha.medium),
                shape = CircleShape
            )

    } else {

        Modifier
            .height(12.dp)
            .aspectRatio(1f)
            .border(1.dp, color = MaterialTheme.colorScheme.onSurface, shape = CircleShape)
    }
    Box(
        modifier = modifier
    ) {

    }
}

@Composable
private fun TaskList(tasks: List<TaskUIModel>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        tasks.map {
            TaskListItem(it)
        }
    }
}

@Composable
private fun CoverImage(imagePath: String) {
    GlideImage(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(RoundedCornerShape(13)),
        imageModel = Uri.parse(imagePath),
        imageOptions = ImageOptions(
            contentScale = ContentScale.Crop
        )
    )
}
