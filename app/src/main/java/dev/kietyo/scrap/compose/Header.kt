package dev.kietyo.scrap.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.LocalContentColor
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import dev.kietyo.scrap.ContentScaleSelection

@Composable
fun Header(
    initialContentScaleSelection: ContentScaleSelection,
    contentScaleSelections: List<ContentScaleSelection>,
    onLoaderFolderClick: () -> Unit,
    onContentScaleSelection: (ContentScaleSelection) -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        Button(onClick = onLoaderFolderClick) {
            Text("Load Folder")
        }
        Box(modifier = Modifier.wrapContentSize()) {
            var expanded by remember { mutableStateOf(false) }
            var currentItem by remember { mutableStateOf(initialContentScaleSelection) }
            Button(onClick = { expanded = true }) {
                Text(text = "Select content scale")
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }) {
                contentScaleSelections.forEach {
                    DropdownMenuItem(
                        modifier = Modifier
                            .background(if (currentItem == it) Color.LightGray else Color.White),
                        onClick = {
                            onContentScaleSelection(it)
                            currentItem = it
                            expanded = false
                        }) {
                        Text(text = it.text)
                    }
                }
            }
        }
    }
}