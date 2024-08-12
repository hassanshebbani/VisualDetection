package com.example.visualdetection.Core.presentation.viewmodel

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.util.Size
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.compose.runtime.mutableStateListOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.RESULT_FORMAT_JPEG
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.SCANNER_MODE_BASE
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.SCANNER_MODE_BASE_WITH_FILTER
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.SCANNER_MODE_FULL
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CoreVM : ViewModel() {


    private val _camPreviewSize = MutableStateFlow(Size(0, 0))
    val camPreviewSize = _camPreviewSize.asStateFlow()

    fun setCamPreviewSize(size: Size) {
        _camPreviewSize.update {
            size
        }
    }



    val options = GmsDocumentScannerOptions.Builder()
        .setScannerMode(SCANNER_MODE_FULL)
//        .setScannerMode(SCANNER_MODE_BASE)
//        .setScannerMode(SCANNER_MODE_BASE_WITH_FILTER)
        .setGalleryImportAllowed(false)
        .setPageLimit(2)
        .setResultFormats(RESULT_FORMAT_JPEG)
        .build()

    val scanner = GmsDocumentScanning.getClient(options)

    var scannerActivity: Activity? = null

    fun setActivity(activity: Activity) {
        scannerActivity = activity
    }


    fun startScannerIntent(
        scannerLauncher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>,
        onFailureCB: (() -> Unit)? = null
    ) {
        if (scannerActivity != null)
            scanner.getStartScanIntent(scannerActivity!!)
                .addOnSuccessListener {
                    scannerLauncher.launch(IntentSenderRequest.Builder(it).build())
                }.addOnFailureListener {
                    onFailureCB?.invoke()
                }
    }


    private val _takeVideo = MutableStateFlow(false)
    val takeVideo = _takeVideo.asStateFlow()

    fun setTakeVideo(startRecording: Boolean) {
        _takeVideo.update {
            startRecording
        }
    }


    private val _scannedDocs = MutableStateFlow<List<Uri>>(emptyList())
    val scannedDocs = _scannedDocs.asStateFlow()

    fun setScannedDocs(docs: List<Uri>) {
        _scannedDocs.update {
            docs
        }
    }

    private val _bitmaps = MutableStateFlow<Map<String, Bitmap?>>(emptyMap())
    fun onTakePhoto(bitmap: Bitmap, uri: Uri?) {
        uri?.let { uri ->
            _bitmaps.update {
                it + mapOf(uri.toString() to bitmap)
            }
        }
    }


    val visiblePermissionDialogQueue = mutableStateListOf<String>()

    fun dismissDialog() {
        visiblePermissionDialogQueue.removeFirst()
    }

    fun onPermissionResult(permission: String, isGranted: Boolean) {
        if (!isGranted && !visiblePermissionDialogQueue.contains(permission)) {
            visiblePermissionDialogQueue.add(permission)
        }
    }

    fun hasRequiredPermissions(context: Context, permissionsToCheck: List<String>): Boolean {
        return permissionsToCheck.all {
            ContextCompat.checkSelfPermission(
                context.applicationContext, it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

}
