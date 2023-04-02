package dev.kietyo.scrap.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.kietyo.scrap.GalleryItem
import dev.kietyo.scrap.log
import dev.kietyo.scrap.viewmodels.GalleryViewModel

@Composable
fun GalleryViewV2(
    galleryViewModel: GalleryViewModel,
    galleryItems: List<GalleryItem>,
) {
    log("Executing GalleryViewV2")
//    val galleryItems = documentFile.listFiles().map { it.toGalleryItem() }
    var gridSize = IntSize(0, 0)
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        verticalArrangement = Arrangement.spacedBy(1.dp),
        horizontalArrangement = Arrangement.spacedBy(1.dp),
        modifier = Modifier
            .background(Color.DarkGray)
            .onGloballyPositioned {
                log("LazyVerticalGrid: layout coords: ${it.size}")
                gridSize = it.size
            }
    ) {
        log("LazyVerticalGrid: gridSize: ${gridSize}")
        galleryItems.forEach {
            item {
                when (it) {
                    is GalleryItem.Folder -> FolderItem(item = it)
                    is GalleryItem.Image -> TODO()
                    is GalleryItem.FolderWithAsyncImage -> FolderItemWithAsyncImage(
                        galleryViewModel,
                        item = it,
                    )
                    is GalleryItem.ExampleImage -> ExampleFolderItemWithImage(it.exampleFolderName)
                }
            }
        }
    }
}

@Composable
fun ExampleFolderItemWithImage(folderName: String) {
    log("ExampleFolderItemWithImage: rendering... folderName: $folderName")
    Box(
        modifier = Modifier
            .aspectRatio(1.0f)
            .background(Color.Black)
            .fillMaxSize()
            .onGloballyPositioned {
                log("layout coords: ${it.size}")
            }
    ) {
        Box(
            modifier = Modifier
                .background(Color.Red)
                .aspectRatio(0.5f)
                .fillMaxSize()
                .align(Alignment.Center)
        )
        Text(
            text = folderName,
            fontSize = 10.sp,
            color = Color.White,
            modifier = Modifier.align(Alignment.BottomStart)
        )
    }
}