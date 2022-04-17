package com.orcharddu.fitty

import android.app.Application
import android.util.Log
import androidx.camera.camera2.Camera2Config
import androidx.camera.core.CameraSelector
import androidx.camera.core.CameraXConfig
import com.orcharddu.fitty.dataholder.DataHolder
import com.orcharddu.fitty.dataholder.Resources
import com.orcharddu.fitty.module.Inference

class FittyApp : Application(), CameraXConfig.Provider {
    override fun getCameraXConfig(): CameraXConfig {
        return CameraXConfig.Builder.fromConfig(Camera2Config.defaultConfig())
            .setAvailableCamerasLimiter(CameraSelector.DEFAULT_BACK_CAMERA)
            .setMinimumLoggingLevel(Log.ERROR).build()
    }

//        private const val INPUT_TENSOR_WIDTH = 256
//        private const val INPUT_TENSOR_HEIGHT = 256
//        private val NORM_MEAN_RGB = floatArrayOf(0F, 0F, 0F)
//        private val NORM_STD_RGB = floatArrayOf(1F, 1F, 1F)
//        private val NORM_MEAN_RGB = TensorImageUtils.TORCHVISION_NORM_MEAN_RGB
//        private val NORM_STD_RGB = TensorImageUtils.TORCHVISION_NORM_MEAN_RGB

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
            modelName = "lite2.ptl",
            tensorWidth = 256,
            tensorHeight = 256,
            normMeanRgb = floatArrayOf(0F, 0F, 0F),
            normStdRgb = floatArrayOf(1F, 1F, 1F)
        )
    }

}