package dev.kietyo.scrap.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import dev.kietyo.scrap.log
import dev.kietyo.scrap.viewmodels.GalleryViewModel

@Composable
fun ExampleFolderItemWithImage(
    galleryViewModel: GalleryViewModel,
    folderName: String,
    exampleImage: Int) {
    log("ExampleFolderItemWithImage: rendering... folderName: $folderName")
    val imageContentScale = galleryViewModel.imageContentScaleFlow.collectAsState()
    val alignment = galleryViewModel.alignmentFlow.collectAsState()
    FolderItemWithAsyncImageTemplate(
        exampleImage,
        folderName,
        imageContentScale,
        alignment,
        {}
    )
}