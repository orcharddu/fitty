package com.orcharddu.fitty.ui.camera

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.OrientationEventListener
import android.view.Surface
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.orcharddu.fitty.databinding.CameraActivityInferenceBinding
import com.orcharddu.fitty.dataholder.DataHolder
import com.orcharddu.fitty.dataholder.Labels
import com.orcharddu.fitty.ui.BaseActivity
import com.orcharddu.fitty.module.Inference
import com.orcharddu.fitty.utils.Utils
import java.io.File
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraInferenceActivity : BaseActivity() {

    companion object {
        private val PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_CAMERA_PERMISSION = 200
        private const val PREDICT_LENGTH = 5
    }

    private lateinit var binding: CameraActivityInferenceBinding
    private lateinit var cameraExecutor: ExecutorService
    private val predictArray = IntArray(PREDICT_LENGTH) { -1 }
    private var predictListPointer = 0
    private var imageCapture: ImageCapture? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var labelIndex = -1

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CameraActivityInferenceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                PERMISSIONS,
                REQUEST_CODE_CAMERA_PERMISSION
            )
        } else {
            configureCamera()
        }
    }

    private fun initView() {
        binding.apply {
            viewCamera.scaleType = PreviewView.ScaleType.FIT_CENTER
            cardLabel.visibility = View.INVISIBLE
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "Need CAMERA permission.", Toast.LENGTH_LONG).show()
                finish()
            } else {
                configureCamera()
            }
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    @RequiresApi(Build.VERSION_CODES.R)
    private fun configureCamera() {
        cameraExecutor = Executors.newSingleThreadExecutor()
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewCamera.surfaceProvider)
                }
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            imageAnalyzer = ImageAnalysis.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also { analyzer ->
                    analyzer.setAnalyzer(cameraExecutor) { imageProxy ->
                        val result = Inference.instance().inference(imageProxy)
                        runOnUiThread { processResult(result) }
                        Handler(Looper.getMainLooper()).postDelayed({
                            imageProxy.close()
                        }, 150)
                    }
                }
            imageCapture = ImageCapture.Builder().build()
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageAnalyzer, imageCapture)
            } catch (e: Exception) {
                Log.e("TAG", "Use case binding failed", e)
            }

        }, ContextCompat.getMainExecutor(this))
        binding.btnShot.setOnClickListener { takePhoto("photo") }
    }

    private fun showLabel(labelIndex: Int) {
        binding.apply {
            textLabel.text = Labels.foodLabels[labelIndex].name.uppercase(Locale.getDefault())
            cardLabel.visibility = View.VISIBLE
            cardLabel.animate()
                .alpha(1f)
                .setDuration(200)
                .setListener(null)
        }
    }

    private fun hideLabel() {
        binding.apply {
            cardLabel.animate()
                .alpha(0f)
                .setDuration(200)
                .setListener(null)
            cardLabel.visibility = View.INVISIBLE
        }
    }

    private fun processResult(result: Int) {
        predictArray[predictListPointer] = result
        // get the most voted labels of the past 10 predicted labels


        /*
        val sortedArray = predictArray.sorted()
        var left = 0
        var right = PREDICT_LENGTH - 1
        var count = 0
        while (left < right) {
            if (sortedArray[left] != sortedArray[right]) {
                if (count++ % 2 == 0) left++ else right --
            } else {
                break
            }
            count++
        }
        if (right - left > 3 && sortedArray[right] != -1) {
            labelIndex = sortedArray[right]
            showLabel(labelIndex)
        } else {
           hideLabel()
        }*/


        /*
        var count = 0
        predictArray.forEach { x -> if (x == result) count++ }
        if (count > 5) {
            labelIndex = result
            showLabel(labelIndex)
        } else {
            hideLabel()
        }*/

        if (predictArray.all { x -> x == result }) {
            labelIndex = result
            showLabel(labelIndex)
        } else {
            hideLabel()
        }
        predictListPointer = (predictListPointer + 1) % PREDICT_LENGTH
    }

    private fun takePhoto(name: String) {
        val imageCapture = imageCapture?: return
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(Utils.mkfiles(File(externalCacheDir, "$name.jpg")))
            .build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Toast.makeText(baseContext, "Photo capture failed.\nPlease add photo manually.",
                        Toast.LENGTH_LONG).show()
                    setResult(RESULT_CANCELED)
                    finish()
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults){
                    intent.putExtra("labelIndex", labelIndex)
                    setResult(RESULT_OK, intent)
                    finish()
                }
            }
        )
    }

    override fun onStart() {
        super.onStart()
        orientationEventListener.enable()
    }

    override fun onStop() {
        super.onStop()
        orientationEventListener.disable()
    }

    private val orientationEventListener by lazy {
        object : OrientationEventListener(this) {
            override fun onOrientationChanged(orientation: Int) {
                val rotation = when (orientation) {
                    in 45 until 135     -> Surface.ROTATION_270
                    in 135 until 225    -> Surface.ROTATION_180
                    in 225 until 315    -> Surface.ROTATION_90
                    else                -> Surface.ROTATION_0
                }
                imageAnalyzer?.targetRotation = rotation
                imageCapture?.targetRotation = rotation
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}
