package dev.kietyo.scrap.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import dev.kietyo.scrap.GalleryItem
import dev.kietyo.scrap.viewmodels.GalleryViewModel

@Composable
fun GalleryView(
    galleryViewModel: GalleryViewModel,
    galleryItems: List<GalleryItem>,
    defaultImageContentScale: ContentScale,
) {
    //    val scrollState = ScrollState(0)
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .background(Color.Black)
        //            .verticalScroll(scrollState)
    ) {
        galleryItems.forEach {
            item {
                when (it) {
                    is GalleryItem.Folder -> FolderItem(item = it)
                    is GalleryItem.Image -> TODO()
                    is GalleryItem.FolderWithAsyncImage -> FolderItemWithAsyncImage(
                        galleryViewModel,
                        item = it,
                    )
                }
            }
        }
    }
}