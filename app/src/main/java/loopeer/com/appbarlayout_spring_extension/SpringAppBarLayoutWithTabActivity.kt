package loopeer.com.appbarlayout_spring_extension

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.viewpager.widget.ViewPager
import com.github.mmin18.widget.RealtimeBlurView
import com.google.android.TabScrimHelper
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayoutSpringBehavior
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.tabs.TabLayout

class SpringAppBarLayoutWithTabActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spring_app_bar_tab_layout)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val collapsingToolbarLayout = findViewById<CollapsingToolbarLayout>(R.id.toolbar_layout)

        val realtimeBlurView = findViewById<RealtimeBlurView>(R.id.real_time_blur_view)

        val appBarLayout = findViewById<AppBarLayout>(R.id.app_bar)
        val springBehavior = (appBarLayout.layoutParams as CoordinatorLayout.LayoutParams).behavior as AppBarLayoutSpringBehavior?
        springBehavior!!.springOffsetCallback = object : AppBarLayoutSpringBehavior.SpringOffsetCallback {
            override fun springCallback(offset: Int) {
                val radius = 20 * (if (240 - offset > 0) 240 - offset else 0) / 240
                realtimeBlurView.setBlurRadius(radius.toFloat())
            }
        }

        val viewPager = findViewById<ViewPager>(R.id.tabs_viewpager)
        val tabLayout = findViewById<TabLayout>(R.id.tabs)
        tabLayout.setupWithViewPager(viewPager)
        viewPager.adapter = TabFragmentPagerAdapter(supportFragmentManager)
        val tabScrimHelper = TabScrimHelper(tabLayout, collapsingToolbarLayout)
        appBarLayout.addOnOffsetChangedListener(tabScrimHelper)
    }
}
