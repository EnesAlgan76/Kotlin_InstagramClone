package com.example.kotlininstagramapp.Story
import PagerAdapter
import com.example.kotlininstagramapp.R
import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.kotlininstagramapp.Home.CameraFragment
import com.example.kotlininstagramapp.Models.Story
import com.example.kotlininstagramapp.utils.EventBusDataEvents
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class StoryActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager
    private lateinit var frameLayout: FrameLayout
    lateinit var pagerAdapter :PagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story)
        val isCurrentUser = intent.getBooleanExtra("isCurrentUser",false)
        viewPager = findViewById(R.id.viewPagerStory)
        frameLayout = findViewById(R.id.fl_storyActivity)



        if (isCurrentUser){
            supportFragmentManager.beginTransaction()
                .replace(R.id.fl_storyActivity,CameraFragment())
                .commit()
        }else{
            val pages: List<Story> = listOf()
            pagerAdapter = PagerAdapter(pages, this)
            viewPager.adapter = pagerAdapter
        }




    }

    override fun onStart() {
        EventBus.getDefault().register(this)
        super.onStart()
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    @Subscribe(sticky = true)
    fun onMessageEvent(event: EventBusDataEvents.SendStories) {
        val isCurrentUser = intent.getBooleanExtra("isCurrentUser",false)
        val position = intent.getIntExtra("position",-1)
        if (!isCurrentUser){
            val stories = event.stories
            pagerAdapter.setData(stories)
            viewPager.setCurrentItem(position-1)
        }

    }




}

