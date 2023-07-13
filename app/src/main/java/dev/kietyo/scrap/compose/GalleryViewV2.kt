package dev.kietyo.scrap.compose

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import dev.kietyo.scrap.GalleryItem
import dev.kietyo.scrap.R
import dev.kietyo.scrap.log
import dev.kietyo.scrap.viewmodels.GalleryViewModel
import dev.kietyo.scrap.viewmodels.KDocumentModel

@SuppressLint("CoroutineCreationDuringComposition")
@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun GalleryViewV2(
    galleryViewModel: GalleryViewModel,
    onImageClick: () -> Unit,
) {
    val numColumns = galleryViewModel.numColumnsFlow.collectAsState()
    log("Executing GalleryViewV2")
    var gridSize = IntSize(0, 0)

    log("Loading grid...")
    val files = galleryViewModel.listFiles.collectAsState(initial = emptyList())
    LazyVerticalGrid(
        columns = GridCells.Fixed(numColumns.value),
        verticalArrangement = Arrangement.spacedBy(2.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier
            .background(Color.DarkGray)
            .onGloballyPositioned {
                log("LazyVerticalGrid: layout coords: ${it.size}")
                gridSize = it.size
            }
            .fillMaxSize()
    ) {
        log("LazyVerticalGrid: gridSize: ${gridSize}")
        files.value.forEach { documentFile ->
            item {
                GalleryItemWrapper(galleryViewModel, documentFile, onImageClick)
            }
        }
    }
}

val exampleImages = listOf(
    R.drawable.example_200x200,
    R.drawable.example_200x300,
    R.drawable.example_300x200,
)

@Composable
fun GalleryItemWrapper(
    galleryViewModel: GalleryViewModel,
    documentFile: KDocumentModel,
    onImageClick: () -> Unit
) {
    val galleryItem = galleryViewModel.fileToGalleryItemCacheV2[documentFile]!!
    log("GalleryItemWrapper: loading $documentFile")
    if (galleryItem.value == null) {
        Box(
            modifier = Modifier
                .aspectRatio(1.0f)
                .background(Color.Black)
                .fillMaxSize()
        ) {
            CircularProgressIndicator(modifier = Modifier.fillMaxSize())
        }
    } else {
        val it = galleryItem.value!!
        when (it) {
            is GalleryItem.Folder -> FolderItem(item = it, {})
            is GalleryItem.Image -> TODO()
            is GalleryItem.FolderWithAsyncImage -> FolderItemWithAsyncImage(
                galleryViewModel,
                item = it,
                onImageClick
            )

            is GalleryItem.ExampleImage -> ExampleFolderItemWithImage(
                galleryViewModel,
                it.exampleFolderName,
                it.exampleImage
            )
        }
    }

}

