@file:Suppress("UNCHECKED_CAST")

package net.johnpgr.craftingtableiifabric.util

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.minecraft.resource.Resource
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.nio.file.Files

fun Resource.loadJsonToMap(): HashMap<String, String> {
    val json = inputStream.bufferedReader().use { it.readText() }
    return Gson().fromJson(json, HashMap::class.java) as HashMap<String, String>
}

fun Map<String, String>.writeToJsonFile(file: File) {
    val gson = GsonBuilder().setPrettyPrinting().create()
    BufferedWriter(FileWriter(file)).use { it.write(gson.toJson(this)) }
}

fun File.readJsonAsMap(): HashMap<String, String> {
    val gson = Gson()
    return Files.newBufferedReader(toPath()).use { reader ->
        gson.fromJson(reader, HashMap::class.java) as HashMap<String, String>
    }
}