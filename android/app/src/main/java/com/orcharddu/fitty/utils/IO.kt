package com.orcharddu.fitty.utils

import android.content.Context
import java.io.*

object IO {

    fun loadSerializedData(context: Context, name: String): Serializable? {
        val serializable: Serializable
        val file: File
        val fileInputStream: FileInputStream
        return try {
            file = File(context.getExternalFilesDir(""), name)
            fileInputStream = FileInputStream(file)
            val objectInputStream = ObjectInputStream(fileInputStream)
            serializable = objectInputStream.readObject() as Serializable
            objectInputStream.close()
            fileInputStream.close()
            serializable
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun saveSerializedData(context: Context, serializable: Serializable, name: String): Boolean {
        val file = File(context.getExternalFilesDir(""), name)
        val fileOutputStream: FileOutputStream
        return try {
            if (!file.exists()) file.createNewFile()
            fileOutputStream = FileOutputStream(file)
            val objectOutputStream = ObjectOutputStream(fileOutputStream)
            objectOutputStream.writeObject(serializable)
            objectOutputStream.flush()
            objectOutputStream.close()
            fileOutputStream.close()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}