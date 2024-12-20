package com.sprinto.pocketnotes.editNote.view.note_edit_screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sprinto.pocketnotes.R
import androidx.wear.compose.material.ContentAlpha
import com.sprinto.pocketnotes.core.view.NoteItTextField
import com.sprinto.pocketnotes.arrangeNotes.view.notes_grid_screen.ui_model.TaskUIModel
import com.sprinto.pocketnotes.editNote.view_model.NoteEditScreenViewModel
import com.google.accompanist.systemuicontroller.SystemUiController
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.launch

private val noteColorPresets = listOf(
    0xFF212121,
    0xFF464D77,
    0xFF5277E0,
    0xFF36827F,
    0xFF7B5E7B,
    0xFF0D3B66,
    0xFF230007,
    0xFFAE76A6,
    0xFFA5243D
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditScreen(
    viewModel: NoteEditScreenViewModel,
    systemUiController: SystemUiController? = null,
    onDelete: () -> Unit,
    onBack: () -> Unit,
) {

    val modalBottomSheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()

    val taskListAdded = viewModel.tasksAdded.collectAsState().value
    val coverImageAdded = viewModel.coverImageAdded.collectAsState().value
    val lastUpdatedTime = viewModel.lastUpdatedTimestamp.collectAsState().value
    val noteColor = viewModel.noteColor.collectAsState().value ?: noteColorPresets[0]

    val color = Color(noteColor)

    systemUiController?.let {
        SideEffect {
            it.setSystemBarsColor(color, darkIcons = false)
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = {
            if (it != null) {
                viewModel.addCoverImage(it.path)
            }
        }
    )

    val drawable =

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 16.dp),
                        text = lastUpdatedTime,
                        textAlign = TextAlign.End,
                        style = MaterialTheme.typography.headlineMedium,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(Icons.Filled.ArrowBack, "back")
                    }
                },
                //backgroundColor = color,
                //contentColor = MaterialTheme.colors.onBackground,
                //elevation = 0.dp
            )
        },
        bottomBar = {
            TextOptionsBar(
                listChecked = taskListAdded,
                onToggleList = { if (it) viewModel.addTaskList() else viewModel.removeTaskList() },
                coverImageChecked = coverImageAdded,
                onToggleCoverImage = {
                    if (it) {
                        launcher.launch("image/*")
                    } else {
                        viewModel.removeCoverImage()
                    }
                },
                onClickMore = {
                    coroutineScope.launch { modalBottomSheetState.show() }
                }
            )
        },
        containerColor = color
    ) {

        Box(modifier = Modifier.padding(it))
        {
            Column {

                if (coverImageAdded) {
                    CoverImage(viewModel.coverImageUri.collectAsState().value)
                }

                Column(
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    NoteItTextField(
                        modifier = Modifier.fillMaxWidth(),
                        text = viewModel.title.collectAsState().value,
                        textStyle = MaterialTheme.typography.headlineSmall.copy(
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Bold
                        ),
                        hint = "Title",
                        hintStyle = MaterialTheme.typography.headlineSmall
                            .copy(
                                color = MaterialTheme.colorScheme.onBackground.copy(
                                    alpha = ContentAlpha.medium
                                )
                            ),
                        onValueChanged = { text -> viewModel.editTitle(text) }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    NoteItTextField(
                        modifier = Modifier
                            .fillMaxWidth(),
                        text = viewModel.body.collectAsState().value,
                        textStyle = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onBackground,
                            lineHeight = 26.sp
                        ),
                        hint = "Write something...",
                        hintStyle = MaterialTheme.typography.bodySmall
                            .copy(
                                color = MaterialTheme.colorScheme.onBackground.copy(
                                    alpha = ContentAlpha.medium
                                )
                            ),
                        onValueChanged = { text -> viewModel.editBody(text) }
                    )
                    if (taskListAdded) {

                        val tasks = viewModel.tasks.collectAsState().value

                        Spacer(modifier = Modifier.height(24.dp))
                        TaskList(
                            tasks = tasks,
                            onAddNewItem = { viewModel.addTask() },
                            onEditTask = { task, text ->
                                viewModel.editTask(task, text)
                            },
                            onToggleTask = { task, checked ->
                                if (checked) viewModel.unCheckTask(task)
                                else viewModel.checkTask(task)
                            }
                        )
                    }
                }
            }

        }

    }
}

