package com.example.arlearner.util

import android.content.SharedPreferences
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@Serializable
data class ARObjectData(
    val model: String,
    val x: Float,
    val y: Float,
    val z: Float,
    val scaleX: Float,
    val scaleY: Float,
    val scaleZ: Float
)

object ARObjectStorage {
    private const val PREF_NAME = "ARObjects"
    private const val KEY_OBJECTS = "SavedObjects"

    fun saveObjects(objects: List<ARObjectData>, sharedPreferences: SharedPreferences) {
        val json = Json.encodeToString(objects)
        sharedPreferences.edit().putString(KEY_OBJECTS, json).apply()
    }

    fun loadObjects(sharedPreferences: SharedPreferences): List<ARObjectData> {
        val json = sharedPreferences.getString(KEY_OBJECTS, null) ?: return emptyList()
        return try {
            Json.decodeFromString(json)
        } catch (e: Exception) {
            emptyList()
        }
    }
}
