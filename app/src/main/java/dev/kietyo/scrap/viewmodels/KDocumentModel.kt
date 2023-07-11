package dev.kietyo.scrap.viewmodels

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.MediaStore
import dev.kietyo.scrap.log
import dev.kietyo.scrap.utils.IMAGE_JPEG_MIME_TYPE

data class KDocumentModel(
    val uri: Uri,
    val mimeType: String,
    val name: String
) {
    val isImage get() = mimeType == IMAGE_JPEG_MIME_TYPE
    val isDirectory get() = mimeType == DocumentsContract.Document.MIME_TYPE_DIR

    fun listFiles(contentResolver: ContentResolver): List<KDocumentModel> {
        return resolverQuery(contentResolver, uri)
    }

    fun getFirstImageFileInDirectoryOrNull(contentResolver: ContentResolver): KDocumentModel? {
        val childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(
            uri,
            DocumentsContract.getDocumentId(uri)
        )
        var result: KDocumentModel? = null
        var c: Cursor? = null
        try {
            c = contentResolver.query(
                childrenUri, arrayOf(
                    DocumentsContract.Document.COLUMN_DOCUMENT_ID,
                    DocumentsContract.Document.COLUMN_MIME_TYPE,
                    DocumentsContract.Document.COLUMN_DISPLAY_NAME,
                ), "${DocumentsContract.Document.COLUMN_MIME_TYPE} like ?",
                arrayOf("image/%"),
                "${DocumentsContract.Document.COLUMN_DISPLAY_NAME} ASC"
            )
            c?.moveToFirst()
            while (c!!.moveToNext() && result == null) {
                val documentId = c.getString(0)
                val mimeType = c.getString(1)
                val columnDisplayName = c.getString(2)
                val documentUri = DocumentsContract.buildDocumentUriUsingTree(
                    uri,
                    documentId
                )
                result = KDocumentModel(documentUri, mimeType, columnDisplayName)
            }
        } catch (e: Exception) {
            log("Failed query: $e")
        } finally {
            c?.close()
        }
        return result
    }
}