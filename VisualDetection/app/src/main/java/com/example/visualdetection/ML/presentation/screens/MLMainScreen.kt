package com.example.visualdetection.ML.presentation.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.visualdetection.Core.navigation.AppScreens
import com.example.visualdetection.Core.wrappers.VMWrapper
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.RESULT_FORMAT_JPEG
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.SCANNER_MODE_FULL
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning


@Composable
fun MLMainScreen(navController: NavController, vmWrapper: VMWrapper) {
    val screens = listOf<Pair<String, String>>(
        Pair(AppScreens.MLFaceRecognitionScreen.name, "Face Detection"),
        Pair(AppScreens.MLDocumentScannerScreen.name, "Document Scanner"),
        Pair(AppScreens.MLObjectDetectionScreen.name, "Object Detection"),
        Pair(AppScreens.MLSelfieSegmentationScreen.name, "Selfie Segmentation"),
        Pair(AppScreens.MLFaceMeshDetectionScreen.name, "Face Mesh Detection"),
        Pair(AppScreens.MLTextRecognitionScreen.name, "Text Recognition"),
    )


    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 25.dp),
            text = "ML Kit test",
            textAlign = TextAlign.Center
        )
        screens.forEachIndexed { index, pair ->
            Box(modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    navController.navigate(pair.first)
                }) {
                Text(modifier = Modifier.padding(20.dp), text = pair.second)
            }
            if (index < screens.size - 1)
            HorizontalDivider()
        }
    }

}
