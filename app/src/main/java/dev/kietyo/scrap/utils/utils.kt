package dev.kietyo.scrap.utils

import androidx.documentfile.provider.DocumentFile

val DocumentFile.isImage: Boolean
    get() {
        return type == "image/jpeg"
    }

const val STRING_ACTIVITY_RESULT = "STRING_ACTIVITY_RESULT"