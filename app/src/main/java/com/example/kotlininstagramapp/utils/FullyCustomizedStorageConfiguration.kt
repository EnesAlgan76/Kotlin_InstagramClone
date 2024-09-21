package com.example.kotlininstagramapp.utils

import android.content.Context
import com.abedelazizshe.lightcompressorlibrary.config.StorageConfiguration
import java.io.File

class FullyCustomizedStorageConfiguration(
) : StorageConfiguration {
    override fun createFileToSave(
        context: Context,
        videoFile: File,
        fileName: String,
        shouldSave: Boolean
    ): File {
        return File(context.cacheDir, "compressed_video.mp4")
    }
}