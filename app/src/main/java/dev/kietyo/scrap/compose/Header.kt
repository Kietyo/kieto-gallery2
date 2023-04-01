package dev.kietyo.scrap.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import dev.kietyo.scrap.ContentScaleSelection

@Composable
fun Header(onLoaderFolderClick: () -> Unit, onContentScaleSelection: (ContentScaleSelection) -> Unit) {

    val contentScales = listOf(
        ContentScaleSelection("Crop", ContentScale.Crop),
        ContentScaleSelection("Fit", ContentScale.Fit),
        ContentScaleSelection("Fill Height", ContentScale.FillHeight),
        ContentScaleSelection("Fill Width", ContentScale.FillWidth),
        ContentScaleSelection("Inside", ContentScale.Inside),
        ContentScaleSelection("Fill Bounds", ContentScale.FillBounds),
        ContentScaleSelection("None", ContentScale.None),
    ).sortedBy { it.text }

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        Button(onClick = onLoaderFolderClick) {
            Text("Load Folder")
        }
        Box(modifier = Modifier.wrapContentSize()) {
            var expanded by remember { mutableStateOf(false) }
            Button(onClick = { expanded = true }) {
                Text(text = "Select content scale")
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                contentScales.forEach {
                    DropdownMenuItem(onClick = {
                        onContentScaleSelection(it)
                        expanded = false
                    }) {
                        Text(text = it.text)
                    }
                }
            }
        }
    }
}