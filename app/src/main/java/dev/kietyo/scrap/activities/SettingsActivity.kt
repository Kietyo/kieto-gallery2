package dev.kietyo.scrap.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.view.WindowMetrics
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.kietyo.scrap.GalleryItem
import dev.kietyo.scrap.compose.GalleryViewV2
import dev.kietyo.scrap.compose.exampleImages
import dev.kietyo.scrap.log
import dev.kietyo.scrap.ui.theme.AndroidComposeTemplateTheme
import dev.kietyo.scrap.utils.DisplayTextI
import dev.kietyo.scrap.utils.alignments
import dev.kietyo.scrap.utils.contentScales
import dev.kietyo.scrap.viewmodels.GalleryViewModel
import dev.kietyo.scrap.viewmodels.SettingsViewModel
import kotlin.math.ceil

@RequiresApi(Build.VERSION_CODES.R)
fun WindowMetrics.asString(): String {
    val it = this
    return "WindowMetrics(bounds=${it.bounds}, windowInsets=${it.windowInsets})"
}

data class WindowSizeData(
    val bounds: Rect = Rect(0, 0, 0, 0)
)

class SettingsActivity : ComponentActivity() {

    private val galleryViewModel: GalleryViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        log(galleryViewModel.imageContentScaleFlow.value)

        log("windowManager.maximumWindowMetrics: ${windowManager.maximumWindowMetrics.asString()}")
        log("windowManager.currentWindowMetrics: ${windowManager.currentWindowMetrics.asString()}")

        settingsViewModel.windowSize = WindowSizeData(windowManager.currentWindowMetrics.bounds)

        setContent {
            AndroidComposeTemplateTheme {
                SettingsContent(settingsViewModel, galleryViewModel) {
                    end()
                }
            }
        }
    }

    private fun end() {
        val intent = Intent()
        //        intent.putExtra(STRING_ACTIVITY_RESULT, it)
        //                launcher.launch(intent)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}

@Composable
fun SettingsContent(
    settingsViewModel: SettingsViewModel,
    galleryViewModel: GalleryViewModel,
    onSaveButtonClick: (String) -> Unit
) {
    Column {
        Row {
            var currentText by remember {
                mutableStateOf("Enter here")
            }
            Text(text = "Hello world")
            TextField(value = currentText, onValueChange = {
                currentText = it
            })
            TextButton(
                onClick = { onSaveButtonClick(currentText) }) {
                Text(text = "Save")
            }
        }

        val computeExampleImagesFn = {
            log("Generating example images")
            val numColumns = galleryViewModel.numColumnsFlow.value
            val windowSize = settingsViewModel.windowSize
            val width = windowSize.bounds.width()
            val pixelsPerColumn = width / numColumns.toDouble()
            val heightPerGalleryItem = pixelsPerColumn
            val height = windowSize.bounds.height()
            val numGalleryItemsForHeight = ceil(height / heightPerGalleryItem).toInt()
            val totalGalleryItems = numGalleryItemsForHeight * numColumns
            log("totalGalleryItems: $totalGalleryItems")
            (1..totalGalleryItems).map {
                GalleryItem.ExampleImage("item $it", exampleImages.random())
            }
        }

        var exampleImages by remember {
            mutableStateOf(computeExampleImagesFn())
        }

        Stepper(
            "Num columns",
            onMinus = {
                val currentNumColumns = galleryViewModel.numColumnsFlow.value
                galleryViewModel.decrementColumns()
                val newNumColumns = galleryViewModel.numColumnsFlow.value
                if (currentNumColumns != newNumColumns) {
                    exampleImages = computeExampleImagesFn()
                }
            },
            onPlus = {
                galleryViewModel.incrementColumns()
                exampleImages = computeExampleImagesFn()
            },
        )

        SettingRow(
            "Content scale",
            galleryViewModel.imageContentScaleFlow.collectAsState(),
            contentScales,
            onDropdownItemSelect = {
                galleryViewModel.updateImageContentScale(it)
            }
        )

        SettingRow(
            "Alignment",
            galleryViewModel.alignmentFlow.collectAsState(),
            alignments,
            onDropdownItemSelect = {
                galleryViewModel.updateAlignment(it)
            }
        )

        GalleryViewV2(
            galleryViewModel = galleryViewModel,
            galleryItems = exampleImages
        )
    }
}

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

@Composable
fun Stepper(
    label: String,
    onMinus: () -> Unit,
    onPlus: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = label,
        )
        OutlinedButton(
            modifier = Modifier.padding(start = 5.dp),
            onClick = { onMinus() }
        ) {
            Text("-")
        }
        OutlinedButton(
            modifier = Modifier.padding(start = 5.dp),
            onClick = { onPlus() }
        ) {
            Text("+")
        }
    }
}