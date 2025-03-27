package com.example.arlearner.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Mesh
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import com.example.arlearner.R
import com.google.android.filament.Engine
import com.google.android.filament.MaterialInstance
import com.google.android.filament.utils.Float3
import com.google.ar.core.Anchor
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.loaders.MaterialLoader
import io.github.sceneview.loaders.ModelLoader
import io.github.sceneview.model.ModelInstance
import io.github.sceneview.node.CubeNode
import io.github.sceneview.node.ModelNode
import io.github.sceneview.node.Node



object Utils {

    val alphabets = mapOf(
        "A" to "A_solemn_Jewish_Israe_0316175952_texture.glb",
        "B" to "ball.glb",
        "C" to "cat.glb",
        "D" to "dog.glb",
        "E" to "elephant.glb",
        "F" to "fox.glb",
        "G" to "goat.glb",
        "H" to "hen.glb",
        "I" to "icecream.glb",
        "J" to "jug.glb",
        "K" to "kite.glb",
        "L" to "lion.glb",
        "M" to "monkey.glb",
        "N" to "nest.glb",
        "O" to "owl.glb",
        "P" to "parrot.glb",
        "Q" to "quail.glb",
        "R" to "rat.glb",
        "S" to "ship.glb",
        "T" to "telephone.glb",
        "U" to "umbrella.glb",
        "V" to "van.glb",
        "W" to "watch.glb",
        "X" to "xylophone.glb",
        "Y" to "yacht.glb",
        "Z" to "zebra.glb"
    )

    fun getModelForAlphabet(alphabet: String): String {
        val modelName = alphabets[alphabet]

        if (modelName == null) {
            Log.e("ARScreen", "Error: Model '$alphabet' not found in dictionary")
            return "models/default.glb"  // ✅ Provide a fallback model instead of crashing
        }

        return "models/$modelName"
    }

    fun createAnchorNode(
        engine: Engine,
        modelLoader: ModelLoader,
        materialLoader: MaterialLoader,
        modelInstance: MutableList<ModelInstance>,
        anchor: Anchor,
        model: String
    ): Pair<AnchorNode, ModelNode> {  // ⬅️ Change return type
        val anchorNode = AnchorNode(engine = engine, anchor = anchor)

        val modelInstanceItem = if (modelInstance.isNotEmpty()) {
            modelInstance.removeAt(modelInstance.size - 1)
        } else {
            modelLoader.createInstancedModel(model, 10).first()
        }

        val modelNode = ModelNode(
            modelInstance = modelInstanceItem,
            scaleToUnits = 0.2f
        ).apply {
            isEditable = true
        }

        val boundingBox = CubeNode(
            engine = engine,
            size = modelNode.extents,
            center = modelNode.center,
            materialInstance = materialLoader.createColorInstance(Color.White)
        ).apply {
            isVisible = false
        }

        modelNode.addChildNode(boundingBox)
        anchorNode.addChildNode(modelNode)

        listOf(modelNode, anchorNode).forEach {
            it.onEditingChanged = { editingTransforms ->
                boundingBox.isVisible = editingTransforms.isNotEmpty()
            }
        }

        return anchorNode to modelNode  // ⬅️ Return both
    }


    //---------------------------------------IMAGE HANDLING----------------------------
//    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
//    fun createImageAnchorNode(
//        context: Context,
//        engine: Engine,
//        materialLoader: MaterialLoader,
//        anchor: Anchor,
//        drawableResId: Int
//    ): AnchorNode {
//        val anchorNode = AnchorNode(engine = engine, anchor = anchor)
//
//        // Convert drawable to bitmap
//        val bitmap = BitmapFactory.decodeResource(context.resources, drawableResId)
//
//        // Create a material with the bitmap
//        val materialInstance = materialLoader.createImageInstance(bitmap)
//
//        // Calculate size for the plane (keep image ratio)
//        val width = 0.4f
//        val height = width * bitmap.height / bitmap.width
//
//        // Create a flat plane with the image
//        val node = Node(engine).apply {
//            name = "IMG"
//            isEditable = true
//            setMaterialInstance(materialInstance)
//            setShape(io.github.sceneview.geometries.PlaneShape(width, height))
//        }
//
//        anchorNode.addChildNode(node)
//        return anchorNode
//    }







}