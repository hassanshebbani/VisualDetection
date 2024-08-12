package com.example.visualdetection.ML.presentation.screens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Rect
import android.util.Log
import android.util.Size
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.visualdetection.Core.data.MyObjectClassifier
import com.example.visualdetection.Core.feature.Camera.MyCameraLauncher
import com.example.visualdetection.Core.feature.Camera.ObjectDetectionImageAnalyzer
import com.example.visualdetection.Core.feature.Camera.takePhoto
import com.example.visualdetection.Core.wrappers.VMWrapper
import com.google.mlkit.vision.objects.DetectedObject
import kotlinx.coroutines.delay


@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun MLObjectDetectionScreen(navController: NavController, vmWrapper: VMWrapper) {

    val context = LocalContext.current
    var counter by remember {
        mutableIntStateOf(3)
    }

    var detectedObjects by remember {
        mutableStateOf(emptyList<DetectedObject>())
    }
    val analyzer = remember {
        ObjectDetectionImageAnalyzer(classifier = MyObjectClassifier(context), onResults = {
            detectedObjects = it
        })
    }

    val controller = remember {
        LifecycleCameraController(context).apply {
            cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            setEnabledUseCases(
                CameraController.IMAGE_CAPTURE or CameraController.VIDEO_CAPTURE or CameraController.IMAGE_ANALYSIS
            )
            setImageAnalysisAnalyzer(
                ContextCompat.getMainExecutor(context), analyzer
            )
        }
    }

    val hintTexts = listOf(
        "Bring your camera closer to the id", "Hold Steady"
    )

    var boundingBoxRect by remember {
        mutableStateOf<Rect>(Rect(0, 0, 0, 0))
    }

    var color by remember {
        mutableStateOf<Color>(Color.Red)
    }

    var hintText by remember {
        mutableStateOf("")
    }

    var isPassport by remember {
        mutableStateOf(false)
    }
    var boundsValidation = listOf(200, 270, 60..130, 340..460, 380..470)
    if (isPassport) {

    } else {

    }

    val previewSize by vmWrapper.coreVM.camPreviewSize.collectAsState()


    val imageCaptureSize = Size(3072, 4096) // Replace with actual target resolution


    var takenPhoto by remember {
        mutableStateOf<Bitmap?>(null)
    }

    // image pinch to zoom
    var scale by remember {
        mutableFloatStateOf(1f)
    }
    var offset by remember {
        mutableStateOf(Offset.Zero)
    }


    if (boundingBoxRect.width() > 200) {
        if (boundingBoxRect.top < 270) {
            if (boundingBoxRect.left in 60..130) {
                if (boundingBoxRect.right in 340..460) {
                    if (boundingBoxRect.bottom in 380..470) {
                        color = Color.Green
                        hintText = hintTexts[1]
//                        takePhoto(
//                            controller = controller,
//                            onPhotoTaken = { bitmap, uri ->
//                                vmWrapper.coreVM.onTakePhoto(bitmap, uri)
//                                takenPhoto = bitmap
//                            },
//                            context = context,
//                            boundingBoxRect,
//                            previewSize,
//                            imageCaptureSize
//                        )

                    } else {
                        color = Color.Red
                        hintText = hintTexts[0]
                    }
                } else {
                    color = Color.Red
                    hintText = hintTexts[0]
                }
            } else {
                color = Color.Red
                hintText = hintTexts[0]
            }
        } else {
            color = Color.Red
            hintText = hintTexts[0]
        }

    } else {
        color = Color.Red
        hintText = hintTexts[0]
    }

    LaunchedEffect(key1 = color) {
        Log.d("meow meow", "$color")
        if (color == Color.Green) {
            while (counter >= 0) {
                delay(1000L)
                if (counter == 0) takePhoto(
                    controller = controller, onPhotoTaken = { bitmap, uri ->
                        vmWrapper.coreVM.onTakePhoto(bitmap, uri)
                        takenPhoto = bitmap
                        controller.unbind()
                    }, context = context, boundingBoxRect, previewSize, imageCaptureSize
                )
                if (counter > 0) counter--
            }
        } else {
            counter = 3
        }
    }



    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (takenPhoto == null) {

            MyCameraLauncher(modifier = Modifier.fillMaxSize(),
                coreVM = vmWrapper.coreVM,
                controller = controller,
                onCameraSwitchCB = {
                    Toast.makeText(context, "switch camera", Toast.LENGTH_SHORT).show()
                })
            Column(
                modifier = Modifier.fillMaxSize()
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black.copy(0.6f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        modifier = Modifier.padding(12.dp),
                        text = hintText + "\n\n\n$counter",
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 28.sp,
                        color = Color.Cyan
                    )
                }

                detectedObjects.forEach { detectedObject ->
                    boundingBoxRect = detectedObject.boundingBox
                    val trackingId = detectedObject.trackingId
                    Log.d(
                        "detectedObjectsaaa",
                        "$detectedObject\n${detectedObjects.size}\n" + "$boundingBoxRect\n" + "$trackingId\n"
                    )
                    Text(
                        text = "${boundingBoxRect.width()} x ${boundingBoxRect.height()}" + "${detectedObjects.size}\n" + "$boundingBoxRect\n" + "$trackingId\n",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                    )
                }
            }
//        BoundingBoxOverlay(rect = boundingBoxRect)

            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .height(250.dp)
                    .border(3.dp, color)
            )
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(25.dp)
            ) {
                Text(text = "${takenPhoto!!.width}")
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(takenPhoto!!.width.toFloat() / takenPhoto!!.height.toFloat())
                ) {
                    val state =
                        rememberTransformableState { zoomChange, panChange, rotationChange ->
                            scale = (scale * zoomChange).coerceIn(1f, 5f)

                            val extraWidth = (scale - 1) * constraints.maxWidth
                            val extraHeight = (scale - 1) * constraints.maxHeight

                            val maxX = extraWidth / 2
                            val maxY = extraHeight / 2

                            offset = Offset(
                                x = (offset.x + scale * panChange.x).coerceIn(-maxX, maxX),
                                y = (offset.y + scale * panChange.y).coerceIn(-maxY, maxY),
                            )
                        }
                    AsyncImage(modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                            translationX = offset.x
                            translationY = offset.y
                        }
                        .transformable(state)
                        ,
                        model = takenPhoto, contentDescription = null)
                }
            }

        }
    }


}

@Composable
fun BoundingBoxOverlay(rect: Rect) {
    // Calculate the position and size of the bounding box
    val left = rect.left
    val top = rect.top
    val right = rect.right
    val bottom = rect.bottom
    val width = right - rect.left
    val height = bottom - rect.top

    var color = Color.Red



    Box(
        modifier = Modifier
            .offset(x = left.dp - 50.dp, y = top.dp + 50.dp)  // Position the box
            .size(width.dp, height.dp)        // Size the box
            .border(2.dp, color)          // Red border for the bounding box
    ) {
        Text(
            text = "${rect.left}\n" + "${rect.top}\n" + "${rect.right}\n" + "${rect.bottom}\n" + ""
        )
    }
}