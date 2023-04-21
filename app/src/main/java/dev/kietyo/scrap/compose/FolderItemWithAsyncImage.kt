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
        FolderItemData.WithImage(
            item.imageRequest,
            myImageContentScale,
            alignmentState
        ),
        folderName = item.folderName,
        onImageClick = onImageClick,
    )
}