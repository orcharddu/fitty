package com.orcharddu.fitty.utils

import android.content.Context
import android.graphics.*

import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.exifinterface.media.ExifInterface
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*
import kotlin.math.max


object Utils {
    fun doubleToStringWithOneDecimal(number: Double): String {
        val format = DecimalFormat("#.#")
        format.roundingMode = RoundingMode.FLOOR
        return format.format(number)
    }

    fun assetFilePath(context: Context, assetName: String): String? {
        val file = File(context.filesDir, assetName)
        if (file.exists() && file.length() > 0) {
            return file.absolutePath
        }
        try {
            context.assets.open(assetName).use { `is` ->
                FileOutputStream(file).use { os ->
                    val buffer = ByteArray(4 * 1024)
                    var read: Int
                    while (`is`.read(buffer).also { read = it } != -1) {
                        os.write(buffer, 0, read)
                    }
                    os.flush()
                }
                return file.absolutePath
            }
        } catch (e: IOException) {
        }
        return null
    }

    fun mkdirs(file: File): File {
        if (!file.exists()) file.mkdirs()
        return file
    }

    fun mkfiles(file: File): File {
        if (!file.exists()) file.createNewFile()
        return file
    }

    fun bitmapFromUri(context: Context, uri: Uri): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            bitmap = if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            } else {
                val source: ImageDecoder.Source =
                    ImageDecoder.createSource(context.contentResolver, uri)
                ImageDecoder.decodeBitmap(source).copy(Bitmap.Config.RGBA_F16, true)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return bitmap
    }

    fun bitmapFromFile(path: String): Bitmap {
        val exif = ExifInterface(path)
        val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1)
        val src = BitmapFactory.decodeFile(path)
        val dst = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> {
                val matrix = Matrix()
                matrix.postRotate(90F)
                val rotated = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true)
                src.recycle()
                rotated
            }

            ExifInterface.ORIENTATION_ROTATE_180 -> {
                val matrix = Matrix()
                matrix.postRotate(180F)
                val rotated = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true)
                src.recycle()
                rotated
            }

            ExifInterface.ORIENTATION_ROTATE_270 -> {
                val matrix = Matrix()
                matrix.postRotate(270F)
                val rotated = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true)
                src.recycle()
                rotated
            }
            else -> src
        }
        return dst
    }

    fun bitmapCenterCrop(src: Bitmap, width: Int, height: Int): Bitmap {
        val xScale: Float = width.toFloat() / src.width
        val yScale: Float = height.toFloat() / src.height
        val scale = max(xScale, yScale)
        val scaledWidth: Float = scale * src.width
        val scaledHeight: Float = scale * src.height
        val dx: Float = (width - scaledWidth) / 2
        val dy: Float = (height - scaledHeight) / 2
        val dst = Bitmap.createBitmap(width, height, src.getConfig())
        val canvas = Canvas(dst)
        val matrix = Matrix()
        matrix.postScale(scale, scale)
        matrix.postTranslate(dx, dy)
        canvas.drawBitmap(src, matrix, null)
        return dst
    }

    fun bitmapScaled(src: Bitmap): Bitmap {
         val maxSize = 1500
         val dst = if (src.width > maxSize || src.height > maxSize) {
             val scale = src.width.toFloat() / src.height
             if (src.width > src.height) {
                 Bitmap.createScaledBitmap(src, maxSize, (maxSize / scale).toInt(), false)
             } else {
                 Bitmap.createScaledBitmap(src, (maxSize * scale).toInt(), maxSize, false)
             }
        } else { src }
        if (src != dst) {
            src.recycle()
        }
        return dst
    }

    fun bitmapToByteArray(src: Bitmap): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        src.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        byteArrayOutputStream.close()
        return byteArray
    }

    fun getCurrentDateInLong(): Long {
//        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).also {
//            return it.parse(it.format(Date(System.currentTimeMillis()))).time
//        }
        Calendar.getInstance().also {
            it.setTime(Date(System.currentTimeMillis()))
            it[Calendar.HOUR_OF_DAY] = 0
            it[Calendar.MINUTE] = 0
            it[Calendar.SECOND] = 0
            it[Calendar.MILLISECOND] = 0
            return it.time.time
        }
    }

    fun getDateInLong(year: Int, month: Int, day: Int): Long {
        Calendar.getInstance().also {
            it[Calendar.YEAR] = year
            it[Calendar.MONTH] = month
            it[Calendar.DAY_OF_MONTH] = day
            it[Calendar.HOUR_OF_DAY] = 0
            it[Calendar.MINUTE] = 0
            it[Calendar.SECOND] = 0
            it[Calendar.MILLISECOND] = 0
            return it.time.time
        }
    }

}