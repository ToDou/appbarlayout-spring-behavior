package loopeer.com.appbarlayout_spring_extension;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.AppBarLayoutSpringBehavior;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TabScrimHelper;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.github.mmin18.widget.RealtimeBlurView;

public class SpringAppBarLayoutWithTabActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spring_app_bar_tab_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.toolbar_layout);

        final RealtimeBlurView realtimeBlurView = findViewById(R.id.real_time_blur_view);

        AppBarLayout appBarLayout = findViewById(R.id.app_bar);
        AppBarLayoutSpringBehavior springBehavior = (AppBarLayoutSpringBehavior) ((CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams()).getBehavior();
        springBehavior.setSpringOffsetCallback(new AppBarLayoutSpringBehavior.SpringOffsetCallback() {
            @Override
            public void springCallback(int offset) {
                int radius = 20 * (240 - offset > 0 ? 240 - offset : 0) / 240;
                realtimeBlurView.setBlurRadius(radius);
            }
        });

        ViewPager viewPager = findViewById(R.id.tabs_viewpager);
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setAdapter(new TabFragmentPagerAdapter(getSupportFragmentManager()));
        TabScrimHelper tabScrimHelper = new TabScrimHelper(tabLayout, collapsingToolbarLayout);
        appBarLayout.addOnOffsetChangedListener(tabScrimHelper);
    }
}
