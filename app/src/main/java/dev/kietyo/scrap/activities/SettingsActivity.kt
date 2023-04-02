package dev.kietyo.scrap.activities

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.registerForActivityResult
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
import dev.kietyo.scrap.log
import dev.kietyo.scrap.ui.theme.AndroidComposeTemplateTheme
import dev.kietyo.scrap.utils.STRING_ACTIVITY_RESULT
import dev.kietyo.scrap.viewmodels.GalleryViewModel

class SettingsActivity : ComponentActivity() {

    private val galleryViewModel: GalleryViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        log(galleryViewModel.imageContentSscaleFlow.value)

        setContent {
            AndroidComposeTemplateTheme {
                SettingsContent(galleryViewModel) {
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
    galleryViewModel: GalleryViewModel,
    onSaveButtonClick: (String) -> Unit) {
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

        GalleryViewV2(
            galleryViewModel = galleryViewModel,
            galleryItems = listOf(
                GalleryItem.ExampleImage("test dir 1"),
                GalleryItem.ExampleImage("test dir 2"),
                GalleryItem.ExampleImage("test dir 3"),
                GalleryItem.ExampleImage("test dir 4"),
            )
        )
    }


}