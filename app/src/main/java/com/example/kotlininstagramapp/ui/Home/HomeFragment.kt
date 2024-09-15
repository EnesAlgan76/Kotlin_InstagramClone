package com.example.kotlininstagramapp.Home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlininstagramapp.MainActivity
import com.example.kotlininstagramapp.Profile.FirebaseHelper
import com.example.kotlininstagramapp.R
import com.example.kotlininstagramapp.data.model.HomePagePostItem
import com.example.kotlininstagramapp.databinding.FragmentHomeBinding
import com.example.kotlininstagramapp.utils.BottomNavHandler
import com.example.kotlininstagramapp.utils.DatabaseHelper
import com.example.ns.ui.NSBottomNavView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding
    var allPosts2: ArrayList<HomePagePostItem>  = ArrayList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val fragmentManager = requireActivity().supportFragmentManager
                if (fragmentManager.backStackEntryCount > 1) {
                    fragmentManager.popBackStack()
                } else {
                    requireActivity().finish()
                }
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        setupRecyclerView()
        setupBottomNavigation()
      //  setupClickListeners()
        updateUiOnNotificationStatusChange()

        return binding.root
    }



    companion object{var isBottomNavInitialized =false}
    private fun setupBottomNavigation() {
        if(!isBottomNavInitialized){
            isBottomNavInitialized = true
            val bottomNavigationView = requireActivity().findViewById<NSBottomNavView>(R.id.bottomNavigationView)
            bottomNavigationView.visibility = View.VISIBLE
            BottomNavHandler.setupBottomNavBar(bottomNavigationView, findNavController())
        }


    }


    private fun setupRecyclerView() {
        binding.rvHomeFragmentPosts.layoutManager = LinearLayoutManager(requireContext(),
            LinearLayoutManager.VERTICAL,false)
        CoroutineScope(Dispatchers.Main).launch {
            try {
                withContext(Dispatchers.IO) {
                    allPosts2 = DatabaseHelper().getHomePagePosts()
                }
                allPosts2.add(0, HomePagePostItem(0.5,"","","","","",0.0,"",""))
                val adapter= PostsAdapter(allPosts2,requireContext(), requireActivity().supportFragmentManager,binding.rvHomeFragmentPosts)
                binding.rvHomeFragmentPosts.adapter=adapter
            } catch (e: Exception) {
                println("Error fetching posts: ${e.message}")
            }
        }

    }



    /*private fun setupClickListeners() {
        binding.ivDirectMessage.setOnClickListener {
            // Handle direct message click
        }

        binding.ivNotifications.setOnClickListener {
           // startActivity(Intent(requireContext(), NotificationsActivity::class.java))
            removeNotificationRedPoint()
            updateUiOnNotificationStatusChange()
        }
    }*/






    private fun updateUiOnNotificationStatusChange() {
        FirebaseHelper().listenForNotificationsAndChanges { newNotification ->
            if (newNotification) {
                showNotificationRedPoint()
            }else{
                removeNotificationRedPoint()
            }
        }
    }

    private fun showNotificationRedPoint() {
        val bottomNavigationView = (activity as MainActivity).findViewById<NSBottomNavView>(R.id.bottomNavigationView)
        bottomNavigationView.updateMenuItemIcon(3,R.drawable.bell_notified,R.drawable.bell_notified)
    }

    private fun removeNotificationRedPoint() {
        val bottomNavigationView = (activity as MainActivity).findViewById<NSBottomNavView>(R.id.bottomNavigationView)
        bottomNavigationView.updateMenuItemIcon(3,R.drawable.bell_active,R.drawable.bell)
    }

   /* override fun onBackPressed() {
        if (navController.currentDestination?.id != R.id.feedFragment) {
            bottomNavigationView.selectedItemId = R.id.menu_item_home
        } else {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }*/


}

