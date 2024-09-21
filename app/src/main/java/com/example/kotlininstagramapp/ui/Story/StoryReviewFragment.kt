package com.example.kotlininstagramapp.ui.Story

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.kotlininstagramapp.Generic.UserSingleton
import com.example.kotlininstagramapp.R
import com.example.kotlininstagramapp.databinding.FragmentStoryReviewBinding
import com.example.kotlininstagramapp.ui.dialogs.NSCircleProgress
import com.example.kotlininstagramapp.utils.DatabaseHelper
import com.example.kotlininstagramapp.utils.FiltersAdapter
import dagger.hilt.android.AndroidEntryPoint
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import jp.co.cyberagent.android.gpuimage.GPUImage
import jp.co.cyberagent.android.gpuimage.filter.GPUImageAddBlendFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageBilateralBlurFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageBrightnessFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageCGAColorspaceFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageChromaKeyBlendFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageColorInvertFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageColorMatrixFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageContrastFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageCrosshatchFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageDissolveBlendFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageEmbossFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageExclusionBlendFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFalseColorFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilterGroup
import jp.co.cyberagent.android.gpuimage.filter.GPUImageGammaFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageGaussianBlurFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageGrayscaleFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageHalftoneFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageHazeFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageHighlightShadowFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageHueFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageLookupFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageLuminanceFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageMonochromeFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageMultiplyBlendFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageNonMaximumSuppressionFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageNormalBlendFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageOpacityFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImagePixelationFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImagePosterizeFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSaturationFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageScreenBlendFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSepiaToneFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSketchFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSmoothToonFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSolarizeFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSourceOverBlendFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSubtractBlendFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSwirlFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageThresholdEdgeDetectionFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageToneCurveFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageToonFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageVignetteFilter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@AndroidEntryPoint
class StoryReviewFragment : Fragment() {

    lateinit var binding: FragmentStoryReviewBinding
    lateinit var nsdialog:NSCircleProgress

    @Inject
    lateinit var databaseHelper: DatabaseHelper
    lateinit var gelenDosya: File

    private lateinit var gpuImage: GPUImage
    private lateinit var originalBitmap: Bitmap
    private lateinit var thumbnailBitmap: Bitmap


    private val filters = listOf(
        Pair(GPUImageGrayscaleFilter(), "Grayscale"),
        Pair(GPUImageSepiaToneFilter(), "Sepia"),
        Pair(GPUImageVignetteFilter(), "Vignette"),
        Pair(GPUImageToonFilter(), "Toon"),
        Pair(GPUImageSketchFilter(), "Sketch"),
        Pair(GPUImageContrastFilter(), "Contrast"),
        Pair(GPUImageBrightnessFilter(), "Brightness"),
        Pair(GPUImageSaturationFilter(), "Saturation"),
        Pair(GPUImageGaussianBlurFilter(), "Gaussian Blur"),
        Pair(GPUImageEmbossFilter(), "Emboss"),
        Pair(GPUImageHueFilter(), "Hue"),
        Pair(GPUImagePixelationFilter(), "Pixelation"),
        Pair(GPUImageColorInvertFilter(), "Color Invert"),
        Pair(GPUImageGammaFilter(), "Gamma"),
        Pair(GPUImageMonochromeFilter(), "Monochrome"),
        Pair(GPUImageOpacityFilter(), "Opacity"),
        Pair(GPUImageCrosshatchFilter(), "Crosshatch"),
        Pair(GPUImageLookupFilter(), "Lookup"),
        Pair(GPUImageToneCurveFilter(), "Tone Curve"),
        Pair(GPUImageSmoothToonFilter(), "Smooth Toon"),
        Pair(GPUImageSwirlFilter(), "Swirl"),
        Pair(GPUImageHalftoneFilter(), "Halftone"),
        Pair(GPUImageNonMaximumSuppressionFilter(), "Non-Max Suppression"),
        Pair(GPUImageDissolveBlendFilter(), "Dissolve Blend"),
        Pair(GPUImageCGAColorspaceFilter(), "CGA Colorspace"),
        Pair(GPUImageFilterGroup(), "Filter Group"),
        Pair(GPUImageHighlightShadowFilter(), "Highlight Shadow"),
        Pair(GPUImageExclusionBlendFilter(), "Exclusion Blend"),
        Pair(GPUImageColorMatrixFilter(), "Color Matrix"),
        Pair(GPUImageFalseColorFilter(), "False Color"),
        Pair(GPUImageLuminanceFilter(), "Luminance"),
        Pair(GPUImageHazeFilter(), "Haze"),
        Pair(GPUImageSourceOverBlendFilter(), "Source Over Blend"),
        Pair(GPUImageAddBlendFilter(), "Add Blend"),
        Pair(GPUImageSubtractBlendFilter(), "Subtract Blend"),
        Pair(GPUImageMultiplyBlendFilter(), "Multiply Blend"),
        Pair(GPUImageScreenBlendFilter(), "Screen Blend"),
        Pair(GPUImageFilterGroup(), "Filter Group")
    )





    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentStoryReviewBinding.inflate(inflater, container, false)
        nsdialog = NSCircleProgress(requireContext())

        gpuImage = GPUImage(requireContext())
        val filePath = requireArguments().getString("FILE_PATH")
        if (filePath != null) {
            gelenDosya = File(filePath)
            originalBitmap = BitmapFactory.decodeFile(gelenDosya.absolutePath)
            gpuImage.setImage(originalBitmap)
        }
        thumbnailBitmap = Bitmap.createScaledBitmap(originalBitmap, 200, 200, false)
        setupRecyclerView()

        binding.ivSendStory.setOnClickListener {
            sendFilteredImage()
        }


        return binding.root
    }

    private fun setupRecyclerView() {
        val filtersAdapter = FiltersAdapter(filters, thumbnailBitmap) { selectedFilter ->
            applyFilter(selectedFilter)
        }
        binding.rvFilters.adapter = filtersAdapter
        binding.rvFilters.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    private fun applyFilter(filter: GPUImageFilter) {
        gpuImage.setFilter(filter)
        binding.ivStoryReview.setImageBitmap(gpuImage.bitmapWithFilterApplied)
    }

    private fun sendFilteredImage() {
        nsdialog.showProgress()
        CoroutineScope(Dispatchers.IO).launch {
            val filteredBitmap = gpuImage.bitmapWithFilterApplied

            val filteredImageFile = File(requireContext().cacheDir, "filtered_image.jpg")
            FileOutputStream(filteredImageFile).use { out ->
                filteredBitmap.compress(Bitmap.CompressFormat.JPEG, 50, out)
            }

            val compressedImageFile = Compressor.compress(requireContext(), File(gelenDosya.absolutePath)) {
                quality(80)
            }

            /*databaseHelper.addStory(
                compressedImageFile,
                status = { Log.e("", "Status ____>>> $it") },
                progress = {
                    if (it == 100) {
                        nsdialog.hideProgress()
                        findNavController().navigate(R.id.action_storyReviewFragment_to_homeFragment)
                    }
                }
            )*/
        }
    }


}
