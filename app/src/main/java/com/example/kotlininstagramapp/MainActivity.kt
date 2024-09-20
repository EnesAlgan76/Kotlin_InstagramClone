package com.example.kotlininstagramapp

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.yalantis.ucrop.UCrop
import dagger.hilt.android.AndroidEntryPoint
import java.io.File


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController

    companion object{
        const val PICK_IMAGE_REQUEST_CODE = 1001
        const val UCROP_REQUEST_CODE = 2002
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navController = findNavController(R.id.nav_host_fragment)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PICK_IMAGE_REQUEST_CODE -> handleImageSelection(data)
                UCROP_REQUEST_CODE -> handleCropResult(data)
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            handleCropError(data)
        }
    }

    private fun handleImageSelection(data: Intent?) {
        val sourceUri: Uri? = data?.data
        sourceUri?.let { uri ->
            val contentResolver = applicationContext.contentResolver
            val mimeType = contentResolver.getType(uri)

            if (mimeType != null) {
                when {
                    mimeType.startsWith("image/") -> startImageCropping(uri)
                    mimeType.startsWith("video/") -> navigateToVideoPreview(uri)
                    else -> {
                        Toast.makeText(this, getString(R.string.unsupported_file_type), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } ?: run {
            Toast.makeText(this, getString(R.string.image_selection_error), Toast.LENGTH_SHORT).show()
        }
    }

    private fun startImageCropping(sourceUri: Uri) {
        val destinationUri = Uri.fromFile(File(cacheDir, "cropped_image.jpg"))
        val options = UCrop.Options().apply {
            setActiveControlsWidgetColor(ContextCompat.getColor(this@MainActivity, R.color.nscolorSecondary))
        }

        UCrop.of(sourceUri, destinationUri)
            .withOptions(options)
            .withAspectRatio(1f, 1f)
            .withMaxResultSize(1000, 1000)
            .start(this@MainActivity, UCROP_REQUEST_CODE)
    }

    private fun navigateToVideoPreview(sourceUri: Uri) {
        val bundle = Bundle().apply {
            putString("uri", sourceUri.toString())
        }
        navController.navigate(R.id.trimVideoFragment, bundle)
    }

    private fun handleCropResult(data: Intent?) {
        val resultUri: Uri? = UCrop.getOutput(data!!)
        resultUri?.let { uri ->
            val bundle = Bundle().apply {
                putString("uri", uri.toString())
            }
            navController.navigate(R.id.shareNextFragment, bundle)
        } ?: run {
            Toast.makeText(this, getString(R.string.crop_result_error), Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleCropError(data: Intent?) {
        val cropError = UCrop.getError(data!!)
        cropError?.let {
            it.printStackTrace()
            showErrorDialog(it.localizedMessage ?: getString(R.string.crop_error_message))
        }
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.error))
            .setMessage(message)
            .setPositiveButton(android.R.string.ok, null)
            .show()
    }




}
