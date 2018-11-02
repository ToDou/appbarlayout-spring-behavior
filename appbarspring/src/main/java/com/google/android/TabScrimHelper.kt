package com.google.android


import android.animation.ValueAnimator
import android.graphics.Color
import androidx.core.graphics.ColorUtils
import androidx.core.view.ViewCompat
import com.google.android.material.animation.AnimationUtils
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.tabs.TabLayout

class TabScrimHelper(private val mTabLayout: TabLayout, private val mToolbarLayout: CollapsingToolbarLayout) : AppBarLayout.OnOffsetChangedListener {


    private var mScrimAnimationDuration: Long = 0
    private var mScrimAlpha: Int = 0
    private var mScrimsAreShown: Boolean = false
    private var mScrimAnimator: ValueAnimator? = null
    private val mNormalColor: Int
    private val mSelectedColor: Int
    private var mCollapseTabSelectTextColor: Int = 0
    private var mCollapseTabNormalTextColor: Int = 0
    private var mCollapseTabBackgroundColor: Int = 0

    init {
        initDefault()
        val colorStateList = mTabLayout.tabTextColors
        mSelectedColor = colorStateList!!.getColorForState(intArrayOf(android.R.attr.state_selected), Color.parseColor("#3F51B5"))
        mNormalColor = colorStateList.defaultColor
    }


    private fun initDefault() {
        mScrimAnimationDuration = DEFAULT_SCRIM_ANIMATION_DURATION.toLong()
        mCollapseTabBackgroundColor = Color.parseColor("#3F51B5")
        mCollapseTabNormalTextColor = Color.parseColor("#FFFFFF")
        mCollapseTabSelectTextColor = Color.parseColor("#FF4081")
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
        setScrimsShown(mToolbarLayout.height + verticalOffset < mToolbarLayout.scrimVisibleHeightTrigger)

    }

    private fun setScrimsShown(shown: Boolean, animate: Boolean = ViewCompat.isLaidOut(mToolbarLayout) && !mToolbarLayout.isInEditMode) {
        if (mScrimsAreShown != shown) {
            if (animate) {
                animateScrim(if (shown) 0xFF else 0x0)
            } else {
                setScrimAlpha(if (shown) 0xFF else 0x0)
            }
            mScrimsAreShown = shown
        }
    }

    private fun animateScrim(targetAlpha: Int) {
        if (mScrimAnimator == null) {
            mScrimAnimator = ValueAnimator()
            mScrimAnimator!!.duration = mScrimAnimationDuration
            mScrimAnimator!!.interpolator = if (targetAlpha > mScrimAlpha)
                AnimationUtils.FAST_OUT_LINEAR_IN_INTERPOLATOR
            else
                AnimationUtils.LINEAR_OUT_SLOW_IN_INTERPOLATOR
            mScrimAnimator!!.addUpdateListener { animator -> setScrimAlpha(animator.animatedValue as Int) }
        } else if (mScrimAnimator!!.isRunning) {
            mScrimAnimator!!.cancel()
        }

        mScrimAnimator!!.setIntValues(mScrimAlpha, targetAlpha)
        mScrimAnimator!!.start()
    }


    private fun updateLayout() {
        val color = ColorUtils.setAlphaComponent(mCollapseTabBackgroundColor, mScrimAlpha)
        mTabLayout.setBackgroundColor(color)

        val i = 1f * mScrimAlpha / 255
        mTabLayout.setTabTextColors(
                ColorUtils.blendARGB(mNormalColor, mCollapseTabNormalTextColor, i),
                ColorUtils.blendARGB(mSelectedColor, mCollapseTabSelectTextColor, i))

    }

    companion object {
        private const val DEFAULT_SCRIM_ANIMATION_DURATION = 600
    }


    fun setCollapseTabBackgroundColor(collapseTabBackgroundColor: Int) {
        mCollapseTabBackgroundColor = collapseTabBackgroundColor
    }

    fun setCollapseTabSelectTextColor(collapseTabSelectTextColor: Int) {
        mCollapseTabSelectTextColor = collapseTabSelectTextColor
    }

    fun setScrimAlpha(alpha: Int) {
        if (alpha != mScrimAlpha) {
            mScrimAlpha = alpha
            updateLayout()
        }
    }

}
