package com.google.android.material.appbar;


import android.animation.ValueAnimator;
import android.content.res.ColorStateList;
import android.graphics.Color;

import androidx.core.graphics.ColorUtils;
import androidx.core.view.ViewCompat;

import com.google.android.material.animation.AnimationUtils;
import com.google.android.material.tabs.TabLayout;

public class TabScrimHelper implements AppBarLayout.OnOffsetChangedListener {
    private static final int DEFAULT_SCRIM_ANIMATION_DURATION = 600;

    private TabLayout mTabLayout;
    private CollapsingToolbarLayout mToolbarLayout;
    private long mScrimAnimationDuration;
    private int mScrimAlpha;
    private boolean mScrimsAreShown;
    private ValueAnimator mScrimAnimator;
    private final int mNormalColor;
    private final int mSelectedColor;
    private int mCollapseTabSelectTextColor;
    private int mCollapseTabNormalTextColor;
    private int mCollapseTabBackgroundColor;

    public TabScrimHelper(TabLayout tabLayout, CollapsingToolbarLayout toolbarLayout) {
        initDefault();
        mTabLayout = tabLayout;
        mToolbarLayout = toolbarLayout;
        ColorStateList colorStateList = mTabLayout.getTabTextColors();
        mSelectedColor = colorStateList.getColorForState(new int[]{android.R.attr.state_selected}, Color.parseColor("#3F51B5"));
        mNormalColor = colorStateList.getDefaultColor();
    }

    @SuppressWarnings("unused")
    public void setCollapseTabBackgroundColor(int collapseTabBackgroundColor) {
        mCollapseTabBackgroundColor = collapseTabBackgroundColor;
    }

    @SuppressWarnings("unused")
    public void setCollapseTabSelectTextColor(int collapseTabSelectTextColor) {
        mCollapseTabSelectTextColor = collapseTabSelectTextColor;
    }

    private void initDefault() {
        mScrimAnimationDuration = DEFAULT_SCRIM_ANIMATION_DURATION;
        mCollapseTabBackgroundColor = Color.parseColor("#3F51B5");
        mCollapseTabNormalTextColor = Color.parseColor("#FFFFFF");
        mCollapseTabSelectTextColor = Color.parseColor("#FF4081");
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        setScrimsShown(mToolbarLayout.getHeight() + verticalOffset < mToolbarLayout.getScrimVisibleHeightTrigger());

    }

    public void setScrimsShown(boolean shown) {
        setScrimsShown(shown, ViewCompat.isLaidOut(mToolbarLayout) && !mToolbarLayout.isInEditMode());
    }

    public void setScrimsShown(boolean shown, boolean animate) {
        if (mScrimsAreShown != shown) {
            if (animate) {
                animateScrim(shown ? 0xFF : 0x0);
            } else {
                setScrimAlpha(shown ? 0xFF : 0x0);
            }
            mScrimsAreShown = shown;
        }
    }

    private void animateScrim(int targetAlpha) {
        if (mScrimAnimator == null) {
            mScrimAnimator = new ValueAnimator();
            mScrimAnimator.setDuration(mScrimAnimationDuration);
            mScrimAnimator.setInterpolator(
                    targetAlpha > mScrimAlpha
                            ? AnimationUtils.FAST_OUT_LINEAR_IN_INTERPOLATOR
                            : AnimationUtils.LINEAR_OUT_SLOW_IN_INTERPOLATOR);
            mScrimAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    setScrimAlpha((Integer) animator.getAnimatedValue());
                }
            });
        } else if (mScrimAnimator.isRunning()) {
            mScrimAnimator.cancel();
        }

        mScrimAnimator.setIntValues(mScrimAlpha, targetAlpha);
        mScrimAnimator.start();
    }

    void setScrimAlpha(int alpha) {
        if (alpha != mScrimAlpha) {
            mScrimAlpha = alpha;
            updateLayout();
        }
    }

    private void updateLayout() {
        int color = ColorUtils.setAlphaComponent(mCollapseTabBackgroundColor, mScrimAlpha);
        mTabLayout.setBackgroundColor(color);

        float i = 1.f * mScrimAlpha / 255;
        mTabLayout.setTabTextColors(
                ColorUtils.blendARGB(mNormalColor, mCollapseTabNormalTextColor, i),
                ColorUtils.blendARGB(mSelectedColor, mCollapseTabSelectTextColor, i));

    }
}
