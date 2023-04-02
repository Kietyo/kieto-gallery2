package dev.kietyo.scrap.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.documentfile.provider.DocumentFile
import coil.request.ImageRequest
import dev.kietyo.scrap.GalleryItem
import dev.kietyo.scrap.di.MyApplication
import dev.kietyo.scrap.log
import dev.kietyo.scrap.utils.isImage
import dev.kietyo.scrap.viewmodels.GalleryViewModel

@Composable
fun GalleryViewV2(
    galleryViewModel: GalleryViewModel,
    documentFile: DocumentFile,
) {
    log("Executing GalleryViewV2")
    val galleryItems = documentFile.listFiles().map { it.toGalleryItem() }
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .background(Color.Black)
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

private fun DocumentFile.toGalleryItem(): GalleryItem {
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
