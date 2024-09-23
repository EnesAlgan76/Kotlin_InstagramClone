package com.example.kotlininstagramapp.Home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.kotlininstagramapp.Generic.UserSingleton
import com.example.kotlininstagramapp.MainActivity
import com.example.kotlininstagramapp.Profile.FirebaseHelper
import com.example.kotlininstagramapp.R
import com.example.kotlininstagramapp.data.model.HomePagePostItem
import com.example.kotlininstagramapp.databinding.FragmentHomeBinding
import com.example.kotlininstagramapp.ui.Home.PostViewModel
import com.example.kotlininstagramapp.utils.BottomNavHandler
import com.example.kotlininstagramapp.utils.DatabaseHelper
import com.example.ns.ui.NSBottomNavView
import com.example.turkiyefinansappclone.video_call.repository.MainService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding
    var allPosts2: ArrayList<HomePagePostItem>  = arrayListOf(HomePagePostItem(0.5,"","","","",0.0,0.0,"",""))
    //story kısmı için fake data
    private var currentPage = 0
    private var isLastPage = false
    private var isLoading = false
    private val postViewModel: PostViewModel by viewModels()
    private lateinit var postAdapter: PostsAdapter

    @Inject
    lateinit var databaseHelper: DatabaseHelper
    @Inject
    lateinit var mainService: MainService

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
        setupChatButton()
        updateUiOnNotificationStatusChange()
        mainService.startService(UserSingleton.userModel!!.userId)

        return binding.root
    }
    


    private fun setupChatButton() {
        binding.ivChat.setOnClickListener {
            findNavController().navigate(R.id.conversationsFragment)
        }
    }

    override fun onDestroy() {
        isBottomNavInitialized = false
        super.onDestroy()
    }


    companion object{var isBottomNavInitialized =false}

    private fun setupBottomNavigation() {
        val bottomNavigationView = requireActivity().findViewById<NSBottomNavView>(R.id.bottomNavigationView)
        bottomNavigationView.visibility = View.VISIBLE
        if(!isBottomNavInitialized){
            isBottomNavInitialized = true
            BottomNavHandler.setupBottomNavBar(requireActivity(),bottomNavigationView, findNavController())
        }


    }

    private fun loadMorePosts() {
        isLoading = true
        postViewModel.getHomePagePosts(currentPage).observe(viewLifecycleOwner) { posts ->
            if (posts.isNotEmpty()) {
                postAdapter.addPosts(posts)
                currentPage++
            } else {
                isLastPage = true
            }
            isLoading = false
        }
    }


    private fun setupRecyclerView() {

        binding.rvHomeFragmentPosts.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL,false)
        postAdapter= PostsAdapter(allPosts2,this, requireActivity().supportFragmentManager,binding.rvHomeFragmentPosts,databaseHelper)
        binding.rvHomeFragmentPosts.adapter=postAdapter

        loadMorePosts()

        binding.rvHomeFragmentPosts.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                // Check if we need to load more data
                if (!isLoading && !isLastPage) {
                    if (
                        visibleItemCount + firstVisibleItemPosition >= totalItemCount &&
                        firstVisibleItemPosition >= 0 &&
                        totalItemCount >= 5) {
                        loadMorePosts()
                    }
                }
            }
        })


        /*CoroutineScope(Dispatchers.Main).launch {
            try {
                withContext(Dispatchers.IO) {
                    allPosts2 = DatabaseHelper().getHomePagePosts()
                }
                allPosts2.add(0, HomePagePostItem(0.5,"","","","",0.0,0.0,"",""))

                binding.rvHomeFragmentPosts.adapter=postAdapter
            } catch (e: Exception) {
                println("Error fetching posts: ${e.message}")
            }
        }*/

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

    override fun onPause() {
        super.onPause()
        // Pause all video players when the fragment is paused
        postAdapter.pauseAllVideos()
    }

    override fun onStop() {
        super.onStop()
        // Stop or release all video players when the fragment is stopped
        postAdapter.stopAllVideos()
    }


}

