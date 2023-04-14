package dev.kietyo.scrap.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
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
        alignment
    )
}