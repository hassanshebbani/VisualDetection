package com.example.visualdetection

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import com.example.visualdetection.Core.feature.Permission.CameraPermissionTextProvider
import com.example.visualdetection.Core.feature.Permission.PermissionDialog
import com.example.visualdetection.Core.feature.Permission.RecordAudioPermissionTextProvider
import com.example.visualdetection.Core.presentation.navigation.AppNavigation
import com.example.visualdetection.Core.presentation.viewmodel.CoreVM
import com.example.visualdetection.ui.theme.VisualDetectionTheme
import com.example.visualdetection.Core.wrappers.VMWrapper
import com.example.visualdetection.ML.presentation.viewmodel.MLVM
import com.google.mlkit.vision.documentscanner.GmsDocumentScanner
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.RESULT_FORMAT_JPEG
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.SCANNER_MODE_FULL
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mlVM = ViewModelProvider(this).get(MLVM::class.java)
        val coreVM = ViewModelProvider(this).get(CoreVM::class.java)
        val vmWrapper = VMWrapper(
            coreViewModel = coreVM,
            mlViewModel = mlVM
        )
        vmWrapper.coreVM.setActivity(this@MainActivity)



        enableEdgeToEdge()
        setContent {
            VisualDetectionTheme {
                val dialogQueue = coreVM.visiblePermissionDialogQueue
                val permissionsToRequest = arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.MANAGE_EXTERNAL_STORAGE

                    )

                val multiplePermissionResultLauncher =
                    rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestMultiplePermissions(),
                        onResult = { permissions ->
                            permissionsToRequest.forEach {
                                coreVM.onPermissionResult(
                                    permission = it, isGranted = permissions[it] == true
                                )
                            }
                        })



                AppNavigation(vmWrapper = vmWrapper) {
                    dialogQueue.reversed().forEach { permission ->
                        PermissionDialog(
                            permissionDialogTextProvider = when (permission) {
                                Manifest.permission.CAMERA -> CameraPermissionTextProvider()
                                Manifest.permission.RECORD_AUDIO -> RecordAudioPermissionTextProvider()
                                else -> return@forEach
                            },
                            permission = permission,
                            isPermanentlyDeclined = !shouldShowRequestPermissionRationale(permission),
                            onDismiss = coreVM::dismissDialog,
                            onOKClick = {
                                coreVM.dismissDialog()
                                multiplePermissionResultLauncher.launch(
                                    arrayOf(permission)
                                )
                            },
                            onGoToAppSettingsClick = ::openAppSettings
                        )
                    }
                    multiplePermissionResultLauncher.launch(permissionsToRequest)
                }
            }
        }
    }
}

fun Activity.openAppSettings() {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", packageName, null)
    ).also(::startActivity)
}
