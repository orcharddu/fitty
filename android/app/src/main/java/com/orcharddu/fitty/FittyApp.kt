package com.orcharddu.fitty

import android.app.Application
import android.util.Log
import androidx.camera.camera2.Camera2Config
import androidx.camera.core.CameraSelector
import androidx.camera.core.CameraXConfig
import com.orcharddu.fitty.dataholder.DataHolder
import com.orcharddu.fitty.dataholder.Resources
import com.orcharddu.fitty.module.Inference
import org.pytorch.torchvision.TensorImageUtils

class FittyApp : Application(), CameraXConfig.Provider {

    companion object {
        private const val MODEL_NAME = "model.ptl"
        private const val INPUT_TENSOR_WIDTH = 224
        private const val INPUT_TENSOR_HEIGHT = 224
        private val NORM_MEAN_RGB = TensorImageUtils.TORCHVISION_NORM_MEAN_RGB
        private val NORM_STD_RGB = TensorImageUtils.TORCHVISION_NORM_MEAN_RGB
        private val DEFAULT_NORM_MEAN_RGB = floatArrayOf(0F, 0F, 0F)
        private val DEFAULT_NORM_STD_RGB = floatArrayOf(1F, 1F, 1F)
    }

    override fun getCameraXConfig(): CameraXConfig {
        return CameraXConfig.Builder.fromConfig(Camera2Config.defaultConfig())
            .setAvailableCamerasLimiter(CameraSelector.DEFAULT_BACK_CAMERA)
            .setMinimumLoggingLevel(Log.ERROR).build()
    }


    override fun onCreate() {
        super.onCreate()

        Resources.init(
            context = applicationContext
        )

        DataHolder.init(
            context = applicationContext
        )

        Inference.init(
            context = applicationContext,
            modelName = MODEL_NAME,
            tensorWidth = INPUT_TENSOR_WIDTH,
            tensorHeight = INPUT_TENSOR_HEIGHT,
            normMeanRgb = NORM_MEAN_RGB,
            normStdRgb = NORM_STD_RGB
        )
    }

}