package dev.kietyo.scrap

import android.graphics.ImageDecoder
import android.os.Parcelable
import androidx.compose.ui.graphics.ImageBitmap
import coil.request.ImageRequest
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

sealed class GalleryItem {
    @Parcelize
    data class Folder(val folderName: String) : GalleryItem(), Parcelable

//    @Parcelize
//    data class FolderWithImage(
//        val folderName: String,
//        val imageSource: ImageDecoder.Source
//    ) : GalleryItem(), Parcelable

    @Parcelize
    data class FolderWithAsyncImage(
        val folderName: String,
        val imageRequest: @RawValue ImageRequest
    ) : GalleryItem(), Parcelable

    data class Image(val imageBitmap: ImageBitmap) : GalleryItem()
}