package dev.kietyo.scrap.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.kietyo.scrap.GalleryItem

@Composable
fun FolderItem(item: GalleryItem.Folder, onImageClick: () -> Unit) {
    Box(
        modifier = Modifier
            .aspectRatio(1.0f)
            .background(Color.Black)
            .fillMaxSize()
            .clickable {
                onImageClick()
            },
        contentAlignment = Alignment.BottomStart
    ) {
        Box(
            Modifier
                //                .wrapContentSize()
                .fillMaxWidth()
                .fillMaxHeight()
                .background(Color.DarkGray)
        ) {
            Text(
                text = item.folderName,
                fontSize = 10.sp,
                color = Color.White,
                modifier = Modifier.align(Alignment.BottomStart)
            )
        }
    }
}

