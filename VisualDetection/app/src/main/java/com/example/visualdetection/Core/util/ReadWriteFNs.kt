package com.example.visualdetection.Core.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.visualdetection.Core.data.MyFileTypes
import java.io.BufferedWriter
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val fileTypesForMediaToScan =
    listOf<MyFileTypes>(MyFileTypes.IMAGE, MyFileTypes.VIDEO, MyFileTypes.AUDIO)

private fun getDirByFileType(fileType: MyFileTypes) = when (fileType) {
    MyFileTypes.IMAGE -> Environment.DIRECTORY_PICTURES
    MyFileTypes.VIDEO -> Environment.DIRECTORY_MOVIES
    MyFileTypes.AUDIO -> Environment.DIRECTORY_DOWNLOADS
    MyFileTypes.TXT -> Environment.DIRECTORY_DOWNLOADS
    MyFileTypes.JSON -> Environment.DIRECTORY_DOWNLOADS
}

private fun getMIMEByFileType(fileType: MyFileTypes) = when (fileType) {
    MyFileTypes.IMAGE -> "image/jpeg"
    MyFileTypes.VIDEO -> ""
    MyFileTypes.AUDIO -> ""
    MyFileTypes.TXT -> ""
    MyFileTypes.JSON -> "application/json"
}

@RequiresApi(Build.VERSION_CODES.Q)
private fun getResolverUriByFileType(fileType: MyFileTypes) = when (fileType) {
    MyFileTypes.IMAGE -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    MyFileTypes.VIDEO -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    MyFileTypes.AUDIO -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    MyFileTypes.TXT -> MediaStore.Downloads.EXTERNAL_CONTENT_URI
    MyFileTypes.JSON -> MediaStore.Downloads.EXTERNAL_CONTENT_URI
}

private fun <T> resolveOutputStreamByFileType(
    fileType: MyFileTypes,
    file: T,
    outputStream: OutputStream
) {
    when (fileType) {
        MyFileTypes.IMAGE -> (file as Bitmap).compress(
            Bitmap.CompressFormat.JPEG, 100, outputStream
        )
        MyFileTypes.VIDEO -> {}
        MyFileTypes.AUDIO -> {}
        MyFileTypes.TXT -> {}
        MyFileTypes.JSON -> outputStream.write((file as String).toByteArray())
    }
}

fun <T> saveFileToStorage(
    context: Context, file: T, fileType: MyFileTypes, filename: String? = null, dir: String? = null
): Uri? {
    val fileName = filename ?: "${fileType.name}_${
        SimpleDateFormat(
            "yyyyMMdd_HHmmss", Locale.getDefault()
        ).format(Date())
    }"
    var savedFileUri: Uri? = null
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        savedFileUri = saveFileUsingSAF(
            context = context,
            file = file,
            filename = fileName,
            dir = dir,
            fileType = fileType
        )
    } else {
        savedFileUri = saveFileLegacy(
            context = context,
            file = file,
            filename = fileName,
            dir = dir,
            fileType = fileType
        )
    }
    return savedFileUri
}

// api > 29
@RequiresApi(Build.VERSION_CODES.Q)
fun <T> saveFileUsingSAF(
    context: Context, filename: String? = null, dir: String? = null, fileType: MyFileTypes, file: T
): Uri? {

    val resolver = context.contentResolver
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
        put(MediaStore.MediaColumns.MIME_TYPE, getMIMEByFileType(fileType = fileType))
        put(MediaStore.MediaColumns.RELATIVE_PATH, dir ?: getDirByFileType(fileType))
    }

    val fileUri = resolver.insert(getResolverUriByFileType(fileType = fileType), contentValues)

    fileUri?.let {
        resolver.openOutputStream(it)?.use { outputStream ->
            try {
                resolveOutputStreamByFileType(
                    fileType = fileType,
                    file = file,
                    outputStream = outputStream
                )
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(context, "Error saving file ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        // Notify the MediaScanner to scan the new file
        if (fileType in fileTypesForMediaToScan) MediaScannerConnection.scanFile(
            context, arrayOf(File(fileUri.path).absolutePath), null
        ) { _, _ ->
            // Optionally, you can perform actions after scanning is complete
        }
        Toast.makeText(context, "file $fileType saved successfully", Toast.LENGTH_SHORT).show()
    }
    return fileUri
}

// api < 29
fun <T> saveFileLegacy(
    context: Context, filename: String? = null, dir: String? = null, fileType: MyFileTypes, file: T
): Uri? {
    val fileDir = Environment.getExternalStoragePublicDirectory(
        dir ?: getDirByFileType(fileType)
    )
    val f = File(fileDir, filename)

    FileOutputStream(f).use { outputStream ->
        try {
            resolveOutputStreamByFileType(
                fileType = fileType,
                file = file,
                outputStream = outputStream
            )
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, "Error saving file ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // Notify the system to scan the new file
    if (fileType in fileTypesForMediaToScan)
        context.sendBroadcast(
            Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(f))
        )

    return Uri.fromFile(f)
}
