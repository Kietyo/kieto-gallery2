package dev.kietyo.scrap.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import dev.kietyo.scrap.utils.DisplayTextI

@Composable
fun <T : DisplayTextI> SettingRow(
    settingText: String,
    defaultItem: State<T>,
    items: List<T>,
    onDropdownItemSelect: (T) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            settingText,
            modifier = Modifier.weight(1f)
        )

        Box(modifier = Modifier.wrapContentSize()) {
            var expanded by remember { mutableStateOf(false) }
            val currentItem = defaultItem
            OutlinedButton(onClick = { expanded = true }) {
                Text(text = currentItem.value.displayText)
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }) {
                items.forEach {
                    DropdownMenuItem(
                        modifier = Modifier
                            .background(if (currentItem.value == it) Color.LightGray else Color.White),
                        onClick = {
                            onDropdownItemSelect(it)
                            expanded = false
                        }) {
                        Text(text = it.displayText)
                    }
                }
            }
        }
    }
}