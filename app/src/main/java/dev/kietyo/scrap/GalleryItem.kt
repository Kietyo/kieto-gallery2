package dev.kietyo.scrap

import android.graphics.ImageDecoder
import android.os.Parcelable
import androidx.compose.ui.graphics.ImageBitmap
import coil.request.ImageRequest
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

sealed class GalleryItem {
    data class Folder(val folderName: String) : GalleryItem()

//    @Parcelize
//    data class FolderWithImage(
//        val folderName: String,
//        val imageSource: ImageDecoder.Source
//    ) : GalleryItem(), Parcelable

    data class FolderWithAsyncImage(
        val folderName: String,
        val imageRequest: @RawValue ImageRequest
    ) : GalleryItem()

    data class Image(val imageBitmap: ImageBitmap) : GalleryItem()
}