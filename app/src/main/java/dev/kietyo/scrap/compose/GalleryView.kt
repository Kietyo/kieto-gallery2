package dev.kietyo.scrap.compose

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.edit
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import dev.kietyo.scrap.log
import dev.kietyo.scrap.ui.theme.AndroidComposeTemplateTheme
import dev.kietyo.scrap.utils.SharedPreferencesDbs
import dev.kietyo.scrap.utils.SharedPreferencesKeys
import dev.kietyo.scrap.viewmodels.GalleryViewModel
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

@OptIn(ExperimentalTime::class)
@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun GalleryView(
    galleryViewModel: GalleryViewModel,
    contentResolver: ContentResolver,
    onImageClick: () -> Unit
) {
    val context = LocalContext.current
    val datastore = context.getSharedPreferences(SharedPreferencesDbs.GALLERY, Context.MODE_PRIVATE)
    val savedUri = datastore.getString(SharedPreferencesKeys.GALLERY_SETTINGS, null)

    log("Cache dir: ${context.cacheDir}")
    log("Received saved URI: $savedUri")

    var selectedFolder by remember { mutableStateOf(savedUri?.toUri()) }
    var documentFile by rememberSaveable {
        mutableStateOf(selectedFolder?.let {
            DocumentFile.fromTreeUri(context, it)
        })
    }

    val openFolderLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocumentTree()
    ) { uri: Uri? ->
        if (uri != null) {
            log("Updating folder...")
            contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            selectedFolder = uri
            selectedFolder?.let { uri ->
                log("kiet here")
                log("Selected path: $uri")
                log("uri.path: ${uri.path}")
                log("uri.encodedPath: ${uri.encodedPath}")
            }

            datastore.edit {
                this.putString(SharedPreferencesKeys.SELECTED_FOLDER, uri.toString())
            }

            documentFile = DocumentFile.fromTreeUri(context, uri)
            documentFile?.let { file ->
                log(file.name)
                log(file.type)
                log("Is file: ${file.isFile}")
                log("Is directory: ${file.isDirectory}")

                //                    log("Files inside directory:")
                //                    file.listFiles().forEach {
                //                        log("${it.name}, isFile: ${it.isFile}, ${it.uri.path}")
                //                        if (it.isDirectory) {
                //                            it.listFiles().forEach { childFile ->
                //                                log("\t${childFile.name}, isFile: ${childFile.isFile}, childFile.getType(): ${childFile.getType()}, ${childFile.uri.path}")
                //                            }
                //                        }
                //                    }
            }

            //            galleryItems = computeGalleryItems()
        }
    }

    AndroidComposeTemplateTheme {
        Column {
            ExpandableHeader(galleryViewModel, openFolderLauncher)

            documentFile?.let { file ->
                val listFiles = measureTimedValue {
                    file.listFiles().filter {
                        it.isDirectory
                    }
                }
                log("Time to list files:  ${listFiles.duration}")
                galleryViewModel.updateCurrentFiles(listFiles.value)

                GalleryViewV2(galleryViewModel, onImageClick)
            }
        }
    }
}