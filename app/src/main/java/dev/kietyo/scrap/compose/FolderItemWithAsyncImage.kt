package dev.kietyo.scrap.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import dev.kietyo.scrap.GalleryItem
import dev.kietyo.scrap.viewmodels.GalleryViewModel

@Composable
fun FolderItemWithAsyncImage(
    galleryViewModel: GalleryViewModel,
    item: GalleryItem.FolderWithAsyncImage,
    imageModifier: Modifier = Modifier,
) {
    val myImageContentScale = galleryViewModel.imageContentSscaleFlow.collectAsState()
    Box(
        modifier = Modifier
            .aspectRatio(1.0f)
            .background(Color.Black)
            .fillMaxSize()
    ) {
        SubcomposeAsyncImage(model = item.imageRequest, contentDescription = "a pic",
            contentScale = myImageContentScale.value,
            modifier = imageModifier
                .fillMaxWidth(),
            loading = {
                CircularProgressIndicator()
            }
        )
        //        AsyncImage(
        //            model = item.imageRequest, contentDescription = "a pic",
        //            contentScale = imageContentScale,
        //            modifier = imageModifier
        //                .fillMaxWidth(),
        //        )
        Text(
            text = item.folderName,
            fontSize = 10.sp,
            color = Color.White,
            modifier = Modifier.align(Alignment.BottomStart)
        )
    }
}