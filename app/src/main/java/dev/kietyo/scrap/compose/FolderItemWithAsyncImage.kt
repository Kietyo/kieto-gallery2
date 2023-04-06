package dev.kietyo.scrap.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import dev.kietyo.scrap.GalleryItem
import dev.kietyo.scrap.viewmodels.GalleryViewModel

@Composable
fun FolderItemWithAsyncImage(
    galleryViewModel: GalleryViewModel,
    item: GalleryItem.FolderWithAsyncImage,
) {
    val myImageContentScale = galleryViewModel.imageContentScaleFlow.collectAsState()
    val alignmentState = galleryViewModel.alignmentFlow.collectAsState()
    FolderItemWithAsyncImageTemplate(
        model = item.imageRequest,
        folderName = item.folderName,
        contentScaleState = myImageContentScale,
        alignmentState = alignmentState
    )
}