@Composable
private fun BottomSheet(onSelectColor: (color: Long) -> Unit, onDelete: () -> Unit) {
    Box(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.surface)
            .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 32.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                noteColorPresets.map {
                    ColorCircle(
                        color = it,
                        onClick = { onSelectColor(it) },
                    )
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
            Tile(icon = {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(id = R.drawable.ic_delete),
                    contentDescription = "delete note",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }, label = {
                Text(text = "Delete note", color = MaterialTheme.colorScheme.onSurface)
            }) {
                onDelete()
            }
        }

    }
}

@Composable
private fun Tile(icon: @Composable () -> Unit, label: @Composable () -> Unit, onClick: () -> Unit) {
    Row(modifier = Modifier.clickable { onClick() }) {
        icon()
        Spacer(modifier = Modifier.width(16.dp))
        label()
    }
}

@Composable
private fun CoverImage(coverImagePath: String?) {
    GlideImage(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.3f),
        imageModel = Uri.parse(coverImagePath),
        imageOptions = ImageOptions(
            contentScale = ContentScale.Crop
        )
    )
}

@Composable
private fun TaskList(
    tasks: List<TaskUIModel>,
    onAddNewItem: () -> Unit,
    onEditTask: (TaskUIModel, String) -> Unit,
    onToggleTask: (TaskUIModel, Boolean) -> Unit
) {

    Column {

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

            tasks.map { it ->
                ListItem(
                    task = it.task,
                    selected = it.complete,
                    { text ->
                        onEditTask(it, text)
                    },
                    { checked ->
                        onToggleTask(it, checked)
                    }
                )
            }

        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.clickable {
            onAddNewItem()
        }) {
            Icon(
                Icons.Filled.Add,
                contentDescription = "add task",
                tint = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text("Add new item", color = MaterialTheme.colorScheme.onBackground)
        }
    }

}


@Composable
private fun ListItem(
    task: String,
    selected: Boolean,
    onTaskEdited: (String) -> Unit,
    onToggle: (Boolean) -> Unit
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        CheckCircle(selected, onToggle)
        NoteItTextField(
            text = task,
            textStyle = MaterialTheme.typography.bodySmall.copy(
                textDecoration = if (selected) TextDecoration.LineThrough else TextDecoration.None,
                color = MaterialTheme.colorScheme.onBackground.copy(
                    alpha = if (selected) ContentAlpha.medium else ContentAlpha.high
                )
            ),
            hint = "Add task",
            onValueChanged = onTaskEdited
        )
    }

}


@Composable
private fun CheckCircle(isChecked: Boolean, onClick: (Boolean) -> Unit) {

    val modifier = if (isChecked) {

        Modifier
            .height(12.dp)
            .width(12.dp)
            .background(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = ContentAlpha.medium),
                shape = CircleShape
            )

    } else {

        Modifier
            .height(12.dp)
            .width(12.dp)
            .border(1.dp, color = MaterialTheme.colorScheme.onSurface, shape = CircleShape)
    }
    Box(
        modifier = modifier.clickable { onClick(isChecked) }
    ) {

    }
}


@Composable
fun TextOptionsBar(
    listChecked: Boolean,
    onToggleList: (Boolean) -> Unit,
    coverImageChecked: Boolean,
    onToggleCoverImage: (Boolean) -> Unit,
    onClickMore: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxHeight(0.0625f)
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.surface)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {

            IconToggleButton(
                checked = coverImageChecked,
                onCheckedChange = { onToggleCoverImage(it) }
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(id = R.drawable.ic_image),
                    contentDescription = "add cover image",
                    tint = if (coverImageChecked) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onBackground
                )
            }

            IconToggleButton(
                checked = listChecked,
                onCheckedChange = { onToggleList(it) }
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(id = R.drawable.ic_list),
                    contentDescription = "add list",
                    tint = if (listChecked) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(
                modifier = Modifier
                    .width(8.dp)
                    .weight(1f)
            )


            IconButton(onClick = { onClickMore() }) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(id = R.drawable.ic_more),
                    contentDescription = "more",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.width(16.dp))
        }
    }
}

@Composable
private fun ColorCircle(
    color: Long,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .height(24.dp)
            .width(24.dp)
            .background(color = MaterialTheme.colorScheme.onSurface, shape = CircleShape)
            .padding(2.dp)
            .background(color = Color(color), shape = CircleShape)
            .clickable { onClick() }
    )
}