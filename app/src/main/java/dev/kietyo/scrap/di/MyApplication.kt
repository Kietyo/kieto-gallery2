
package dev.kietyo.scrap.di

import android.content.Context
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import dagger.hilt.android.HiltAndroidApp
import dev.kietyo.scrap.BaseApplication
import dev.kietyo.scrap.log

@HiltAndroidApp
open class MyApplication: BaseApplication(), ImageLoaderFactory {
    companion object {
        private lateinit var instance: MyApplication

        fun getAppContext(): Context = instance.applicationContext
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    override fun newImageLoader(): ImageLoader {
        log("Loading ImageLoader")
        return ImageLoader.Builder(applicationContext)
            .diskCache {
                DiskCache.Builder()
                    .directory(applicationContext.cacheDir.resolve("image_cache"))
                    .maxSizePercent(0.2)
                    .build()
            }
            .build()
    }
}