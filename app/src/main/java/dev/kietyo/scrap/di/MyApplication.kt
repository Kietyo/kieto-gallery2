
package dev.kietyo.scrap.di

import android.content.Context
import dagger.hilt.android.HiltAndroidApp
import dev.kietyo.scrap.BaseApplication

@HiltAndroidApp
open class MyApplication: BaseApplication() {
    companion object {
        private lateinit var instance: MyApplication

        fun getAppContext(): Context = instance.applicationContext
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}