package dev.kietyo.scrap.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import dev.kietyo.scrap.GalleryItem
import dev.kietyo.scrap.viewmodels.GalleryViewModel

@Composable
fun FolderItemWithAsyncImage(
    galleryViewModel: GalleryViewModel,
    item: GalleryItem.FolderWithAsyncImage,
    onImageClick: () -> Unit,
) {
    val myImageContentScale = galleryViewModel.imageContentScaleFlow.collectAsState()
    val alignmentState = galleryViewModel.alignmentFlow.collectAsState()
    FolderItemWithAsyncImageTemplate(
        model = item.imageRequest,
        folderName = item.folderName,
        contentScaleState = myImageContentScale,
        alignmentState = alignmentState,
        onImageClick = onImageClick,
    )
}