package com.example.kotlininstagramapp

import android.R.attr
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.kotlininstagramapp.ui.Share.ShareLastFragment
import com.example.kotlininstagramapp.ui.Share.ShareNextFragment
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


    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE_REQUEST_CODE) {
            val sourceUri: Uri? = data?.data
            val contentResolver = applicationContext.contentResolver
            val mimeType = contentResolver.getType(sourceUri!!)

            if (mimeType != null && mimeType.startsWith("image/")) {
                val destinationUri = Uri.fromFile(File(cacheDir, "cropped_image.jpg"))
                val options = UCrop.Options()
                options.setToolbarTitle("Resmi Kırp")
                options.setActiveControlsWidgetColor(ContextCompat.getColor(this, R.color.nscolorSecondary))

                sourceUri.let {
                    UCrop.of(it, destinationUri)
                        .withOptions(options)
                        .withAspectRatio(1f, 1f)
                        .withMaxResultSize(1000, 1000)
                        .start(this@MainActivity, UCROP_REQUEST_CODE)
                }

            } else if (mimeType != null && mimeType.startsWith("video/")) {
                // This is a video file, handle video operation
              //  handleVideoOperation(sourceUri)
            }


        } else if (resultCode == Activity.RESULT_OK && requestCode == UCROP_REQUEST_CODE) {
            val resultUri = UCrop.getOutput(data!!)
            val bundle = Bundle().apply {
                putString("uri", resultUri.toString())
            }
            navController.navigate(R.id.shareNextFragment, bundle)

        } else if (resultCode == UCrop.RESULT_ERROR) {
            // Handle crop error
            val cropError = UCrop.getError(data!!)
            cropError?.printStackTrace()
        }
    }



}
