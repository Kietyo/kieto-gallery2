package dev.kietyo.scrap.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun Header(
    onLoaderFolderClick: () -> Unit,
    onSettingsButtonClick: () -> Unit,
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        Button(onClick = onLoaderFolderClick) {
            Text("Load Folder")
        }
        Button(onClick = {
            onSettingsButtonClick()
        }) {
            Text(text = "Settings")
        }
    }
}