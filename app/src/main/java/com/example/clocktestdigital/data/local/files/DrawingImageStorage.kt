package com.example.clocktestdigital.data.files

import android.content.Context
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream

fun saveDrawingBitmapToInternalStorage(
    context: Context,
    bitmap: Bitmap,
    patientCode: String,
    timestamp: Long
): String {
    val directory = File(context.filesDir, "drawings")

    if (!directory.exists()) {
        directory.mkdirs()
    }

    val safePatientCode = patientCode.replace(Regex("[^A-Za-z0-9_-]"), "_")
    val file = File(directory, "clock_${safePatientCode}_$timestamp.png")

    FileOutputStream(file).use { outputStream ->
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
    }

    return file.absolutePath
}