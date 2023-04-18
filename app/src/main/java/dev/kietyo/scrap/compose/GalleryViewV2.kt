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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.currentComposer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.documentfile.provider.DocumentFile
import dev.kietyo.scrap.GalleryItem
import dev.kietyo.scrap.R
import dev.kietyo.scrap.log
import dev.kietyo.scrap.utils.toGalleryItem
import dev.kietyo.scrap.viewmodels.GalleryViewModel
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun GalleryViewV2(
    galleryViewModel: GalleryViewModel,
) {
    val numColumns = galleryViewModel.numColumnsFlow.collectAsState()
    log("Executing GalleryViewV2")
    var gridSize = IntSize(0, 0)
//    val coroutineScope = rememberCoroutineScope()

//    coroutineScope.launch {
//        log("Launching effect...")
//        galleryViewModel.currentFiles.forEach {documentFile ->
//            galleryViewModel.fileToGalleryItemCacheV2.getOrPut(documentFile) {
//                mutableStateOf(documentFile.toGalleryItem())
//            }
//            log("Computed gallery item for $documentFile")
//        }
//    }

//    LaunchedEffect(key1 = true) {
//        galleryViewModel.currentFiles.forEach {documentFile ->
//            galleryViewModel.fileToGalleryItemCacheV2.getOrPut(documentFile) {
//                mutableStateOf(documentFile.toGalleryItem())
//            }
//        }
//    }

    log("Loading grid...")
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
        galleryViewModel.currentFiles.value.forEach { documentFile ->
            item {
                GalleryItemWrapper(galleryViewModel, documentFile)
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
fun GalleryItemWrapper(galleryViewModel: GalleryViewModel, documentFile: DocumentFile) {
    val galleryItem = galleryViewModel.fileToGalleryItemCacheV2.get(documentFile)!!
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
            is GalleryItem.Folder -> FolderItem(item = it)
            is GalleryItem.Image -> TODO()
            is GalleryItem.FolderWithAsyncImage -> FolderItemWithAsyncImage(
                galleryViewModel,
                item = it,
            )

            is GalleryItem.ExampleImage -> ExampleFolderItemWithImage(
                galleryViewModel,
                it.exampleFolderName,
                it.exampleImage
            )
        }
    }

}

