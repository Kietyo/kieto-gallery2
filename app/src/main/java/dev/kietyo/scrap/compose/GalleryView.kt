package dev.kietyo.scrap.compose

import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import dev.kietyo.scrap.log
import dev.kietyo.scrap.ui.theme.AndroidComposeTemplateTheme
import dev.kietyo.scrap.viewmodels.GalleryViewModel
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun GalleryView(
    galleryViewModel: GalleryViewModel,
    onImageClick: () -> Unit
) {
    val openFolderLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocumentTree()
    ) { selectedFolder: Uri? ->
        if (selectedFolder != null) {
            galleryViewModel.updateSelectedFolder(selectedFolder)
        }
    }

    AndroidComposeTemplateTheme {
        Column {
            val documentFile = galleryViewModel.documentFile.collectAsState(initial = null)
            ExpandableHeader(galleryViewModel, openFolderLauncher)

            documentFile.value?.let { file ->
                val listFiles = galleryViewModel.listFiles.collectAsState(initial = emptyList())
                galleryViewModel.updateCurrentFiles(listFiles.value)
                GalleryViewV2(galleryViewModel, onImageClick)
            }
        }
    }
}