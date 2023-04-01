package dev.kietyo.scrap.compose

import android.graphics.ImageDecoder
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.sp
import androidx.core.graphics.decodeBitmap
import coil.decode.ImageSource
import dev.kietyo.scrap.GalleryItem

@Composable
fun FolderItemWithImage(
    item: GalleryItem.FolderWithImage,
    defaultImageContentScale: ContentScale,
) {
    val imageContentScale by remember {
        mutableStateOf(defaultImageContentScale)
    }
    Box(
        modifier = Modifier
            .aspectRatio(1.0f)
            .background(Color.Black)
            .fillMaxSize()
    ) {
        FolderImage(item.imageSource, imageContentScale)
        //        AsyncImage(model = ImageRequest.Builder(contex), contentDescription = "a pic")
        Text(
            text = item.folderName,
            fontSize = 10.sp,
            color = Color.White,
            modifier = Modifier.align(Alignment.BottomStart)
        )
    }
}


@Composable
fun FolderImage(imageSource: ImageDecoder.Source, imageContentScale: ContentScale) {
    val imageBitmap by rememberSaveable {
        mutableStateOf(BitmapPainter(imageSource.decodeBitmap { info, source -> }.asImageBitmap()))
    }
    Image(
        imageBitmap,
        "a picture",
        contentScale = imageContentScale,
        modifier = Modifier.fillMaxWidth()
    )
}