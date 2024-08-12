package com.example.visualdetection.Core.feature.Camera

import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.example.visualdetection.Core.domain.ImageClassifier
import com.example.visualdetection.Core.domain.ObjectClassifier
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.objects.DetectedObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class ObjectDetectionImageAnalyzer(
    private val classifier: ObjectClassifier,
    private val onResults: (List<DetectedObject>) -> Unit
): ImageAnalysis.Analyzer {


    override fun analyze(image: ImageProxy) {
            val rotationDegrees = image.imageInfo.rotationDegrees
            val bitmap = image
                .toBitmap()
            Log.d("ImageAnalyzer", "bitmap analysis: $bitmap")

            val results = classifier.detectObjects(InputImage.fromBitmap(bitmap, rotationDegrees))
            Log.d("results", "$results")
            GlobalScope.launch(Dispatchers.Main) {
                try {
                    val results = detectObjectsAsync(InputImage.fromBitmap(bitmap, rotationDegrees))
                    onResults(results)
                } catch (e: Exception) {
                    Log.e("ObjectDetectionImageAnalyzer", "Error detecting objects", e)
                } finally {
                    image.close()
                }
            }
        image.close()
    }

    private suspend fun detectObjectsAsync(image: InputImage): List<DetectedObject> =
        suspendCancellableCoroutine { continuation ->
            val task = classifier.detectObjects(image)
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