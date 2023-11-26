package com.example.kotlininstagramapp.Generic

import Comment
import android.annotation.SuppressLint
import android.opengl.Visibility
import android.os.Bundle
import android.text.Layout
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlininstagramapp.Profile.FirebaseHelper
import com.example.kotlininstagramapp.R
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.w3c.dom.Text

class FollowOptionsBottomSheetFragment(val userId:String, val followStateUIHandler: FollowStateUIHandler) : BottomSheetDialogFragment() {

    lateinit var tv_unfollow:TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_bottom_sheet_followoptions,container,false)

        tv_unfollow = view.findViewById(R.id.tv_unfollow)

        tv_unfollow.setOnClickListener{
            CoroutineScope(Dispatchers.Main).launch {
                withContext(Dispatchers.IO){
                    FirebaseHelper().unfollowUser(userId = userId)
                }
                followStateUIHandler.handleFollowStateUI()
                dismiss()
            }

        }

        return view;
    }


    override fun getTheme(): Int {
        return R.style.BottomSheetTransparent
    }


}