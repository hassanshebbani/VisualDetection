package com.example.visualdetection.Core.domain


import android.graphics.Bitmap
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face

interface ImageClassifier {

    fun detectFaces(image: InputImage) : Task<List<Face>>
}
