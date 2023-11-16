package com.example.kotlininstagramapp.Home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlininstagramapp.Models.Comment
import com.example.kotlininstagramapp.R

class CommentBottomSheetFragment : DialogFragment() {

    var comments:ArrayList<Comment> = arrayListOf()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_bottom_sheet_comments,container,false)
        val rootView = view.findViewById<View>(R.id.bottom_sheet_root)
        rootView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                // Store the initial touch position if needed for calculations
            } else if (event.action == MotionEvent.ACTION_UP) {
                // Detect the downward swipe gesture
                val initialY = 2
                val currentY = event.rawY
                val deltaY = currentY - initialY
                if (deltaY > 0 && deltaY > 100) { // Adjust the threshold as needed
                    dismiss()
                }
            }
            true
        }

        return view;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        comments.add(Comment("kdfjh","Bu yorum test amaçlıdır","44",
            "efw","https://marketplace.canva.com/EAFXS8-cvyQ/1/0/1600w/canva-brown-and-light-brown%2C-circle-framed-instagram-profile-picture-2PE9qJLmPac.jpg",
        "2s","ensalgn"))

        var recycleView = view.findViewById<RecyclerView>(R.id.rv_comments)

        var commentAdapter: CommentsAdapter = CommentsAdapter(view.context,comments)
        recycleView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL,false)

        recycleView.adapter = commentAdapter

    }

    override fun getTheme(): Int {
        return R.style.BottomSheetTransparent
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog?.window?.setGravity(Gravity.BOTTOM)
    }
}