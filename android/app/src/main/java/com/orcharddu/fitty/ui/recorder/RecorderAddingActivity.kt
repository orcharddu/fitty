package com.orcharddu.fitty.ui.recorder

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.orcharddu.fitty.*
import com.orcharddu.fitty.databinding.RecorderActivityAddingBinding
import com.orcharddu.fitty.dataholder.DataHolder
import com.orcharddu.fitty.dataholder.Labels
import com.orcharddu.fitty.dataholder.Resources
import com.orcharddu.fitty.model.Activity
import com.orcharddu.fitty.model.Exercise
import com.orcharddu.fitty.model.Food
import com.orcharddu.fitty.module.Inference
import com.orcharddu.fitty.ui.BaseActivity
import com.orcharddu.fitty.ui.camera.CameraInferenceActivity
import com.orcharddu.fitty.ui.recorder.adapter.CheckableExercisePlanRecyclerViewAdapter
import com.orcharddu.fitty.utils.UIUtils
import com.orcharddu.fitty.utils.Utils
import java.io.File
import java.util.*


class RecorderAddingActivity : BaseActivity() {

    enum class Type {
        CAMERA, INTEGRATED_CAMERA, BARCODE, MANUAL, FAVORITES
    }

    private lateinit var binding : RecorderActivityAddingBinding
    private lateinit var photo: File
    private lateinit var type: Type

    private var favoriteIndex: Int = 0

    private var labelIndex: Int = -1
    private var mealSize = Food.MealSize.MEDIUM
    private val mealSizeMap = mutableMapOf<Int, Food.MealSize>()
    private var foodCover: Bitmap? = null

    private val sliderValueFormatter = { value: Float, valueFrom: Float, valueTo: Float ->
        if (value < valueFrom) valueFrom else if (value > valueTo) valueTo else value
    }

