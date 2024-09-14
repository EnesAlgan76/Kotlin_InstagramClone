package com.example.kotlininstagramapp.Home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainer
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlininstagramapp.Profile.FirebaseHelper
import com.example.kotlininstagramapp.R
import com.example.kotlininstagramapp.data.model.HomePagePostItem
import com.example.kotlininstagramapp.ui.Profile.ProfileFragment
import com.example.kotlininstagramapp.ui.Search.SearchFragment
import com.example.kotlininstagramapp.ui.Share.ShareFragment
import com.example.kotlininstagramapp.utils.BottomNavHandler
import com.example.kotlininstagramapp.utils.DatabaseHelper
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var ivDirectMessage: ImageView
    private lateinit var ivNotifications: ImageView
    private lateinit var ivRedPoint: ImageView
    private var auth = FirebaseAuth.getInstance()
    lateinit var recyclerView: RecyclerView
    var allPosts2: ArrayList<HomePagePostItem>  = ArrayList()
    //private lateinit var navController: NavController

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
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        bottomNavigationView = view.findViewById(R.id.bottomNavigationView)
        ivDirectMessage = view.findViewById(R.id.iv_direct_message)
        ivNotifications = view.findViewById(R.id.iv_notifications)
        ivRedPoint = view.findViewById(R.id.iv_redPoint)
        recyclerView = view.findViewById(R.id.rv_homeFragment_posts)
        setupRecyclerView()

        setupBottomNavigation()
        setupClickListeners()

        updateUiOnNotificationStatusChange()

        return view
    }

    private fun setupBottomNavigation() {
        BottomNavHandler.setupBottomNavBar(bottomNavigationView,requireActivity(),findNavController())

    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(requireContext(),
            LinearLayoutManager.VERTICAL,false)
        CoroutineScope(Dispatchers.Main).launch {
            try {
                withContext(Dispatchers.IO) {
                    // allPosts = FirebaseHelper().getAllPosts()
                    allPosts2 = DatabaseHelper().getHomePagePosts()
                }
                // Invalid data to replace story view at index 0
                allPosts2.add(0, HomePagePostItem(0.5,"","","","","",0.0,"",""))
                val adapter= PostsAdapter(allPosts2,requireContext(), requireActivity().supportFragmentManager,recyclerView)
                recyclerView.adapter=adapter
            } catch (e: Exception) {
                println("Error fetching posts: ${e.message}")
            }
        }

    }



    private fun setupClickListeners() {
        ivDirectMessage.setOnClickListener {
            // Handle direct message click
        }

        ivNotifications.setOnClickListener {
            startActivity(Intent(requireContext(), NotificationsActivity::class.java))
            removeNotificationRedPoint()
            updateUiOnNotificationStatusChange()
        }
    }






    private fun updateUiOnNotificationStatusChange() {
        FirebaseHelper().listenForNotificationsAndChanges { callback ->
            if (callback) {
                showNotificationRedPoint()
            }
        }
    }

    private fun showNotificationRedPoint() {
        ivRedPoint.visibility = View.VISIBLE
    }

    private fun removeNotificationRedPoint() {
        ivRedPoint.visibility = View.INVISIBLE
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

