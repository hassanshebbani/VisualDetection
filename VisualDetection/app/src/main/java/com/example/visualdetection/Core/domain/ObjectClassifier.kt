package com.example.visualdetection.Core.domain


import android.graphics.Bitmap
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.objects.DetectedObject

interface ObjectClassifier {
    fun detectObjects(image: InputImage) : Task<List<DetectedObject>>
}

