package com.example.visualdetection.Core.feature.Permission

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat


@Composable
fun PermissionDialog(
    permissionDialogTextProvider: PermissionDialogTextProvider,
    modifier: Modifier = Modifier,
    permission: String,
    isPermanentlyDeclined: Boolean,
    onDismiss: () -> Unit,
    onOKClick: () -> Unit,
    onGoToAppSettingsClick: () -> Unit,
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        confirmButton = {
            Column {
                HorizontalDivider()
                Text(
                    modifier = Modifier.clickable {
                        if (isPermanentlyDeclined) {
                            onGoToAppSettingsClick()
                        } else {
                            onOKClick()
                        }
                    }, text = if (isPermanentlyDeclined) "Grant Permission" else "OK"
                )
            }
        },
        title = { Text(text = "Permission Required!") },
        text = { Text(text = permissionDialogTextProvider.getDescription(isPermanentlyDeclined)) },
        dismissButton = {
            Text(
                modifier = Modifier.clickable { onDismiss() }, text = "Dismiss button"
            )

        },
    )
}

interface PermissionDialogTextProvider {
    fun getDescription(isPermanentlyDeclined: Boolean): String
}

class CameraPermissionTextProvider : PermissionDialogTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if (isPermanentlyDeclined) "Permission was PERMANENTLY declined." + "Please go to the settings to enable it." else "Needs camera permission"
    }
}

class RecordAudioPermissionTextProvider : PermissionDialogTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if (isPermanentlyDeclined) "Permission was PERMANENTLY declined." + "Please go to the settings to enable it." else "Needs audio permission"
    }
}