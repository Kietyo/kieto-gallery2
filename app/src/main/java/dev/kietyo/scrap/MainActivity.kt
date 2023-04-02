package dev.kietyo.scrap

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import coil.request.ImageRequest
import dagger.hilt.android.AndroidEntryPoint
import dev.kietyo.scrap.compose.FolderItem
import dev.kietyo.scrap.compose.GalleryView
import dev.kietyo.scrap.compose.GalleryViewV2
import dev.kietyo.scrap.compose.Header
import dev.kietyo.scrap.ui.theme.AndroidComposeTemplateTheme
import dev.kietyo.scrap.utils.isImage
import dev.kietyo.scrap.viewmodels.GalleryViewModel
import kotlin.streams.toList
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

const val REQUEST_CODE_SELECT_FOLDER = 100
const val MY_PREFERENCES = "MyPreferences"
const val PREFERENCE_SELECTED_FOLDER = "PREFERENCE_SELECTED_FOLDER"
private const val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 10

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val galleryViewModel: GalleryViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
            )
        }

        setContent {
            HelloWorldContent(galleryViewModel, contentResolver)
        }
    }
}

//val CitySaver = run {
//    listSaver<GalleryItem, Any>(
//        save = {
//        this
//    },
//    restore = {
//
//    })
//}

@OptIn(ExperimentalTime::class)
@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun HelloWorldContent(
    galleryViewModel: GalleryViewModel,
    contentResolver: ContentResolver) {
    val context = LocalContext.current

    val datastore = context.getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE)

    val savedUri = datastore.getString(PREFERENCE_SELECTED_FOLDER, null)

    log("Received saved URI: $savedUri")

    var selectedFolder by remember { mutableStateOf(savedUri?.toUri()) }
    var documentFile by remember {
        mutableStateOf(selectedFolder?.let {
            DocumentFile.fromTreeUri(context, it)
        })
    }

    val computeGalleryItems = {
        log("Computing gallery items...")
        documentFile?.let {
            val timeToProcessGalleryItems = measureTimedValue {
                it.listFiles().toList().parallelStream().filter { it.isDirectory }
                    .map { directory ->
                        val firstImageFileInDirectory = directory.listFiles().firstOrNull {
                            it.isImage
                        }
                        if (firstImageFileInDirectory == null) {
                            GalleryItem.Folder(directory.name!!)
                        } else {
                            GalleryItem.FolderWithAsyncImage(
                                directory.name!!,
                                ImageRequest.Builder(context)
                                    .data(firstImageFileInDirectory.uri).build()
                            )
                        }
                    }?.toList()
            }
            log("Time to process all folder items: ${timeToProcessGalleryItems.duration}")
            timeToProcessGalleryItems.value

        }
            ?: listOf()
    }
//    var galleryyItems by remember {
//        mutableStateOf(computeGalleryItems())
//    }

    val computeGalleryItemsV2 = {
        log("Computing gallery items V2...")
        documentFile?.let {
            val timeToProcessGalleryItems = measureTimedValue {
                it.listFiles().asSequence().filter { it.isDirectory }.toList()
            }
            log("Time to process all folder items: ${timeToProcessGalleryItems.duration}")
            timeToProcessGalleryItems.value

        }
            ?: listOf()
    }
//    var galleryItemsV2 by remember {
//        mutableStateOf(computeGalleryItemsV2())
//    }

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
                this.putString(PREFERENCE_SELECTED_FOLDER, uri.toString())
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

    val scrollState = ScrollState(0)

    val contentScales = listOf(
        ContentScaleSelection("Crop", ContentScale.Crop),
        ContentScaleSelection("Fit", ContentScale.Fit),
        ContentScaleSelection("Fill Height", ContentScale.FillHeight),
        ContentScaleSelection("Fill Width", ContentScale.FillWidth),
        ContentScaleSelection("Inside", ContentScale.Inside),
        ContentScaleSelection("Fill Bounds", ContentScale.FillBounds),
        ContentScaleSelection("None", ContentScale.None),
    ).sortedBy { it.text }


    AndroidComposeTemplateTheme {
        Column {
            Header(
                contentScales.first(),
                contentScales,
                onLoaderFolderClick = {
                    openFolderLauncher.launch(null)
                }, onContentScaleSelection = {
                    galleryViewModel.updateImageContentScale(it.contentScale)
                })
            documentFile?.let { GalleryViewV2(galleryViewModel, it) }
        }
    }
}

data class ContentScaleSelection(
    val text: String,
    val contentScale: ContentScale
)

fun log(content: Any?) {
    Log.d("KietHere", content.toString())
}

//@Preview
//@Composable
//fun GalleryViewPreview() {
//    GalleryView(mutableListOf<GalleryItem>().apply {
//        repeat(9) {
//            add(GalleryItem.Folder("Item $it"))
//        }
//    }.asSequence(), ContentScale.Fit)
//}

@Preview
@Composable
fun FolderItemPreview() {
    FolderItem(GalleryItem.Folder("Hello world how are you today."))
}

@Composable
fun ImageItem(painter: Painter) {
    Box(
        Modifier
            .background(Color.Red)
            .aspectRatio(1.0f)
            .fillMaxSize()
    ) {
        Image(
            painter,
            "a picture",
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

//@Preview
//@Composable
//fun ImageItemPreview() {
//    val blah = rememberAsyncImagePainter(model = R.drawable.example)
//    //    val painter = rememberAsyncImagePainter(model = "https://fastly.picsum.photos/id/302/200/300.jpg?hmac=b5e6gUSooYpWB3rLAPrDpnm8PsPb84p_NXRwD-DK-1I")
//    //    val blah = ImageRequest.Builder(LocalContext.current)
//    //        .data("https://picsum.photos/200").build()
//
//    ImageItem(painter = blah)
//}