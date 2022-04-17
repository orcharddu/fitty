package com.orcharddu.fitty.ui.camera.controller

import android.annotation.SuppressLint
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.orcharddu.fitty.module.Inference

class CameraAnalyzer(private val callback: (Int) -> Unit) : ImageAnalysis.Analyzer {

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(image: ImageProxy) {
        callback.invoke(Inference.instance().inference(image))
        image.close()
    }
}

