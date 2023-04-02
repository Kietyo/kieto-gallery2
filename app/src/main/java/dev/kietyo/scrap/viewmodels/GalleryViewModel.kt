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
    private val _numColumnsFlow = MutableStateFlow(3)
    val numColumnsFlow = _numColumnsFlow.asStateFlow()

    private val _imageContentScaleFlow = MutableStateFlow(ContentScale.Crop)
    val imageContentScaleFlow = _imageContentScaleFlow.asStateFlow()
    fun updateImageContentScale(newContentScale: ContentScale) {
        _imageContentScaleFlow.update {
            newContentScale
        }
    }
}