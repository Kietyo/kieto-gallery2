package dev.kietyo.scrap.utils

import androidx.documentfile.provider.DocumentFile

val DocumentFile.isImage: Boolean
    get() {
        return type == "image/jpeg"
    }