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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
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
import coil.ImageLoader
import dagger.hilt.android.AndroidEntryPoint
import dev.kietyo.scrap.activities.ImageSettingsContent
import dev.kietyo.scrap.compose.FolderItem
import dev.kietyo.scrap.compose.GalleryViewV2
import dev.kietyo.scrap.compose.Header
import dev.kietyo.scrap.ui.theme.AndroidComposeTemplateTheme
import dev.kietyo.scrap.utils.STRING_ACTIVITY_RESULT
import dev.kietyo.scrap.utils.toGalleryItem
import dev.kietyo.scrap.viewmodels.GalleryViewModel
import kotlin.time.ExperimentalTime

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

        val imageLoader = ImageLoader.Builder(baseContext).build()

        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            log("Got activity result")
            log(it.resultCode)
            log(it.data?.getStringExtra(STRING_ACTIVITY_RESULT))
        }

        setContent {
            MainContent(galleryViewModel, contentResolver)
        }
    }

}

@OptIn(ExperimentalTime::class)
@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun MainContent(
    galleryViewModel: GalleryViewModel,
    contentResolver: ContentResolver
) {
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

    AndroidComposeTemplateTheme {
        Column {
            ExpandableHeader(galleryViewModel, openFolderLauncher)

            documentFile?.let { file ->
                val galleryItems = file.listFiles().map { it.toGalleryItem() }
                GalleryViewV2(galleryViewModel, galleryItems) }
        }
    }
}

@Composable
fun ExpandableHeader(
    galleryViewModel: GalleryViewModel,
    openFolderLauncher: ActivityResultLauncher<Uri?>
) {
    var isSettingsOpen by remember {
        mutableStateOf(false)
    }
    if (isSettingsOpen) {
        ImageSettingsContent(
            galleryViewModel = galleryViewModel,
            onCancelButtonClick = {
                isSettingsOpen = false
            }
        )
    } else {
        Header(
            onLoaderFolderClick = {
                openFolderLauncher.launch(null)
            }
        ) {
            isSettingsOpen = true
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