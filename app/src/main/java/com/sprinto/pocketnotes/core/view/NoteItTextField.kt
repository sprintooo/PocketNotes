package com.sprinto.pocketnotes.core.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.wear.compose.material.ContentAlpha

@Composable
fun NoteItTextField(
    modifier: Modifier = Modifier,
    text: String,
    textStyle: TextStyle? = null,
    hint: String,
    hintStyle: TextStyle? = null,
    maxLines: Int = Int.MAX_VALUE,
    imeAction: ImeAction = ImeAction.Default,
    onValueChanged: (String) -> Unit,
    onImeAction: ((String) -> Unit)? = null,
) {

    Box(modifier = modifier) {
        if (text.isEmpty()) {
            Text(
                text = hint,
                style = hintStyle ?: MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(ContentAlpha.medium)
                )
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            BasicTextField(
                value = text,
                onValueChange = { onValueChanged(it) },
                modifier = Modifier.weight(1f),
                singleLine = maxLines == 1,
                maxLines = maxLines,
                textStyle = textStyle
                    ?: MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                cursorBrush = Brush.verticalGradient(
                    0.00f to MaterialTheme.colorScheme.primary,
                    1.00f to MaterialTheme.colorScheme.primary
                ),
                keyboardOptions = KeyboardOptions(imeAction = imeAction),
                keyboardActions = KeyboardActions {
                    if (text.isNotEmpty() && onImeAction != null) {
                        onImeAction(text)
                    }
                }
            )
        }
    }

}