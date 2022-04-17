package com.orcharddu.fitty.module

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.media.Image
import androidx.camera.core.ImageProxy
import com.orcharddu.fitty.utils.Utils
import org.pytorch.IValue
import org.pytorch.LiteModuleLoader
import org.pytorch.Module
import org.pytorch.Tensor
import org.pytorch.torchvision.TensorImageUtils


class Inference private constructor (
    private val model: Module,
    val tensorWidth: Int,
    val tensorHeight:Int,
    val normMeanRgb: FloatArray,
    val normStdRgb: FloatArray
) {
    private val tensorBuffer =
        Tensor.allocateFloatBuffer(3 * tensorHeight * tensorWidth)
    private val tensorInput = Tensor.fromBlob(
        tensorBuffer,
        longArrayOf(1, 3, tensorHeight.toLong(), tensorWidth.toLong())
    )

    companion object {
        @Volatile private var instance: Inference? = null
        fun init(context: Context,
                 modelName: String,
                 tensorWidth: Int,
                 tensorHeight:Int,
                 normMeanRgb: FloatArray,
                 normStdRgb: FloatArray
        ): Inference =
            instance ?: synchronized(this) {
                instance ?: Inference(
                    model = LiteModuleLoader.load(Utils.assetFilePath(context, modelName)),
                    tensorWidth = tensorWidth,
                    tensorHeight = tensorHeight,
                    normMeanRgb = normMeanRgb,
                    normStdRgb = normStdRgb
                ).also {
                    instance = it
                }
            }

        // should be invoked after init
        fun instance(): Inference = instance!!
    }

    fun inference(bitmap: Bitmap): Int {
        val scaledBitmap = Utils.bitmapCenterCrop(bitmap, tensorWidth, tensorHeight)
//        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, tensorWidth, tensorHeight, false)
        val tensorInput = TensorImageUtils.bitmapToFloat32Tensor(
            scaledBitmap,
            normMeanRgb,
            normStdRgb,
        )
        val outputTensor = model.forward(IValue.from(tensorInput)).toTensor()
        val scores = outputTensor.dataAsFloatArray
        var maxScore = Float.MIN_VALUE
        var maxScoreIdx = -1
        for (i in scores.indices) {
            if (scores[i] > maxScore) {
                maxScore = scores[i]
                maxScoreIdx = i
            }
        }
        scaledBitmap.recycle()
        return maxScoreIdx
    }


    @SuppressLint("UnsafeOptInUsageError")
    fun inference(imageProxy: ImageProxy) = inference(imageProxy.image!!, imageProxy.imageInfo.rotationDegrees)

    fun inference(image: Image, rotation: Int): Int {
        TensorImageUtils.imageYUV420CenterCropToFloatBuffer(
            image,
            0,
            tensorWidth,
            tensorHeight,
            normMeanRgb,
            normStdRgb,
            tensorBuffer,
            0
        )
        val outputTensor = model.forward(IValue.from(tensorInput)).toTensor()
        val scores = outputTensor.dataAsFloatArray
        var maxScore = -Float.MAX_VALUE
        var maxScoreIdx = -1
        for (i in scores.indices) {
            if (scores[i] > maxScore) {
                maxScore = scores[i]
                maxScoreIdx = i
            }
        }
        return maxScoreIdx
    }
}