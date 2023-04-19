package dev.kietyo.scrap.viewmodels

import android.app.Application
import android.content.ContentResolver
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.edit
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.kietyo.scrap.GalleryItem
import dev.kietyo.scrap.di.MyApplication
import dev.kietyo.scrap.log
import dev.kietyo.scrap.utils.AlignmentEnum
import dev.kietyo.scrap.utils.ContentScaleEnum
import dev.kietyo.scrap.utils.SharedPreferencesKeys
import dev.kietyo.scrap.utils.toGalleryItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.mapNotNull
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
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

const val MAX_COLUMNS = 10

@Serializable
data class GallerySettings(
    val numColumns: Int,
    val contentScaleEnum: ContentScaleEnum,
    val alignmentEnum: AlignmentEnum
)

class GalleryViewModel(
    private val application: Application,
    private val sharedPreferences: SharedPreferences,
    private val executorService: ExecutorService,
    gallerySettings: GallerySettings =
        sharedPreferences.getString(SharedPreferencesKeys.GALLERY_SETTINGS, null)?.let {
            Json.decodeFromString(it)
        }
            ?: GallerySettings(
                3, ContentScaleEnum.CROP, AlignmentEnum.CENTER
            )
) : AndroidViewModel(application) {
    private val contentResolver: ContentResolver = application.contentResolver

    private val _selectedFolder = MutableStateFlow(
        sharedPreferences.getString(SharedPreferencesKeys.SELECTED_FOLDER, null).apply {
            log("Got selected folder: $this")
        }?.toUri()
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val documentFile = _selectedFolder.mapLatest {
        _selectedFolder.value?.let {
            DocumentFile.fromTreeUri(application.baseContext, it)
        }?.apply {
            log("document file name: $name, type: $type, is file: $isFile, is directory: $isDirectory")
        }
    }

    @OptIn(ExperimentalTime::class)
    val listFiles = documentFile.map { file ->
        if (file == null) {
            emptyList<DocumentFile>()
        } else {
            val listFiles = measureTimedValue {
                file.listFiles().filter {
                    it.isDirectory
                }
            }
            log("Time to list files:  ${listFiles.duration}")
            listFiles.value
        }
    }

    fun updateSelectedFolder(uri: Uri?) {
        if (uri != null) {
            log("Updating folder: $uri")
            contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)

            sharedPreferences.edit {
                this.putString(SharedPreferencesKeys.SELECTED_FOLDER, uri.toString())
            }

            _selectedFolder.update {
                uri
            }
        }
    }

    init {
        log("Received saved URI: ${_selectedFolder.value}")
    }

    private val ioScope = CoroutineScope(Dispatchers.IO)

    private val dispatcher = executorService.asCoroutineDispatcher()

    private val _currentFiles = MutableStateFlow(listOf<DocumentFile>())
    val currentFiles = _currentFiles.asStateFlow()
    val fileToGalleryItemCacheV2 = ConcurrentHashMap<DocumentFile, MutableState<GalleryItem?>>()
    var loadImagesJob: Job? = null

    @OptIn(DelicateCoroutinesApi::class)
    fun updateCurrentFiles(newCurrentFiles: List<DocumentFile>) {
        _currentFiles.update {
            newCurrentFiles
        }

        newCurrentFiles.forEach {
            fileToGalleryItemCacheV2[it] = mutableStateOf(null)
        }

        loadImagesJob = ioScope.launch(dispatcher) {
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