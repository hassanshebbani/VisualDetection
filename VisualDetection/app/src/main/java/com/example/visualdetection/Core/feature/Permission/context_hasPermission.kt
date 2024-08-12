package com.example.visualdetection.Core.feature.Permission

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.example.visualdetection.Core.data.MyAppPermissions

@RequiresApi(Build.VERSION_CODES.Q)
fun Context.hasPermission(permission: MyAppPermissions): Boolean = when (permission) {
    MyAppPermissions.ACTIVITY_RECOGNITION -> ContextCompat.checkSelfPermission(
        this, Manifest.permission.ACTIVITY_RECOGNITION
    ) == PackageManager.PERMISSION_GRANTED

    MyAppPermissions.INTERNET -> ContextCompat.checkSelfPermission(
        this, Manifest.permission.INTERNET
    ) == PackageManager.PERMISSION_GRANTED

    MyAppPermissions.CAMERA -> ContextCompat.checkSelfPermission(
        this, Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED

    MyAppPermissions.LOCATION -> ContextCompat.checkSelfPermission(
        this, Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
        this, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    MyAppPermissions.NOTIFICATIONS -> ContextCompat.checkSelfPermission(
        this, Manifest.permission.POST_NOTIFICATIONS
    ) == PackageManager.PERMISSION_GRANTED

    MyAppPermissions.RECORD_AUDIO -> ContextCompat.checkSelfPermission(
        this, Manifest.permission.RECORD_AUDIO
    ) == PackageManager.PERMISSION_GRANTED

    MyAppPermissions.WRITE_EXTERNAL_STORAGE -> ContextCompat.checkSelfPermission(
        this, Manifest.permission.WRITE_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED

    MyAppPermissions.READ_EXTERNAL_STORAGE -> ContextCompat.checkSelfPermission(
        this, Manifest.permission.READ_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED

    MyAppPermissions.FOREGROUND_SERVICE -> ContextCompat.checkSelfPermission(
        this, Manifest.permission.FOREGROUND_SERVICE
    ) == PackageManager.PERMISSION_GRANTED && (ContextCompat.checkSelfPermission(
        this, Manifest.permission.FOREGROUND_SERVICE_HEALTH
    ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
        this, Manifest.permission.FOREGROUND_SERVICE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED)

    MyAppPermissions.ALARM -> ContextCompat.checkSelfPermission(
        this, Manifest.permission.SCHEDULE_EXACT_ALARM
    ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
        this, Manifest.permission.USE_EXACT_ALARM
    ) == PackageManager.PERMISSION_GRANTED
}
