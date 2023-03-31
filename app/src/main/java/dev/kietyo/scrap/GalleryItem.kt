package dev.kietyo.scrap

import android.graphics.ImageDecoder
import androidx.compose.ui.graphics.ImageBitmap

sealed class GalleryItem {
    data class Folder(val folderName: String) : GalleryItem()
    data class FolderWithImage(
        val folderName: String,
        val imageSource: ImageDecoder.Source
    ) : GalleryItem()
    data class Image(val imageBitmap: ImageBitmap) : GalleryItem()
}