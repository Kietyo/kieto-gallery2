package dev.kietyo.scrap

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.kietyo.scrap.compose.FolderItem
import dev.kietyo.scrap.compose.GalleryView
import dev.kietyo.scrap.utils.NavDestinations
import dev.kietyo.scrap.utils.STRING_ACTIVITY_RESULT
import dev.kietyo.scrap.utils.SharedPreferencesDbs
import dev.kietyo.scrap.viewmodels.GalleryViewModel
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

const val REQUEST_CODE_SELECT_FOLDER = 100
private const val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 10

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var executorService: ExecutorService = Executors.newFixedThreadPool(4)

    private val factory = viewModelFactory {
        initializer {
            GalleryViewModel(
                application,
                baseContext.getSharedPreferences(SharedPreferencesDbs.GALLERY, MODE_PRIVATE),
                executorService
            )
        }
    }

    private val galleryViewModel: GalleryViewModel by viewModels(factoryProducer = { factory })

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

        val NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors()

        log("Num available cores: $NUMBER_OF_CORES")

        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            log("Got activity result")
            log(it.resultCode)
            log(it.data?.getStringExtra(STRING_ACTIVITY_RESULT))
        }

        log("Cache dir: ${baseContext.cacheDir}")


        setContent {
            MainScreen(galleryViewModel)
        }
    }

}

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun MainScreen(galleryViewModel: GalleryViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = NavDestinations.GALLERY) {
        log("navController.currentDestination: ${navController.currentDestination}")
        composable(NavDestinations.GALLERY) {
            GalleryView(galleryViewModel) {
                log("Navigating the image destination...")
                galleryViewModel.loadImagesJob?.cancel()
                log("Load images job cancelled...")
                navController.navigate(NavDestinations.IMAGES)
            }
        }
        composable(NavDestinations.IMAGES) {
            Text(text = "Hello world!")
            TextButton(onClick = { navController.navigate(NavDestinations.GALLERY) }) {
                Text(text = "Go back")
            }
        }
    }
}

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
    FolderItem(GalleryItem.Folder("Hello world how are you today."), {})
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