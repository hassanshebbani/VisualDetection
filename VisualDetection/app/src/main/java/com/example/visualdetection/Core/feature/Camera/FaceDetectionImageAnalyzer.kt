package com.example.visualdetection.Core.feature.Camera

import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.example.visualdetection.Core.domain.ImageClassifier
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FaceDetectionImageAnalyzer(
    private val classifier: ImageClassifier,
    private val onResults: (List<Face>) -> Unit
): ImageAnalysis.Analyzer {

    private var frameSkipCounter = 0

    override fun analyze(image: ImageProxy) {
        Log.d("ImageAnalyzer", "entering analysis: $frameSkipCounter")
        if(frameSkipCounter % 60 == 0) {
            val rotationDegrees = image.imageInfo.rotationDegrees
            val bitmap = image
                .toBitmap()
//                .centerCrop(321, 321)
            Log.d("ImageAnalyzer", "bitmap analysis: $bitmap")

            val results = classifier.detectFaces(InputImage.fromBitmap(bitmap, rotationDegrees))
            Log.d("results", "$results")
//            onResults(results)
            GlobalScope.launch(Dispatchers.Main) {
                try {
                    val results = detectFacesAsync(InputImage.fromBitmap(bitmap, rotationDegrees))
                    onResults(results)
                } catch (e: Exception) {
                    Log.e("ImageAnalyzer", "Error detecting faces", e)
                } finally {
                    image.close()
                }
            }
        }
        frameSkipCounter++
        Log.d("ImageAnalyzer", "exiting analysis: $frameSkipCounter")

        image.close()
    }

    private suspend fun detectFacesAsync(image: InputImage): List<Face> =
        suspendCancellableCoroutine { continuation ->
            val task = classifier.detectFaces(image)
            task.addOnSuccessListener { faces ->
                continuation.resume(faces)
            }
            task.addOnFailureListener { e ->
                continuation.resumeWithException(e)
            }
            task.addOnCanceledListener {
                continuation.cancel()
            }
        }
}


fun Bitmap.centerCrop(desiredWidth: Int, desiredHeight: Int): Bitmap {
    val xStart = (width - desiredWidth) / 2
    val yStart = (height - desiredHeight) / 2

    if(xStart < 0 || yStart < 0 || desiredWidth > width || desiredHeight > height) {
        throw IllegalArgumentException("Invalid arguments for center cropping")
    }

    return Bitmap.createBitmap(this, xStart, yStart, desiredWidth, desiredHeight)
}