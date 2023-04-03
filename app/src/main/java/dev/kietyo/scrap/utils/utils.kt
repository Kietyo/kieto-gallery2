package dev.kietyo.scrap.utils

import androidx.compose.ui.layout.ContentScale
import androidx.documentfile.provider.DocumentFile
import coil.request.ImageRequest
import dev.kietyo.scrap.ContentScaleSelection
import dev.kietyo.scrap.GalleryItem
import dev.kietyo.scrap.di.MyApplication

const val STRING_ACTIVITY_RESULT = "STRING_ACTIVITY_RESULT"

val DocumentFile.isImage: Boolean
    get() {
        return type == "image/jpeg"
    }

fun DocumentFile.toGalleryItem(): GalleryItem {
    val directory = this
    require(directory.isDirectory)
    val firstImageFileInDirectory = directory.listFiles().firstOrNull {
        it.isImage
    }
    return if (firstImageFileInDirectory == null) {
        GalleryItem.Folder(directory.name!!)
    } else {
        GalleryItem.FolderWithAsyncImage(
            directory.name!!,
            ImageRequest.Builder(MyApplication.getAppContext())
                .data(firstImageFileInDirectory.uri).build()
        )
    }
}

val contentScales = listOf(
    ContentScaleSelection("Crop", ContentScale.Crop),
    ContentScaleSelection("Fit", ContentScale.Fit),
    ContentScaleSelection("Fill Height", ContentScale.FillHeight),
    ContentScaleSelection("Fill Width", ContentScale.FillWidth),
    ContentScaleSelection("Inside", ContentScale.Inside),
    ContentScaleSelection("Fill Bounds", ContentScale.FillBounds),
    ContentScaleSelection("None", ContentScale.None),
).sortedBy { it.text }
