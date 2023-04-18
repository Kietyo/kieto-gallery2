package dev.kietyo.scrap.viewmodels

import android.content.SharedPreferences
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.edit
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.ViewModel
import dev.kietyo.scrap.GalleryItem
import dev.kietyo.scrap.log
import dev.kietyo.scrap.utils.AlignmentEnum
import dev.kietyo.scrap.utils.ContentScaleEnum
import dev.kietyo.scrap.utils.SharedPreferencesKeys
import dev.kietyo.scrap.utils.toGalleryItem
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import kotlin.math.max
import kotlin.math.min

const val MAX_COLUMNS = 10

@Serializable
data class GallerySettings(
    val numColumns: Int,
    val contentScaleEnum: ContentScaleEnum,
    val alignmentEnum: AlignmentEnum
)

class GalleryViewModel(
    private val sharedPreferences: SharedPreferences,
    private val executorService: ExecutorService,
    gallerySettings: GallerySettings =
        sharedPreferences.getString(SharedPreferencesKeys.GALLERY_SETTINGS, null)?.let {
            Json.decodeFromString(it)
        }
            ?: GallerySettings(
                3, ContentScaleEnum.CROP, AlignmentEnum.CENTER
            )
) : ViewModel() {
    val dispatcher = executorService.asCoroutineDispatcher()

    private var _currentFiles = MutableStateFlow(listOf<DocumentFile>())
    val currentFiles = _currentFiles.asStateFlow()
    val fileToGalleryItemCacheV2 = ConcurrentHashMap<DocumentFile, MutableState<GalleryItem?>>()

    fun updateCurrentFiles(newCurrentFiles: List<DocumentFile>) {
        _currentFiles.update {
            newCurrentFiles
        }

        newCurrentFiles.forEach {
            fileToGalleryItemCacheV2[it] = mutableStateOf(null)
        }

        GlobalScope.launch(dispatcher) {
            newCurrentFiles.forEach {
                fileToGalleryItemCacheV2[it]!!.value = it.toGalleryItem()
            }
        }
    }

    init {
        log("GalleryViewModel init: $gallerySettings")
    }

    val currentGallerySettings
        get() = GallerySettings(
            numColumnsFlow.value,
            imageContentScaleFlow.value,
            alignmentFlow.value
        )

    private val _numColumnsFlow = MutableStateFlow(gallerySettings.numColumns)
    val numColumnsFlow = _numColumnsFlow.asStateFlow()
    fun incrementColumns() = _numColumnsFlow.updateAndGet { min(it + 1, MAX_COLUMNS) }
    fun decrementColumns() = _numColumnsFlow.updateAndGet { max(it - 1, 1) }

    private val _imageContentScaleFlow = MutableStateFlow(gallerySettings.contentScaleEnum)
    val imageContentScaleFlow = _imageContentScaleFlow.asStateFlow()
    fun updateImageContentScale(newContentScale: ContentScaleEnum) {
        _imageContentScaleFlow.update {
            newContentScale
        }
    }

    private val _alignmentFlow = MutableStateFlow(gallerySettings.alignmentEnum)
    val alignmentFlow = _alignmentFlow.asStateFlow()
    fun updateAlignment(newAlignment: AlignmentEnum) {
        _alignmentFlow.update {
            newAlignment
        }
    }

    fun applyGallerySettings(gallerySettings: GallerySettings) {
        _numColumnsFlow.update { gallerySettings.numColumns }
        updateImageContentScale(gallerySettings.contentScaleEnum)
        updateAlignment(gallerySettings.alignmentEnum)
        sharedPreferences.edit {
            this.putString(
                SharedPreferencesKeys.GALLERY_SETTINGS,
                Json.encodeToString(currentGallerySettings)
            )
        }
    }


}