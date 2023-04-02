package dev.kietyo.scrap.viewmodels

import androidx.compose.ui.layout.ContentScale
import androidx.lifecycle.ViewModel
import dev.kietyo.scrap.log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GalleryViewModel: ViewModel() {
    init {
        log("GalleryViewModel init")
    }
    private val _imageContentScaleFlow = MutableStateFlow(ContentScale.Crop)
    val imageContentSscaleFlow = _imageContentScaleFlow.asStateFlow()
    fun updateImageContentScale(newContentScale: ContentScale) {
        _imageContentScaleFlow.update {
            newContentScale
        }
    }
}