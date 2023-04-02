package dev.kietyo.scrap.utils

import androidx.documentfile.provider.DocumentFile
import coil.request.ImageRequest
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


