package com.example.visualdetection.Core.navigation

enum class AppScreens {
    // ML
    MLMainScreen, MLTextRecognitionScreen, MLFaceRecognitionScreen, MLFaceMeshDetectionScreen, MLDocumentScannerScreen, MLSelfieSegmentationScreen, MLObjectDetectionScreen
    ;

    companion object {
        fun fromRoute(route: String?): AppScreens = when (route?.substringBefore("/")) {

            // ML
            MLMainScreen.name -> MLMainScreen
            MLTextRecognitionScreen.name -> MLTextRecognitionScreen
            MLFaceRecognitionScreen.name -> MLFaceRecognitionScreen
            MLFaceMeshDetectionScreen.name -> MLFaceMeshDetectionScreen
            MLDocumentScannerScreen.name -> MLDocumentScannerScreen
            MLSelfieSegmentationScreen.name -> MLSelfieSegmentationScreen
            MLObjectDetectionScreen.name -> MLObjectDetectionScreen

            null -> MLMainScreen

            else -> throw IllegalArgumentException("Route $route is not found")
        }
    }
}