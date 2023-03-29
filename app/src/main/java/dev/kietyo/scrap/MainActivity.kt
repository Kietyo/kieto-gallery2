package dev.kietyo.scrap

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.graphics.decodeBitmap
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import dagger.hilt.android.AndroidEntryPoint
import dev.kietyo.scrap.ui.theme.AndroidComposeTemplateTheme
import dev.marcocattaneo.androidcomposetemplate.R

val REQUEST_CODE_SELECT_FOLDER = 100
val MY_PREFERENCES = "MyPreferences"

val PREFERENCE_SELECTED_FOLDER = "PREFERENCE_SELECTED_FOLDER"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 10

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
            );
        }

        setContent {
            HelloWorldContent(contentResolver)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun HelloWorldContent(contentResolver: ContentResolver) {
    val context = LocalContext.current

    val datastore = context.getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE)

    val savedUri = datastore.getString(PREFERENCE_SELECTED_FOLDER, null)

    log("Received saved URI: $savedUri")

    val selectedFolder = remember { mutableStateOf<Uri?>(savedUri?.toUri()) }

    val openFolderLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocumentTree()
    ) { uri: Uri? ->
        if (uri != null) {
            contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            selectedFolder.value = uri
            datastore.edit {
                this.putString(PREFERENCE_SELECTED_FOLDER, uri?.toString())
            }
        }
    }

    val scrollState = ScrollState(0)
    

    AndroidComposeTemplateTheme {
        Column(modifier = Modifier.verticalScroll(scrollState)) {
            Text(text = "Hello world!", color = Color.Red)
            Button(onClick = {
                openFolderLauncher.launch(null)
            }) {
                Text("Load Folder")
            }

            selectedFolder.value?.let { uri ->
                Text("Selected path: $uri")
                Text("uri.path: ${uri.path}")
                Text("uri.encodedPath: ${uri.encodedPath}")

                val documentFile = DocumentFile.fromTreeUri(context, uri)
                documentFile?.let { file ->
                    log(file.name)
                    log(file.type)
                    log("Is file: ${file.isFile}")
                    log("Is directory: ${file.isDirectory}")

                    log("Files inside directory:")
                    log(file.listFiles().map { "${it.name}, isFile: ${it.isFile}, ${it.uri.path}" }
                        .joinToString("\n"))

                    file.listFiles().filterNotNull().forEach {
                        if (it.isFile) {
                            val bitmap = ImageDecoder.createSource(contentResolver, it.uri)
                                .decodeBitmap { info, source ->
                                }
                            Image(
                                painter = BitmapPainter(bitmap.asImageBitmap()),
                                contentDescription = it.name
                                    ?: "",
                                contentScale = ContentScale.FillWidth,
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            Text(text = "Directory: ${it.name ?: ""}")
                        }

                    }
                }
            }

        }

    }
}

fun log(content: Any?) {
    Log.d("KietHere", content.toString())
}

@Composable
fun GalleryView() {
    LazyVerticalGrid(columns = GridCells.Fixed(3),
    modifier = Modifier.background(Color.Black)
        ) {
        item {
            FolderItem(item = GalleryItem.Folder("Item 1 asdasd asdasd asdasda sadasd asdadad asdasd"))
        }
        item {
            FolderItem(item = GalleryItem.Folder("Item 2"))
        }
        item {
            FolderItem(item = GalleryItem.Folder("Item 3"))
        }
        item {
            FolderItem(item = GalleryItem.Folder("Item 4"))
        }
    }
}

@Preview
@Composable
fun GalleryViewPreview() {
    GalleryView()
}

sealed class GalleryItem {
    data class Folder(val name: String): GalleryItem()
    data class Image(val imageBitmap: ImageBitmap): GalleryItem()
}

@Composable
fun FolderItem(item: GalleryItem.Folder) {
    AndroidComposeTemplateTheme {
        Box(
            Modifier
                .background(Color.Black)
                .fillMaxSize()
                .padding(3.dp)
                .aspectRatio(1.0f)
                ) {
            val text = item.name
            Text(text = text,
                color = Color.White,
                modifier = Modifier
                    .wrapContentSize()
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(Color.DarkGray)
                    .padding(2.dp)
            )
        }
    }
}

@Preview
@Composable
fun FolderItemPreview() {
    FolderItem(GalleryItem.Folder("Hello world how are you today."))
}

@Composable
fun ImageItem(imageBitmap: ImageBitmap) {
    Box() {
        Image(
            BitmapPainter(imageBitmap),
            "a picture",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

//@Preview
//@Composable
//fun ImageItemPreview() {
//    val painter = rememberVectorPainter(image = )
//    ImageItem(imageBitmap = )
//}