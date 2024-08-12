package com.example.visualdetection.ML.presentation.screens

import android.util.Log
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.visualdetection.Core.data.MyImageClassifier
import com.example.visualdetection.Core.feature.Camera.FaceDetectionImageAnalyzer
import com.example.visualdetection.Core.feature.Camera.MyCameraLauncher
import com.example.visualdetection.Core.feature.Camera.takePhoto
import com.example.visualdetection.Core.wrappers.VMWrapper
import com.google.mlkit.vision.face.Face
import kotlinx.coroutines.delay


@Composable
fun MLFaceRecognitionScreen(navController: NavController, vmWrapper: VMWrapper) {
    val context = LocalContext.current;
    var counter by remember {
        mutableIntStateOf(0)
    }

    var isValidFace by remember {
        mutableStateOf(false)
    }
    var detectedFaces by remember {
        mutableStateOf(emptyList<Face>())
    }
    val analyzer = remember {
        FaceDetectionImageAnalyzer(
            classifier = MyImageClassifier(context),
            onResults = {
                detectedFaces = it
            }
        )
    }

    LaunchedEffect(key1 = isValidFace) {
        if (isValidFace) {
            while (counter < 10) {
                counter++
                delay(1000L)
                if (counter == 7)
                    navController.popBackStack()
//                    navController.navigate(AppScreens.MLMainScreen.name)
            }
            isValidFace = false
            detectedFaces = emptyList()
            counter = 0
        }
    }

    val controller = remember {
        LifecycleCameraController(context).apply {
            cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
            setEnabledUseCases(
                CameraController.IMAGE_CAPTURE or
                        CameraController.VIDEO_CAPTURE or
                        CameraController.IMAGE_ANALYSIS
            )
            setImageAnalysisAnalyzer(
                ContextCompat.getMainExecutor(context), analyzer
            )
        }
    }
    if (detectedFaces.isNotEmpty()) {
        Log.d("facesssssss", "$counter $detectedFaces")
        if (detectedFaces.last().leftEyeOpenProbability!! + detectedFaces.last().rightEyeOpenProbability!! >= 1.8f) {
            isValidFace = true
            if (counter == 3) {
                takePhoto(
                    controller = controller,
                    onPhotoTaken = vmWrapper.coreVM::onTakePhoto,
                    context = context
                )
            }
            if (counter >= 2) {
                vmWrapper.coreVM.setTakeVideo(true)
            }
            if (counter >= 7) {
                vmWrapper.coreVM.setTakeVideo(false)
            }
        } else {
            isValidFace = false
            detectedFaces = emptyList()
            counter = 0
        }
    } else {
        isValidFace = false
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        MyCameraLauncher(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .border(
                    brush = Brush.verticalGradient(
                        if (isValidFace) listOf(
                            Color.Green,
                            Color.Yellow
                        ) else listOf(Color.Blue, Color.Red)
                    ),
                    width = 3.dp,
                    shape = CircleShape
                ),
            coreVM = vmWrapper.coreVM,
//            analyzer = analyzer,
            controller = controller,
            onCameraSwitchCB = {
                Toast.makeText(context, "switch camera", Toast.LENGTH_SHORT).show()
            }
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Text(
                text = "$counter",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
            )
            detectedFaces.forEach {
                Text(
                    text = it.toString(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                )
            }
        }

    }
}

