import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.setMargins
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.example.kotlininstagramapp.R
import com.example.kotlininstagramapp.Story.PageModel
import com.naver.android.helloyako.imagecrop.view.ImageCropView

class PagerAdapter(private val pages: List<PageModel>, private val context: Context) : PagerAdapter() {

    override fun getCount(): Int {
        return pages.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(context).inflate(R.layout.item_pager, container, false)

        val imageView: ImageView = view.findViewById(R.id.imageViewPager)
        val closeButton: ImageView = view.findViewById(R.id.iv_closeButtonStory)
        val profileImage: ImageView = view.findViewById(R.id.profileImagePager)
        val username: TextView = view.findViewById(R.id.usernamePager)
        val indicatorLayout: LinearLayout = view.findViewById(R.id.indicatorLayout)

        val page = pages[position]
        Glide.with(context).load(page.imageUrls[0]).into(imageView)
        Glide.with(context).load(page.profileImageUrl).into(profileImage)
        username.text = page.username

        var currentImageIndex = 0

        closeButton.setOnClickListener {
            (context as? Activity)?.finish()
        }

        imageView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_UP -> {
                    if (event.x > imageView.width / 2) {
                        // -- Right side tap show next image
                        currentImageIndex = (currentImageIndex + 1) % page.imageUrls.size
                    } else {
                        // Left side tap show previous image 5 3 2
                        currentImageIndex = (currentImageIndex - 1 + page.imageUrls.size) % page.imageUrls.size
                    }
                    Glide.with(context).load(page.imageUrls[currentImageIndex]).into(imageView)
                    updateIndicatorColor(indicatorLayout, currentImageIndex, page.imageUrls.size)
                }
            }
            true
        }

        setupIndicators(indicatorLayout, page.imageUrls.size, 0)

        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    // Set up the indicators
    private fun setupIndicators(indicatorLayout: LinearLayout, count: Int, selectedIndex: Int) {
        indicatorLayout.removeAllViews()
        for (i in 0 until count) {

            val line = View(context)
            val size = context.resources.getDimensionPixelSize(R.dimen.line_width)
            val layoutParams = LinearLayout.LayoutParams(size, ViewGroup.LayoutParams.MATCH_PARENT)
            layoutParams.weight = 1f
            layoutParams.height =8
            layoutParams.setMargins(0,4,if(i==count-1) 16 else 8,4)
            line.layoutParams = layoutParams
            line.setBackgroundResource(if (i == selectedIndex) R.color.selected_line_color else R.color.unselected_line_color)
            indicatorLayout.addView(line)

        }
    }

    // Update the indicator color based on the current image
    private fun updateIndicatorColor(indicatorLayout: LinearLayout, currentIndex: Int, count: Int) {
        for (i in 0 until indicatorLayout.childCount) {
            indicatorLayout.getChildAt(i).setBackgroundResource(if (i == currentIndex) R.drawable.selected_indicator else R.drawable.unselected_indicator)
        }
    }




}


