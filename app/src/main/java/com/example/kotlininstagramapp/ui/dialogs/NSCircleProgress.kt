package com.example.kotlininstagramapp.ui.dialogs
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import android.widget.ProgressBar
import com.example.kotlininstagramapp.R

class NSCircleProgress(context: Context) : Dialog(context) {

    private var progressBar: ProgressBar? = null

    init {
        // Remove the title from the dialog
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        // Set the custom layout
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.custom_progress_dialog, null)
        setContentView(view)

        // Set the dialog properties
        window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        window?.setBackgroundDrawableResource(android.R.color.transparent) // Transparent background
        setCancelable(false) // Optional: Prevent closing by tapping outside

        // Reference the progress bar
        progressBar = view.findViewById(R.id.nsprogressBar)
    }

    fun showProgress() {
        if (!isShowing) {
            show()
        }
    }

    fun hideProgress() {
        if (isShowing) {
            dismiss()
        }
    }
}
