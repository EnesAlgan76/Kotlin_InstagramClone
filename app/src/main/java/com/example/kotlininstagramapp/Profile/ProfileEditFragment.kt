package com.example.kotlininstagramapp.Profile

import ProgressDialogFragment
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.kotlininstagramapp.Models.UserDetails
import com.example.kotlininstagramapp.R
import com.example.kotlininstagramapp.utils.EventBusDataEvents
import com.example.kotlininstagramapp.utils.EImageLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class ProfileEditFragment : Fragment() {
    private var eventuserDetails: UserDetails =UserDetails()
    private var eventuserName: String=""
    private var eventuserFullName: String=""
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    private lateinit var firebaseHelper: FirebaseHelper
    private lateinit var profilePicture: ImageView
    private var selectedImageUri: Uri?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_edit_profile, container, false)

        firebaseHelper = FirebaseHelper()
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
             (requireActivity() as ProfileActivity).toggleProfileRootVisibility(true);
         }


        changeProfilePhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            galleryLauncher.launch(intent)
        }

         saveButton.setOnClickListener {
             val progressDialog = ProgressDialogFragment()
             progressDialog.show(childFragmentManager, "progress_dialog")

             GlobalScope.launch(Dispatchers.Main) {
                 firebaseHelper.updateUserProfile(
                     eventuserName,
                     if (fullName.text.toString() != eventuserFullName) fullName.text.toString() else null,
                     if (userNameEditText.text.toString() != eventuserName) userNameEditText.text.toString() else null,
                     if (biography.text.toString() != eventuserDetails.biography) biography.text.toString() else null,
                     selectedImageUri,
                 )

                 progressDialog.dismiss()
             }
         }

         fullName.setText(eventuserFullName)
        userNameEditText.setText(eventuserName)
        biography.setText(eventuserDetails.biography)
        site.setText("boş")
        EImageLoader.setImage(eventuserDetails.profilePicture, profilePicture, null)
    }

    private fun setupGalleryLauncher() {
        galleryLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    if (data != null) {
                        val imageUri: Uri? = data.data
                        if (imageUri != null) {
                            profilePicture.setImageURI(imageUri)
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
        eventuserDetails = event.userDetails
        eventuserName = event.user_name
        eventuserFullName = event.full_name
    }
}
