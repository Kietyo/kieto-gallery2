package dev.kietyo.scrap.viewmodels

import androidx.lifecycle.ViewModel
import dev.kietyo.scrap.log
import dev.kietyo.scrap.utils.AlignmentEnum
import dev.kietyo.scrap.utils.ContentScaleEnum
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlin.math.max
import kotlin.math.min

const val MAX_COLUMNS = 10

data class GallerySettings(
    val numColumns: Int,
    val contentScaleEnum: ContentScaleEnum,
    val alignmentEnum: AlignmentEnum
)

class GalleryViewModel: ViewModel() {
    init {
        log("GalleryViewModel init")
    }

    val currentGallerySettings get() = GallerySettings(
        numColumnsFlow.value,
        imageContentScaleFlow.value,
        alignmentFlow.value)

    private val _numColumnsFlow = MutableStateFlow(3)
    val numColumnsFlow = _numColumnsFlow.asStateFlow()
    fun incrementColumns() = _numColumnsFlow.updateAndGet { min(it + 1, MAX_COLUMNS) }
    fun decrementColumns() = _numColumnsFlow.updateAndGet { max(it - 1, 1) }

    private val _imageContentScaleFlow = MutableStateFlow(ContentScaleEnum.CROP)
    val imageContentScaleFlow = _imageContentScaleFlow.asStateFlow()
    fun updateImageContentScale(newContentScale: ContentScaleEnum) {
        _imageContentScaleFlow.update {
            newContentScale
        }
    }

    private val _alignmentFlow = MutableStateFlow(AlignmentEnum.CENTER)
    val alignmentFlow = _alignmentFlow.asStateFlow()
    fun updateAlignment(newAlignment: AlignmentEnum) {
        _alignmentFlow.update {
            newAlignment
        }
    }

}