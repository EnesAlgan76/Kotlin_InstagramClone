package com.example.kotlininstagramapp.ui.Story

import PagerAdapter
import com.example.kotlininstagramapp.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.example.kotlininstagramapp.Home.CameraFragment
import com.example.kotlininstagramapp.Models.Story
import com.example.kotlininstagramapp.utils.EventBusDataEvents
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class StoryFragment : Fragment() {

    private lateinit var viewPager: ViewPager
    private lateinit var frameLayout: FrameLayout
    lateinit var pagerAdapter: PagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_story, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val isCurrentUser = arguments?.getBoolean("isCurrentUser", false) ?: false

        viewPager = view.findViewById(R.id.viewPagerStory)
        frameLayout = view.findViewById(R.id.fl_storyActivity)

        if (isCurrentUser) {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fl_storyActivity, CameraFragment())
                .commit()
        } else {
            val pages: List<Story> = listOf() // Initialize empty stories
            pagerAdapter = PagerAdapter(pages, requireContext())
            viewPager.adapter = pagerAdapter
        }
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(sticky = true)
    fun onMessageEvent(event: EventBusDataEvents.SendStories) {
        val isCurrentUser = arguments?.getBoolean("isCurrentUser", false) ?: false
        val position = arguments?.getInt("position", 0) ?: 0

        if (!isCurrentUser) {
            val stories = event.stories
            pagerAdapter.setData(stories)
            viewPager.setCurrentItem(position)
        }
    }
}
