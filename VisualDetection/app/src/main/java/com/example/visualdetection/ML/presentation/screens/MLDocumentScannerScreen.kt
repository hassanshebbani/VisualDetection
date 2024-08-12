package com.example.visualdetection.ML.presentation.screens

import android.app.Activity.RESULT_OK
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.visualdetection.Core.wrappers.VMWrapper
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult


@Composable

fun MLDocumentScannerScreen(navController: NavController, vmWrapper: VMWrapper) {
    val scannedDocs by vmWrapper.coreVM.scannedDocs.collectAsState()


    val scannerLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartIntentSenderForResult()) {
            if (it.resultCode == RESULT_OK) {
                val result = GmsDocumentScanningResult.fromActivityResultIntent(it.data)
                vmWrapper.coreVM.setScannedDocs(result?.pages?.map { it.imageUri } ?: emptyList())
            }
        }
    LaunchedEffect(key1 = null) {
        vmWrapper.coreVM.startScannerIntent(scannerLauncher)
    }
    Column(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(20.dp)) {
        if (scannedDocs.isNotEmpty()) {
            Button(onClick = {
                vmWrapper.coreVM.startScannerIntent(scannerLauncher)
            }) {
                Text(text = "Scan Again")
            }
            scannedDocs.forEach { uri ->
                AsyncImage(
                    modifier = Modifier.fillMaxWidth(),
                    model = uri,
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth
                )
            }
        }
    }
}

