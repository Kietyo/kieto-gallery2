package dev.kietyo.scrap.utils

import android.content.ContentResolver
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.documentfile.provider.DocumentFile
import coil.request.ImageRequest
import dev.kietyo.scrap.GalleryItem
import dev.kietyo.scrap.di.MyApplication
import dev.kietyo.scrap.viewmodels.KDocumentModel

const val STRING_ACTIVITY_RESULT = "STRING_ACTIVITY_RESULT"
const val IMAGE_JPEG_MIME_TYPE = "image/jpeg"

fun KDocumentModel.toGalleryItem(contentResolver: ContentResolver): GalleryItem {
    val directory = this
    require(directory.isDirectory)

    val firstImageFileInDirectory = directory.getFirstImageFileInDirectoryOrNull(contentResolver)
    println(firstImageFileInDirectory)
    return if (firstImageFileInDirectory == null) {
        GalleryItem.Folder(directory.name)
    } else {
        GalleryItem.FolderWithAsyncImage(
            directory.name,
            ImageRequest.Builder(MyApplication.getAppContext())
                .data(firstImageFileInDirectory.uri).build()
        )
    }
}


interface DisplayTextI {
    val displayText: String
}

enum class ContentScaleEnum(
    override val displayText: String,
    val contentScale: ContentScale
): DisplayTextI {
    CROP("Crop", ContentScale.Crop),
    FIT("Fit", ContentScale.Fit),
    FILL_HEIGHT("Fill Height", ContentScale.FillHeight),
    FILL_WIDTH("Fill Width", ContentScale.FillWidth),
    FILL_BOUNDS("Fill Bounds", ContentScale.FillBounds),
    INSIDE("Inside", ContentScale.Inside),
    NONE("None", ContentScale.None);
}

val contentScales = ContentScaleEnum.values().asSequence().sortedBy { it.displayText }.toList()

enum class AlignmentEnum(
    override val displayText: String,
    val alignment: Alignment
): DisplayTextI {
    TOP("Top Center", Alignment.TopCenter),
    CENTER("Center", Alignment.Center),
    BOTTOM_CENTER("Bottom Center", Alignment.BottomCenter),
}

val alignments = AlignmentEnum.values().toList()

object SharedPreferencesDbs {
    const val GALLERY = "gallery"
}

object SharedPreferencesKeys {
    const val GALLERY_SETTINGS = "gallery_settings"
    const val SELECTED_FOLDER = "selected_folder"
}

object NavDestinations {
    const val GALLERY = "gallery"
    const val IMAGES = "images"
}