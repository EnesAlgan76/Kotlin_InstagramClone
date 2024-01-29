package com.example.kotlininstagramapp.Story
import PagerAdapter
import com.example.kotlininstagramapp.R
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager

class StoryActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story)

        viewPager = findViewById(R.id.viewPagerStory)

        val pages = listOf(
            PageModel(listOf("https://media.istockphoto.com/id/1043470800/photo/black-eyed-susan-flowers-in-the-garden-at-sunset.jpg?s=612x612&w=0&k=20&c=LICHQa9w_BSypiCA3WpKqewDd65MfalaULAvDrI9etM=","https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTX4W-WHdWBWNp4nQ8cpHFyXdNybn4hB4ytZbR0MZtYWoocRQ3k-Y6a8iksNZIs6Wy12yo&usqp=CAU"), "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTX4W-WHdWBWNp4nQ8cpHFyXdNybn4hB4ytZbR0MZtYWoocRQ3k-Y6a8iksNZIs6Wy12yo&usqp=CAU", "User1"),
            PageModel(
                listOf("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTkHwIfa83tAQNOjJdd9LRhjc9LNakC8v3jZnnOWc1dil6XHmsEdaab8z7DYAhcTiT3DGw&usqp=CAU","https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTdNDhxC7fkYPuseWWzc6ZsuL1XJrHPOlwgMxy2R7OBimngZWsinsoH_HG0FJPbxRlZbUI&usqp=CAU" ),
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTdNDhxC7fkYPuseWWzc6ZsuL1XJrHPOlwgMxy2R7OBimngZWsinsoH_HG0FJPbxRlZbUI&usqp=CAU", "User2"),

            PageModel(listOf("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSC4MYCyNchNpmN89RTCMDHw7JrcQOImAehK-J3uyKlXAHE66t41TUGNUrTFMLW21rapi0&usqp=CAU", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRdjbgV6oe2UqlzrI_fJ10b1VyFwEExbUZ7h1Vp8Eu6q1FB-IudCyI7UdUwMoa42sl9Bz8&usqp=CAU"), "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRdjbgV6oe2UqlzrI_fJ10b1VyFwEExbUZ7h1Vp8Eu6q1FB-IudCyI7UdUwMoa42sl9Bz8&usqp=CAU", "User3")
        )

        val pagerAdapter = PagerAdapter(pages, this)
        viewPager.adapter = pagerAdapter
    }
}

