package com.example.kotlininstagramapp.Generic

import android.os.Bundle
import android.view.*
import android.widget.TextView
import com.example.kotlininstagramapp.Profile.FirebaseHelper
import com.example.kotlininstagramapp.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
                followStateUIHandler.handleFollowStateUI(isFollowing = false)
                dismiss()
            }

        }

        return view;
    }


    override fun getTheme(): Int {
        return R.style.BottomSheetTransparent
    }


}