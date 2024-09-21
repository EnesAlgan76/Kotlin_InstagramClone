package com.example.kotlininstagramapp.ui.Profile

import ProgressDialogFragment
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.kotlininstagramapp.Models.UserDetails
import com.example.kotlininstagramapp.Profile.FirebaseHelper
import com.example.kotlininstagramapp.R
import com.example.kotlininstagramapp.utils.DatabaseHelper
import com.example.kotlininstagramapp.utils.EventBusDataEvents
import com.example.kotlininstagramapp.utils.EImageLoader
import dagger.hilt.android.AndroidEntryPoint
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.default
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import id.zelory.compressor.constraint.size
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.io.File
import java.io.IOException
import java.lang.String.format
import javax.inject.Inject

@AndroidEntryPoint
class ProfileEditFragment : Fragment() {
    private var eventBiografi: String=""
    private var eventProfilePicture: String=""
    private var eventuserName: String=""
    private var eventuserFullName: String=""
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    @Inject
    lateinit var databaseHelper: DatabaseHelper

    private lateinit var profilePicture: ImageView
    private var selectedImageUri: Uri?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_edit_profile, container, false)
        initViews(view)
        setupGalleryLauncher()
        return view
    }

     private fun initViews(view: View) {
        val closeButton = view.findViewById<ImageView>(R.id.iv_closeButton)
        val fullName = view.findViewById<EditText>(R.id.fullNameTextField)
        val userNameEditText = view.findViewById<EditText>(R.id.et_userName)
        val biography = view.findViewById<EditText>(R.id.et_biografi)
        val site = view.findViewById<EditText>(R.id.et_site)
        profilePicture = view.findViewById(R.id.iv_profile)
        val saveButton = view.findViewById<ImageView>(R.id.checkButton)
        val changeProfilePhoto = view.findViewById<TextView>(R.id.tv_changePhoto)

         closeButton.setOnClickListener {
             parentFragmentManager.popBackStack()
            // (requireActivity() as ProfileActivity).toggleProfileRootVisibility(true);
         }


        changeProfilePhoto.setOnClickListener {
            val pickIntent = Intent(Intent.ACTION_PICK)
            pickIntent.type = "image/*"
            galleryLauncher.launch(pickIntent)
        }

         saveButton.setOnClickListener {
             val progressDialog = ProgressDialogFragment()
             progressDialog.show(childFragmentManager, "progress_dialog")

             CoroutineScope(Dispatchers.Main).launch {
                 var compressedImageUri: Uri? = null
                 val url: String?

                 if (selectedImageUri != null) {
                     val tempFile = createTempFileFromUri(selectedImageUri!!)

                     // Compress the image using the temporary file
                     val compressedImageFile = Compressor.compress(requireContext(), tempFile) {
                         resolution(1280, 720)
                         quality(80)
                         format(Bitmap.CompressFormat.JPEG)  // Ensure the format is JPEG
                         size(204_800)
                     }
                     compressedImageUri = Uri.fromFile(compressedImageFile)
                 }

                 url = if (userNameEditText.text.toString() != eventuserName) {
                     databaseHelper.updateProfileImage(compressedImageUri, userNameEditText.text.toString())
                 } else {
                     databaseHelper.updateProfileImage(compressedImageUri, eventuserName)
                 }

                 databaseHelper.updateUserProfile(
                     eventuserName,
                     if (fullName.text.toString() != eventuserFullName) fullName.text.toString() else null,
                     if (userNameEditText.text.toString() != eventuserName) userNameEditText.text.toString() else null,
                     if (biography.text.toString() != eventBiografi) biography.text.toString() else null,
                     url
                 ) { message ->
                     Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                 }

                 progressDialog.dismiss()
             }
         }


         fullName.setText(eventuserFullName)
        userNameEditText.setText(eventuserName)
        biography.setText(eventBiografi)
        site.setText("boş")
        EImageLoader.setImage(eventProfilePicture, profilePicture, null)
    }

    private suspend fun createTempFileFromUri(uri: Uri): File = withContext(Dispatchers.IO) {
        val tempFile = File.createTempFile("temp_image", ".jpg", requireContext().cacheDir)
        try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val outputStream = tempFile.outputStream()
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        tempFile
    }


    private fun setupGalleryLauncher() {
        galleryLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    if (data != null) {
                        val imageUri: Uri? = data.data
                        if (imageUri != null) {
                            Glide.with(requireContext()).load(imageUri).into(profilePicture)
                            selectedImageUri = imageUri
                        }
                    }
                }
            }
    }

    override fun onAttach(context: Context) {
        EventBus.getDefault().register(this)
        super.onAttach(context)
    }

    override fun onDetach() {
        EventBus.getDefault().unregister(this)
        super.onDetach()
    }

    @Subscribe(sticky = true)
    fun onTelefonGonderReceived(event: EventBusDataEvents.KullaniciBilgileriGonder) {
        eventBiografi = event.biografi
        eventProfilePicture = event.profilePicture
        eventuserName = event.user_name
        eventuserFullName = event.full_name
    }


}
