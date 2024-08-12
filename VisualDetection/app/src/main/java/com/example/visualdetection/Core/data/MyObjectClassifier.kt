package com.example.visualdetection.Core.data

import android.content.Context
import android.util.Log
import com.example.visualdetection.Core.domain.ObjectClassifier
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.objects.DetectedObject
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions

class MyObjectClassifier(
    private val context: Context,
): ObjectClassifier {

    override fun detectObjects(image: InputImage): Task<List<DetectedObject>> {
        val options = ObjectDetectorOptions.Builder()
            .setDetectorMode(ObjectDetectorOptions.STREAM_MODE)
//            .enableClassification()  // Optional
            .build()


        val objectDetector = ObjectDetection.getClient(options)
        val result = objectDetector.process(image)
        return result

    }
}