package com.example.arlearner.ui.screens

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.MotionEvent
import androidx.annotation.RequiresApi
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import com.example.arlearner.util.Utils
import com.google.ar.core.Config
import com.google.ar.core.Frame
import com.google.ar.core.TrackingFailureReason
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.arcore.createAnchorOrNull
import io.github.sceneview.ar.arcore.isValid
import io.github.sceneview.ar.rememberARCameraNode
import io.github.sceneview.model.ModelInstance
import io.github.sceneview.node.Node
import io.github.sceneview.rememberCollisionSystem
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberMaterialLoader
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNodes
import io.github.sceneview.rememberOnGestureListener
import io.github.sceneview.rememberView



import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.arlearner.R
import com.example.arlearner.util.ARObjectData
import com.example.arlearner.util.ARObjectStorage
import dev.romainguy.kotlin.math.Float3
import io.github.sceneview.ar.node.AnchorNode


@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun ARScreenView(navController: NavController, model: String, context: Context) {

    // State to track the selected model
    var selectedModel by remember { mutableStateOf("A") } // Default to "A"
    var selectedType by remember { mutableStateOf("A") } // "A", "B", or "IMG"


    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine = engine)
    val materialLoader = rememberMaterialLoader(engine = engine)
    val cameraNode = rememberARCameraNode(engine = engine)
    val childNodes = rememberNodes()
    val view = rememberView(engine = engine)
    val collisionSystem = rememberCollisionSystem(view = view)
    val planeRenderer = remember {
        mutableStateOf(true)
    }
    val modelInstance = remember {
        mutableListOf<ModelInstance>()
    }
    val trackingFailureReason = remember {
        mutableStateOf<TrackingFailureReason?>(null)
    }
    val frame = remember {
        mutableStateOf<Frame?>(null)
    }
    val sharedPreferences = context.getSharedPreferences("ARObjects", Context.MODE_PRIVATE)

    // ✅ State to track if objects should be loaded
    var objectsLoaded by remember { mutableStateOf(false) }

    fun loadSavedObjects() {
        val savedObjects = ARObjectStorage.loadObjects(sharedPreferences)
        Log.d("ARScreen", "Loading Visitor Mode: Found ${savedObjects.size} objects")

        savedObjects.forEach { data ->
            Log.d("ARScreen", "Loading Object: Model=${data.model}, x=${data.x}, y=${data.y}, z=${data.z}")

            val pose = com.google.ar.core.Pose.makeTranslation(data.x, data.y, data.z)
            val anchor = cameraNode.session?.createAnchor(pose)

            if (anchor == null) {
                Log.e("ARScreen", "Failed to create anchor at (${data.x}, ${data.y}, ${data.z}) for object: ${data.model}")
                return@forEach
            }

            val (anchorNode, modelNode) = Utils.createAnchorNode(
                engine = engine,
                modelLoader = modelLoader,
                materialLoader = materialLoader,
                modelInstance = modelInstance,
                anchor = anchor,
                model = Utils.getModelForAlphabet(data.model))
            anchorNode.name = data.model
            modelNode.scale = Float3(data.scaleX, data.scaleY, data.scaleZ)
            childNodes += anchorNode

            Log.d("ARScreen", "Object placed: ${data.model} at x=${data.x}, y=${data.y}, z=${data.z}, Scale=${modelNode.scale}")
        }

        objectsLoaded = true // ✅ Mark as loaded
    }


    Box(modifier = Modifier.fillMaxSize()) {
        ARScene(
            modifier = Modifier.fillMaxSize(),
            childNodes = childNodes,
            engine = engine,
            view = view,
            modelLoader = modelLoader,
            collisionSystem = collisionSystem,
            planeRenderer = planeRenderer.value,
            cameraNode = cameraNode,
            materialLoader = materialLoader,
            onTrackingFailureChanged = {
                trackingFailureReason.value = it
            },
            onSessionUpdated = { _, updatedFrame ->
                frame.value = updatedFrame
            },
            sessionConfiguration = { session, config ->
                config.depthMode = when (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
                    true -> Config.DepthMode.AUTOMATIC
                    else -> Config.DepthMode.DISABLED
                }
                config.lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR
            },
            onGestureListener = rememberOnGestureListener(
                onSingleTapConfirmed = { e: MotionEvent, node: Node? ->
                    if (node == null) {
                        val hitTestResult = frame.value?.hitTest(e.x, e.y)
                        hitTestResult?.firstOrNull {
                            it.isValid(depthPoint = false, point = false)
                        }?.createAnchorOrNull()?.let { anchor ->
                            if (selectedModel == "IMG") {
//                                val imageNode = Utils.createImageAnchorNode(
//                                    context,
//                                    engine,
//                                    materialLoader,
//                                    anchor,
//                                    R.drawable.person // Your image resource
//                                )
//                                childNodes += imageNode
                            } else {
                                val (anchorNode, modelNode) = Utils.createAnchorNode(
                                    engine = engine,
                                    modelLoader = modelLoader,
                                    materialLoader = materialLoader,
                                    modelInstance = modelInstance,
                                    anchor = anchor,
                                    model = Utils.getModelForAlphabet(selectedModel)
                                )
                                anchorNode.name = selectedModel
                                modelNode.scale = Float3(1f, 1f, 1f)
                                childNodes += anchorNode
                            }
                        }
                    }

                },
                onScale = { _, _, node ->
                    node?.let {
                        it.scale *= 1.1f // You can adjust this scale factor as needed
                        Log.d("ARScreen", "Scaled ${it.name} to (${it.scale.x}, ${it.scale.y}, ${it.scale.z})")
                    }
                }
            )



        )
        // Buttons for selecting models
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(Color.Black.copy(alpha = 0.6f))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { selectedModel = "A" }, // Change model to "A"
                    modifier = Modifier.padding(8.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Model A")
                }

                Button(
                    onClick = { selectedModel = "B" }, // Change model to "B"
                    modifier = Modifier.padding(8.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Model B")
                }
//                Button(
//                    onClick = { selectedModel = "IMG" },
//                    modifier = Modifier.padding(8.dp),
//                    shape = RoundedCornerShape(12.dp)
//                ) { Text("Model Img") }

                // **Save Button for Creator Mode**
                if (model == "C") {
                    Button(
                        onClick = {
                            val savedObjects = childNodes.mapNotNull { node ->
                                if (node is AnchorNode) {
                                    val modelNode = node.childNodes.firstOrNull()
                                    val modelName = node.name ?: "Unknown"
                                    val scale = modelNode?.scale ?: Float3(1f, 1f, 1f)

                                    Log.d("ARScreen", "Saving Object: Model=\$modelName, x=\${node.position.x}, y=\${node.position.y}, z=\${node.position.z}, Scale=\$scale")

                                    ARObjectData(modelName, node.position.x, node.position.y, node.position.z, scale.x, scale.y, scale.z)
                                } else null
                            }

                            ARObjectStorage.saveObjects(savedObjects, sharedPreferences)
                            Log.d("ARScreen", "Saved ${savedObjects.size} objects to SharedPreferences")

                            try {
                                // ✅ Prevent crash: safely detach all anchors before exit
                                childNodes.forEach { node ->
                                    if (node is AnchorNode) {
                                        node.anchor?.detach()
                                    }
                                }
                                childNodes.clear()
                            } catch (e: Exception) {
                                Log.e("ARScreen", "Error while detaching anchors: ${e.message}")
                            }

                            // ✅ Navigate back and pass result
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set("saveSuccess", true)

                            navController.popBackStack()
                        },
                        modifier = Modifier.padding(8.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Save & Exit")
                    }


                }

                // ✅ Add button to manually load objects in Visitor Mode
                if (model == "D" && !objectsLoaded) {
                    Button(
                        onClick = { loadSavedObjects() },
                        modifier = Modifier.padding(8.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Load Objects")
                    }
                }

            }
        }

    }
}