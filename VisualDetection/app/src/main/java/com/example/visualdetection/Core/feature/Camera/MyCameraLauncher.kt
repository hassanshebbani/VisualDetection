package com.example.visualdetection.Core.feature.Camera

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.Rect
import android.net.Uri
import android.util.Log
import android.util.Size
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture.OnImageCapturedCallback
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Recording
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.camera.view.video.AudioConfig
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.visualdetection.Core.data.MyFileTypes
import com.example.visualdetection.Core.presentation.components.MyIconPainter
import com.example.visualdetection.Core.presentation.viewmodel.CoreVM
import com.example.visualdetection.Core.util.saveFileToStorage
import java.io.File
import java.time.LocalDateTime

private var recording: Recording? = null
private var totalRecordings = 0
private var isTakingPhoto = false

@Composable
fun MyCameraLauncher(
    modifier: Modifier = Modifier,
    coreVM: CoreVM,
    onTakePhotoCB: (() -> Unit)? = null,
    onTakeVideoCB: (() -> Unit)? = null,
    onPhotoLibraryCB: (() -> Unit)? = null,
    onCloseCameraCB: (() -> Unit)? = null,
    onCameraSwitchCB: (() -> Unit)? = null,
    controller: LifecycleCameraController,
) {

    val context = LocalContext.current
    val shouldTakeVideo by coreVM.takeVideo.collectAsState()

    if (shouldTakeVideo && recording == null) {
        takeVideo(controller, context)
    } else if (!shouldTakeVideo && recording != null) {
        takeVideo(controller, context)
    }

    val cameraXPermissions = listOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO,
    )

    if (coreVM.hasRequiredPermissions(context, cameraXPermissions)) Box(modifier = modifier) {
        CameraPreview(modifier = Modifier.fillMaxSize(), controller = controller,
            onPreviewSizeChanged = { size ->
                coreVM.setCamPreviewSize(size)
                // Now you can use previewSizeState.value for your scaling logic
            })
        Box(
            modifier = Modifier
                .padding(12.dp)
                .background(Color.Cyan)
                .clickable {
                    controller.cameraSelector =
                        if (controller.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) CameraSelector.DEFAULT_FRONT_CAMERA
                        else CameraSelector.DEFAULT_BACK_CAMERA
                    onCameraSwitchCB?.invoke()
                },
        ) {

            MyIconPainter(
                modifier = Modifier
                    .size(30.dp)
                    .offset(-(16).dp, 16.dp)
                    .align(Alignment.TopEnd),
                icon = Icons.Default.Refresh,
                iconColor = Color.White
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .padding(12.dp)
                    .background(Color.Cyan)
                    .clickable {
                        takePhoto(
                            controller = controller,
                            context = context,
                            onPhotoTaken = coreVM::onTakePhoto
                        )
                        onTakePhotoCB?.invoke()
                    },
            ) {
                MyIconPainter(
                    modifier = Modifier
                        .size(40.dp), icon = Icons.Default.ThumbUp, iconColor = Color.White
                )
            }
            Box(
                modifier = Modifier
                    .padding(12.dp)
                    .background(Color.Cyan)
                    .clickable {
                        takeVideo(controller = controller, context)
                        onTakeVideoCB?.invoke()
                    }
            ) {
                MyIconPainter(
                    modifier = Modifier
                        .size(30.dp)
                        , icon = Icons.Default.PlayArrow, iconColor = Color.White
                )
            }
        }
    }
}

@Composable
fun CameraPreview(
    controller: LifecycleCameraController,
    modifier: Modifier = Modifier,
    onPreviewSizeChanged: (Size) -> Unit
) {
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    AndroidView(modifier = modifier.onSizeChanged { size ->
        // The size is now available
        onPreviewSizeChanged(Size(size.width, size.height))
    }, factory = {
        PreviewView(it).apply {
            this.controller = controller
            controller.bindToLifecycle(lifecycleOwner)
        }
    })
}

@SuppressLint("MissingPermission")
fun takeVideo(
    controller: LifecycleCameraController,
    context: Context
) {
    if (recording != null) {
        recording?.stop()
        recording = null
        Toast.makeText(context, "Video Capture End", Toast.LENGTH_SHORT).show()
        return
    }
    if (totalRecordings == 0) {
        val outputFile = File(context.filesDir, "my-rec-${LocalDateTime.now()}.mp4")
        recording = controller.startRecording(

            FileOutputOptions.Builder(outputFile).build(),
            AudioConfig.create(true),
            ContextCompat.getMainExecutor(context.applicationContext)
        ) {

            when (it) {
                is VideoRecordEvent.Start -> {
                    Toast.makeText(context, "Video Capture Start", Toast.LENGTH_SHORT).show()
                }

                is VideoRecordEvent.Pause -> {
                    Toast.makeText(context, "Video Capture Pause", Toast.LENGTH_SHORT).show()
                }

                is VideoRecordEvent.Resume -> {
                    Toast.makeText(context, "Video Capture Resumed", Toast.LENGTH_SHORT).show()
                }

                is VideoRecordEvent.Finalize -> {
                    if (it.hasError()) {
                        recording?.close()
                        recording = null
                        Log.d("takeVideo", it.error.toString())
                    } else {
                        Toast.makeText(context, "Video Capture Succeeded", Toast.LENGTH_SHORT)
                            .show()
                        totalRecordings++
                    }
                }
            }
        }
    } else {
        Toast.makeText(context, "Already Captured Video", Toast.LENGTH_LONG)
            .show()
    }
}


fun takePhoto(
    controller: LifecycleCameraController, onPhotoTaken: (Bitmap, Uri?) -> Unit, context: Context,
    boundingRect: Rect? = null, previewSize: Size? = null, imageCaptureSize: Size? = null
) {

    if (!isTakingPhoto) {
        isTakingPhoto = true
    controller.takePicture(ContextCompat.getMainExecutor(context),
        object : OnImageCapturedCallback() {

            override fun onCaptureSuccess(image: ImageProxy) {
                try {
                    super.onCaptureSuccess(image)
                    val matrix = Matrix().apply {
                        postRotate(image.imageInfo.rotationDegrees.toFloat())
                        // flip photo for front camera because it appears flipped
                        if (controller.cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA) postScale(
                            -1f,
                            1f
                        )
                    }
                    Log.d(
                        "adasdfasdfasdf",
                        "$boundingRect: ${boundingRect?.width()} x ${boundingRect?.height()}\n" + "${image.imageInfo}\n" +
                                "${image.cropRect}\n" + "${previewSize}\n" + "${imageCaptureSize}\n" + "${image.width} x ${image.height}"
                    )
                    var rotatedBitmap = Bitmap.createBitmap(
                        image.toBitmap(),
                        (image.width * 0.15).toInt(),
                        (image.height * 0.24).toInt(),
                        (image.width * 0.7).toInt(),
                        (image.height * 0.6).toInt(),
                        matrix,
                        true
                    )

                    val savedFileUri = saveFileToStorage(
                        context = context, file = rotatedBitmap, fileType = MyFileTypes.IMAGE
                    )
                    onPhotoTaken(rotatedBitmap, savedFileUri)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.d("onCaptureSuccess", e.message.toString())
                } finally {
                    isTakingPhoto = false
                }
            }

            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
                Log.d("takePhoto", exception.message.toString())
                isTakingPhoto = false
            }

        })
    }
}