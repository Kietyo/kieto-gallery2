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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import dev.kietyo.scrap.GalleryItem
import dev.kietyo.scrap.compose.GalleryViewV2
import dev.kietyo.scrap.compose.ImageSettingsContent
import dev.kietyo.scrap.compose.exampleImages
import dev.kietyo.scrap.log
import dev.kietyo.scrap.ui.theme.AndroidComposeTemplateTheme
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
    onSaveButtonClick: () -> Unit
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
                onClick = { onSaveButtonClick() }) {
                Text(text = "Save")
            }
        }

        var currentColumnNum by remember {
            mutableStateOf(galleryViewModel.numColumnsFlow.value)
        }

        val computeExampleImagesFn = {
            log("Generating example images")
            val windowSize = settingsViewModel.windowSize
            val width = windowSize.bounds.width()
            val pixelsPerColumn = width / currentColumnNum.toDouble()
            val heightPerGalleryItem = pixelsPerColumn
            val height = windowSize.bounds.height()
            val numGalleryItemsForHeight = ceil(height / heightPerGalleryItem).toInt()
            val totalGalleryItems = numGalleryItemsForHeight * currentColumnNum
            log("totalGalleryItems: $totalGalleryItems")
            (1..totalGalleryItems).map {
                GalleryItem.ExampleImage("item $it", exampleImages.random())
            }
        }

        var exampleImages by remember {
            mutableStateOf(computeExampleImagesFn())
        }

        ImageSettingsContent(
            galleryViewModel = galleryViewModel,
            onColumnChange = {
                if (it != currentColumnNum) {
                    currentColumnNum = it
                    exampleImages = computeExampleImagesFn()
                }
            },
            onSaveButtonClick = onSaveButtonClick,
        )

        GalleryViewV2(
            galleryViewModel = galleryViewModel,
            galleryItems = exampleImages
        )
    }
}

