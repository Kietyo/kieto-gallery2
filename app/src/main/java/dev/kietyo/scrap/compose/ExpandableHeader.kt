package dev.kietyo.scrap.compose

import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import dev.kietyo.scrap.viewmodels.GalleryViewModel

@Composable
fun ExpandableHeader(
    galleryViewModel: GalleryViewModel,
    openFolderLauncher: ActivityResultLauncher<Uri?>
) {
    var isSettingsOpen by remember {
        mutableStateOf(false)
    }
    var currentGallerySettings = galleryViewModel.currentGallerySettings
    if (isSettingsOpen) {
        ImageSettingsContent(
            galleryViewModel = galleryViewModel,
            onSaveButtonClick = {
                currentGallerySettings = galleryViewModel.currentGallerySettings
                galleryViewModel.applyGallerySettings(currentGallerySettings)
                isSettingsOpen = false
            },
            onCancelButtonClick = {
                galleryViewModel.applyGallerySettings(currentGallerySettings)
                isSettingsOpen = false
            }
        )
    } else {
        Header(
            onLoaderFolderClick = {
                openFolderLauncher.launch(null)
            }
        ) {
            isSettingsOpen = true
        }
    }
}