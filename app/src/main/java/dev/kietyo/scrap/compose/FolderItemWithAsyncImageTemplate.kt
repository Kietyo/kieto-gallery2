package dev.kietyo.scrap.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import dev.kietyo.scrap.utils.AlignmentEnum
import dev.kietyo.scrap.utils.ContentScaleEnum

sealed class FolderItemData {
    data class WithImage(
        val model: Any,
        val contentScaleState: State<ContentScaleEnum>,
        val alignmentState: State<AlignmentEnum>,
    ): FolderItemData()
    object NoImage: FolderItemData()
}

@Composable
fun FolderItemWithAsyncImageTemplate(
    data: FolderItemData,
    folderName: String,
    onImageClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(1.0f)
            .background(Color.Black)
            .fillMaxSize()
            .clickable {
                onImageClick()
            }
    ) {
        when (data) {
            is FolderItemData.WithImage -> SubcomposeAsyncImage(
                model = data.model,
                contentDescription = "a pic",
                contentScale = data.contentScaleState.value.contentScale,
                alignment = data.alignmentState.value.alignment,
                modifier = Modifier.fillMaxWidth()
                    .align(Alignment.Center),
                loading = {
                    CircularProgressIndicator(modifier = Modifier.fillMaxSize())
                }
            )
            FolderItemData.NoImage -> Unit
        }

        Text(
            text = folderName,
            fontSize = 10.sp,
            color = Color.White,
            modifier = Modifier.align(Alignment.BottomStart)
        )
    }
}