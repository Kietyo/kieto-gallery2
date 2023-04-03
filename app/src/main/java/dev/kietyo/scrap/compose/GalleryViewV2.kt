package dev.kietyo.scrap.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import dev.kietyo.scrap.GalleryItem
import dev.kietyo.scrap.R
import dev.kietyo.scrap.log
import dev.kietyo.scrap.viewmodels.GalleryViewModel
import kotlin.random.Random

@Composable
fun GalleryViewV2(
    galleryViewModel: GalleryViewModel,
    galleryItems: List<GalleryItem>,
) {
    val numColumns = galleryViewModel.numColumnsFlow.collectAsState()
    log("Executing GalleryViewV2")
//    val galleryItems = documentFile.listFiles().map { it.toGalleryItem() }
    var gridSize = IntSize(0, 0)
    LazyVerticalGrid(
        columns = GridCells.Fixed(numColumns.value),
        verticalArrangement = Arrangement.spacedBy(1.dp),
        horizontalArrangement = Arrangement.spacedBy(1.dp),
        modifier = Modifier
            .background(Color.DarkGray)
            .onGloballyPositioned {
                log("LazyVerticalGrid: layout coords: ${it.size}")
                gridSize = it.size
            }
            .fillMaxSize()
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
                    is GalleryItem.ExampleImage -> ExampleFolderItemWithImage(
                        galleryViewModel,
                        it.exampleFolderName,
                        it.exampleImage)
                }
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
fun ExampleFolderItemWithImage(
    galleryViewModel: GalleryViewModel,
    folderName: String,
    exampleImage: Int) {
    log("ExampleFolderItemWithImage: rendering... folderName: $folderName")
    val imageContentScale = galleryViewModel.imageContentScaleFlow.collectAsState()
    Box(
        modifier = Modifier
            .aspectRatio(1.0f)
            .background(Color.Black)
            .fillMaxSize()
    ) {
//        val randomAspect = Random.nextInt(25, 200) / 100f
//        Box(
//            modifier = Modifier
//                .background(Color.Red)
//                .aspectRatio(randomAspect)
//                .fillMaxSize()
//                .align(Alignment.Center)
//        )
        AsyncImage(
            model = exampleImage,
            contentDescription = "example image",
            contentScale = imageContentScale.value.contentScale,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = folderName,
            fontSize = 10.sp,
            color = Color.White,
            modifier = Modifier.align(Alignment.BottomStart)
        )
    }
}