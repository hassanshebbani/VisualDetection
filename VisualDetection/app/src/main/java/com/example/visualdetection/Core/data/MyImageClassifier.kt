package com.example.visualdetection.Core.data

import android.content.Context
import android.util.Log
import com.example.visualdetection.Core.domain.ImageClassifier
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceContour
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.face.FaceLandmark

class MyImageClassifier(
    private val context: Context,
): ImageClassifier {

    override fun detectFaces(image: InputImage) : Task<List<Face>> {
        // [START set_detector_options]
        Log.d("MyImageClassifier", "entering detectFaces: $image")
        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .setMinFaceSize(0.15f)
            .enableTracking()
            .build()
        // [END set_detector_options]

        // [START get_detector]
        val detector = FaceDetection.getClient(options)
        // Or, to use the default option:
        // val detector = FaceDetection.getClient();
        // [END get_detector]

        // [START run_detector]
        val result = detector.process(image)
//            .addOnSuccessListener { faces : List<Face> ->
//                // Task completed successfully
//                // [START_EXCLUDE]
//                // [START get_face_info]
//                Log.d("MyImageClassifier", "result addOnSuccessListener: $faces")
//                for (face in faces) {
//                    val bounds = face.boundingBox
//                    val rotY = face.headEulerAngleY // Head is rotated to the right rotY degrees
//                    val rotZ = face.headEulerAngleZ // Head is tilted sideways rotZ degrees
//
//                    // If landmark detection was enabled (mouth, ears, eyes, cheeks, and
//                    // nose available):
//                    val leftEar = face.getLandmark(FaceLandmark.LEFT_EAR)
//                    leftEar?.let {
//                        val leftEarPos = leftEar.position
//                    }
//
//                    // If classification was enabled:
//                    if (face.smilingProbability != null) {
//                        val smileProb = face.smilingProbability
//                    }
//                    if (face.rightEyeOpenProbability != null) {
//                        val rightEyeOpenProb = face.rightEyeOpenProbability
//                    }
//
//                    // If face tracking was enabled:
//                    if (face.trackingId != null) {
//                        val id = face.trackingId
//                    }
//                }
//                // [END get_face_info]
//                // [END_EXCLUDE]
//            }
//            .addOnFailureListener { e ->
//                // Task failed with an exception
//                Log.d("MyImageClassifier", "result addOnFailureListener: $image")
//                // ...
//            }
        // [END run_detector]
        return result
    }
}

private fun faceOptionsExamples() {
    // [START mlkit_face_options_examples]
    // High-accuracy landmark detection and face classification
    val highAccuracyOpts = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
//        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
//        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
        .build()

    // Real-time contour detection
    val realTimeOpts = FaceDetectorOptions.Builder()
        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
        .build()
    // [END mlkit_face_options_examples]
}

private fun processFaceList(faces: List<Face>) {
    // [START mlkit_face_list]
    for (face in faces) {
        val bounds = face.boundingBox
        val rotY = face.headEulerAngleY // Head is rotated to the right rotY degrees
        val rotZ = face.headEulerAngleZ // Head is tilted sideways rotZ degrees

        // If landmark detection was enabled (mouth, ears, eyes, cheeks, and
        // nose available):
        val leftEar = face.getLandmark(FaceLandmark.LEFT_EAR)
        leftEar?.let {
            val leftEarPos = leftEar.position
        }

        // If contour detection was enabled:
        val leftEyeContour = face.getContour(FaceContour.LEFT_EYE)?.points
        val upperLipBottomContour = face.getContour(FaceContour.UPPER_LIP_BOTTOM)?.points

        // If classification was enabled:
        if (face.smilingProbability != null) {
            val smileProb = face.smilingProbability
        }
        if (face.rightEyeOpenProbability != null) {
            val rightEyeOpenProb = face.rightEyeOpenProbability
        }

        // If face tracking was enabled:
        if (face.trackingId != null) {
            val id = face.trackingId
        }
    }
    // [END mlkit_face_list]

}