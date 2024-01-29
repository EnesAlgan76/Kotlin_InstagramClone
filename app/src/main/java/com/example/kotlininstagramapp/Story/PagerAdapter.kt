import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.example.kotlininstagramapp.R
import com.example.kotlininstagramapp.Story.PageModel

class PagerAdapter(private val pages: List<PageModel>, private val context: Context) : PagerAdapter() {

    override fun getCount(): Int {
        return pages.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(context).inflate(R.layout.item_pager, container, false)

        val imageView: ImageView = view.findViewById(R.id.imageViewPager)
        val profileImage: ImageView = view.findViewById(R.id.profileImagePager)
        val username: TextView = view.findViewById(R.id.usernamePager)
        val indicatorLayout: LinearLayout = view.findViewById(R.id.indicatorLayout)

        val page = pages[position]
        Glide.with(context).load(page.imageUrls[0]).into(imageView)
        Glide.with(context).load(page.profileImageUrl).into(profileImage)
        username.text = page.username

        var currentImageIndex = 0

        // Set a click listener on the imageView to cycle through the images when tapped
        imageView.setOnClickListener {
            // Increment the index to get the next image URL
            currentImageIndex = (currentImageIndex + 1) % page.imageUrls.size
            Glide.with(context).load(page.imageUrls[currentImageIndex]).into(imageView)

            // Update the indicator color based on the current image
            updateIndicatorColor(indicatorLayout, currentImageIndex, page.imageUrls.size)
        }

        // Initialize the indicators
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
            val indicator = View(context)
            val size = context.resources.getDimensionPixelSize(R.dimen.indicator_size)
            val margin = context.resources.getDimensionPixelSize(R.dimen.indicator_margin)
            val layoutParams = LinearLayout.LayoutParams(size, size)
            layoutParams.setMargins(margin, 0, margin, 0)
            indicator.layoutParams = layoutParams
            indicator.setBackgroundResource(if (i == selectedIndex) R.drawable.selected_indicator else R.drawable.unselected_indicator)
            indicatorLayout.addView(indicator)
        }
    }

    // Update the indicator color based on the current image
    private fun updateIndicatorColor(indicatorLayout: LinearLayout, currentIndex: Int, count: Int) {
        for (i in 0 until indicatorLayout.childCount) {
            indicatorLayout.getChildAt(i).setBackgroundResource(if (i == currentIndex) R.drawable.selected_indicator else R.drawable.unselected_indicator)
        }
    }
}