    private val requestPhotoFromCamera =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                inference(Utils.bitmapFromFile(photo.absolutePath))
            } else {
                finish()
            }
        }

    private val requestPhotoFromIntegratedCamera =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                inference(
                    bitmap = Utils.bitmapFromFile(photo.absolutePath),
                    index = result.data?.getIntExtra("labelIndex", -1)?: -1
                )
            } else {
                finish()
            }
        }

    private val requestPhotoFromGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val imageUri = result.data?.data
                imageUri?.let {
                    Utils.bitmapFromUri(applicationContext, it)?.let { x -> inference(x) }
                }
            }
        }


    @SuppressLint("SetTextI18n")
    private fun inference(bitmap: Bitmap, index: Int = -1) {
        binding.apply {
            labelIndex = if (index == -1) {
                Inference.instance().inference(bitmap)
            } else { index }
            val label = Labels.foodLabels[labelIndex].name.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
            }
            inputFood.setText(label)
            inputFood.clearFocus()
            TransitionManager.beginDelayedTransition(scrollview, AutoTransition())
            layoutDetails.setVisibility(View.VISIBLE)
            sliderCalorie.value = Labels.foodLabels[labelIndex].calorie.toFloat() * mealSize.sizeFactor
            foodCover = Utils.bitmapScaled(bitmap)
            imgFood.setImageBitmap(foodCover)
            imgPickPhoto.visibility = View.INVISIBLE
            textPickPhoto.visibility = View.INVISIBLE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RecorderActivityAddingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()

        photo = Utils.mkfiles(File(externalCacheDir, "photo.jpg"))
        type = intent.getSerializableExtra("type") as Type
        when (type) {
            Type.CAMERA -> {
                Intent().also {
                    it.action = MediaStore.ACTION_IMAGE_CAPTURE
                    val providerFile = FileProvider.getUriForFile(binding.root.context,"com.orcharddu.fitty.provider", photo)
                    it.putExtra(MediaStore.EXTRA_OUTPUT, providerFile)
                    requestPhotoFromCamera.launch(it)
                }
            }
            Type.INTEGRATED_CAMERA -> {
                Intent().also {
                    it.setClass(binding.root.context, CameraInferenceActivity::class.java)
                    requestPhotoFromIntegratedCamera.launch(it)
                }
            }
            Type.BARCODE -> { }
            Type.MANUAL -> {
                binding.cardAddPhoto.setOnClickListener {
                    Intent().also {
                        it.action = Intent.ACTION_GET_CONTENT
                        it.type = "image/*"
                        requestPhotoFromGallery.launch(it)
                        binding.layoutDetails.setVisibility(View.GONE)
                    }
                }
            }
            Type.FAVORITES -> {
                binding.cardAddPhoto.setOnClickListener {
                    Intent().also {
                        it.action = Intent.ACTION_GET_CONTENT
                        it.type = "image/*"
                        requestPhotoFromGallery.launch(it)
                        binding.layoutDetails.setVisibility(View.GONE)
                    }
                }
                favoriteIndex = intent.getSerializableExtra("favoriteIndex") as Int
                loadFavorite(DataHolder.instance().user.favorites[favoriteIndex])
            }
        }
    }

    private fun loadFavorite(favorite: Food) {
        binding.apply {
            inputFood.setText(favorite.name)
            inputFood.clearFocus()
            TransitionManager.beginDelayedTransition(scrollview, AutoTransition())
            layoutDetails.setVisibility(View.VISIBLE)
            sliderCalorie.value = favorite.calorie.toFloat()
            mealSize = favorite.size
            println(mealSize)

            chipGroup.check(when (mealSize) {
                Food.MealSize.SMALL -> chipSmall.id
                Food.MealSize.MEDIUM -> chipMedium.id
                Food.MealSize.LARGE -> chipLarge.id
            })
            favorite.picture?.let {
                foodCover = BitmapFactory.decodeByteArray(it, 0, it.size)
            }
            foodCover?.let {
                imgFood.setImageBitmap(Utils.bitmapScaled(it))
                imgPickPhoto.visibility = View.INVISIBLE
                textPickPhoto.visibility = View.INVISIBLE
            }
            switchAddFavorite.isChecked = true
        }
    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    private fun initView() {
        binding.apply {

            btnClose.setOnClickListener { onBackPressed() }

            imgPickPhoto.setImageDrawable(
                UIUtils.drawableWithColorFilter(applicationContext, R.drawable.ic_add_pic, "#B3969696"))

            layoutDetails.visibility = View.GONE
            textFood.setEndIconOnClickListener {
                TransitionManager.beginDelayedTransition(scrollview, AutoTransition())
                layoutDetails.setVisibility(View.GONE)
                inputFood.setText("")
                (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).showSoftInput(inputFood, SHOW_IMPLICIT)
            }

            inputFood.setFocusable(true)
            inputFood.setFocusableInTouchMode(true)
            inputFood.requestFocus()
            inputFood.setOnEditorActionListener { _, i, _ ->
                if ((i == EditorInfo.IME_ACTION_DONE || i == EditorInfo.IME_ACTION_GO) && inputFood.text.toString() != "") {
                    inputFood.clearFocus()
                    TransitionManager.beginDelayedTransition(scrollview, AutoTransition())
                    layoutDetails.setVisibility(View.VISIBLE)
                }
                false
            }

            inputFood.addTextChangedListener {
                if (it.toString() == "") {
                    layoutDetails.setVisibility(View.GONE)
                    TransitionManager.beginDelayedTransition(scrollview, AutoTransition())
                    labelIndex = -1
                }
            }

            val exercisePlans = Labels.exercisePlans.shuffled().subList(0, 4).toList()

            sliderCalorie.addOnChangeListener { _, value, _ ->
                if (labelIndex == -1) {
                    btnSetCalorie.setText("${value.toInt()}kCal")
                } else {
                    btnSetCalorie.setText("${value.toInt()}kCal " +
                            "(${(value / Labels.foodLabels[labelIndex].calorie * Labels.foodLabels[labelIndex].weights).toInt()}gram)")
                }

                exercisePlans.forEach { item ->
                    item.duration = (value / item.calorieFactor).toInt()
                    recyclerviewExercisePlan.adapter?.notifyDataSetChanged()
                }
            }


            sliderCalorie.setLabelFormatter { value: Float ->
                if (labelIndex == -1) {
                    value.toInt().toString().let { "${it}kCal" }
                } else {
                    value.toInt().toString().let {"${value.toInt()}kCal " +
                            "(${(value / Labels.foodLabels[labelIndex].calorie * Labels.foodLabels[labelIndex].weights).toInt()}gram)"
                    }
                }
            }

            sliderCalorie.valueTo = 2000F
            sliderCalorie.value = 10F

            mealSizeMap[binding.chipSmall.id] = Food.MealSize.SMALL
            mealSizeMap[binding.chipMedium.id] = Food.MealSize.MEDIUM
            mealSizeMap[binding.chipLarge.id] = Food.MealSize.LARGE
            chipGroup.setOnCheckedChangeListener { _, checkedId ->
                mealSizeMap[checkedId]?.also {
                    if (labelIndex != -1) {
                        sliderCalorie.value = Labels.foodLabels[labelIndex].calorie.toFloat() * it.sizeFactor
                    }
                    mealSize = it
                }
            }

            switchExercisePlan.setOnCheckedChangeListener { _, checked ->
                when (checked) {
                    true -> {
                        exercisePlans.forEach { plan -> plan.checked = false }
                        recyclerviewExercisePlan.adapter?.notifyDataSetChanged()
                        TransitionManager.beginDelayedTransition(scrollview, AutoTransition())
                        recyclerviewExercisePlan.setVisibility(View.VISIBLE)
                    }
                    false -> {
                        TransitionManager.beginDelayedTransition(scrollview, AutoTransition())
                        recyclerviewExercisePlan.setVisibility(View.GONE)
                    }
                }
            }

            recyclerviewExercisePlan.apply {
                setVisibility(View.GONE)
                setLayoutManager(LinearLayoutManager(context))
                setNestedScrollingEnabled(false)
                setItemAnimator(DefaultItemAnimator())
                setAdapter(CheckableExercisePlanRecyclerViewAdapter(exercisePlans))
            }

            btnAddToMeal.setOnClickListener {
                DataHolder.instance().dailyEvents().exercisePlans.addAll(0,
                    exercisePlans.asSequence().filter { x -> x.checked }.onEach { x -> x.checked = false}.toList())

                Food().apply {
                    type = Activity.Type.FOOD
                    name = inputFood.text.toString()
                    calorie = sliderCalorie.value.toInt()
                    icon = Resources.Icon.FOOD
                    time = timeFormatter.format(now)
                    date = dateFormatter.format(now)
                    if (labelIndex != -1) {
                        weights = (sliderCalorie.value / Labels.foodLabels[labelIndex].calorie * Labels.foodLabels[labelIndex].weights).toInt()
                    }
                    size = mealSize
                    favorite = switchAddFavorite.isChecked
                }.also {
                    DataHolder.instance().dailyEvents().addActivityHistory(it)
                }
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.apply {
            if (switchAddFavorite.isChecked) {
                val food = if (type == Type.FAVORITES) DataHolder.instance().user.favorites[favoriteIndex] else Food()
                food.apply {
                    // modify existing food in favorites
                    type = Activity.Type.FOOD
                    name = inputFood.text.toString()
                    calorie = sliderCalorie.value.toInt()
                    icon = Resources.Icon.FOOD
                    time = timeFormatter.format(now)
                    date = dateFormatter.format(now)
                    if (labelIndex != -1) {
                        weights = (sliderCalorie.value / Labels.foodLabels[labelIndex].calorie * Labels.foodLabels[labelIndex].weights).toInt()
                    }
                    size = mealSize
                    favorite = switchAddFavorite.isChecked
                    foodCover?.let { bitmap ->
                        picture = Utils.bitmapToByteArray(bitmap)
                    }
                }.takeIf {
                    switchAddFavorite.isChecked && type != Type.FAVORITES
                }?.also {
                    // add the food to favorites
                    DataHolder.instance().user.favorites.add(it)
                }
            }
            if (!switchAddFavorite.isChecked && type == Type.FAVORITES) {
                DataHolder.instance().user.favorites.removeAt(favoriteIndex)
            }
        }
        DataHolder.instance().save(applicationContext)
        foodCover?.recycle()
    }
}



