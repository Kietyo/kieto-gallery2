package dev.kietyo.scrap.compose

import androidx.compose.foundation.background
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
fun FolderItem(item: GalleryItem.Folder) {
    Box(
        modifier = Modifier
            .aspectRatio(1.0f)
            .background(Color.Black)
            .fillMaxSize()
            .padding(3.dp),
        contentAlignment = Alignment.BottomStart
    ) {
        Box(
            Modifier
                //                .wrapContentSize()
                .fillMaxWidth()
                .fillMaxHeight()
                .background(Color.DarkGray)
        ) {
            val text = item.folderName
            Text(
                text = text,
                fontSize = 10.sp,
                color = Color.White,
                modifier = Modifier.align(Alignment.BottomStart)
            )
        }
    }
}