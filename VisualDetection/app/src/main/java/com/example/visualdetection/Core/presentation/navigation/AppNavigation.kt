package com.example.visualdetection.Core.presentation.navigation

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll 
import androidx.compose.material3.Scaffold 
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember   
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.visualdetection.Core.feature.Camera.MyCameraLauncher
import com.example.visualdetection.ML.presentation.screens.MLDocumentScannerScreen
import com.example.visualdetection.ML.presentation.screens.MLFaceMeshDetectionScreen
import com.example.visualdetection.ML.presentation.screens.MLFaceRecognitionScreen
import com.example.visualdetection.ML.presentation.screens.MLMainScreen
import com.example.visualdetection.ML.presentation.screens.MLSelfieSegmentationScreen
import com.example.visualdetection.ML.presentation.screens.MLTextRecognitionScreen
import com.example.visualdetection.Core.navigation.AppScreens
import com.example.visualdetection.Core.wrappers.VMWrapper
import com.example.visualdetection.ML.presentation.screens.MLObjectDetectionScreen


@Composable
fun AppNavigation(
    vmWrapper: VMWrapper,
    permissionsComposable: @Composable () -> Unit
) {

    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
    ) { innerPadding ->
        permissionsComposable()
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()

            ) {
                    NavHost(
                        modifier = Modifier
                            .fillMaxSize(),
                                navController = navController,
                        startDestination = AppScreens.MLMainScreen.name,
                    ) {
                        composable(AppScreens.MLMainScreen.name) {
                            MLMainScreen(navController, vmWrapper)
                        }

                        composable(AppScreens.MLTextRecognitionScreen.name) {
                            MLTextRecognitionScreen(navController, vmWrapper)
                        }

                        composable(AppScreens.MLSelfieSegmentationScreen.name) {
                            MLSelfieSegmentationScreen(navController, vmWrapper)
                        }

                        composable(AppScreens.MLDocumentScannerScreen.name) {
                            MLDocumentScannerScreen(navController, vmWrapper)
                        }

                        composable(AppScreens.MLFaceRecognitionScreen.name) {
                            MLFaceRecognitionScreen(navController, vmWrapper)
                        }

                        composable(AppScreens.MLFaceMeshDetectionScreen.name) {
                            MLFaceMeshDetectionScreen(navController, vmWrapper)
                        }

                        composable(AppScreens.MLObjectDetectionScreen.name) {
                            MLObjectDetectionScreen(navController, vmWrapper)
                        }


                    
                }
            }


        }
    }
}
