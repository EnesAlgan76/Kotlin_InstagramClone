package com.example.kotlininstagramapp.Home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlininstagramapp.databinding.FragmentNotificationsBinding
import com.example.kotlininstagramapp.utils.DatabaseHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class NotificationsFragment : Fragment() {
    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!
    private lateinit var mContext: Context
    @Inject
    lateinit var databaseHelper: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        mContext = requireContext()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.notificationList.layoutManager = LinearLayoutManager(mContext)

        CoroutineScope(Dispatchers.Main).launch {
            val notificationList = withContext(Dispatchers.IO) {
                // FirebaseHelper().getNotifications()
                databaseHelper.getNotifications()
            }
            val adapter = NotificationAdapter(mContext, notificationList, databaseHelper)
            binding.notificationList.adapter = adapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
