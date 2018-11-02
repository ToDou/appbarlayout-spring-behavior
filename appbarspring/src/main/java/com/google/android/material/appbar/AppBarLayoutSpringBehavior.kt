package com.google.android.material.appbar

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.annotation.VisibleForTesting
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.core.view.ViewCompat.TYPE_NON_TOUCH
import com.google.android.material.animation.AnimationUtils


class AppBarLayoutSpringBehavior : AppBarLayout.Behavior {

    private var mOffsetDelta: Int = 0

    var offsetSpring: Int = 0
        private set
    private var mSpringRecoverAnimator: ValueAnimator? = null
    private var mFlingAnimator: ValueAnimator? = null
    private var mPreHeadHeight: Int = 0
    var springOffsetCallback: SpringOffsetCallback? = null
    private var mOffsetAnimator: ValueAnimator? = null

    interface SpringOffsetCallback {
        fun springCallback(offset: Int)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun onStartNestedScroll(parent: CoordinatorLayout, child: AppBarLayout, directTargetChild: View, target: View, nestedScrollAxes: Int, type: Int): Boolean {
        val started = super.onStartNestedScroll(parent, child, directTargetChild, target, nestedScrollAxes, type)
        if (started && mSpringRecoverAnimator != null && mSpringRecoverAnimator!!.isRunning) {
            mSpringRecoverAnimator!!.cancel()
        }
        resetFlingAnimator()
        return started
    }

    private fun resetFlingAnimator() {
        if (mFlingAnimator != null) {
            if (mFlingAnimator!!.isRunning) {
                mFlingAnimator!!.cancel()
            }
            mFlingAnimator = null
        }
    }

    override fun onNestedScroll(coordinatorLayout: CoordinatorLayout, child: AppBarLayout, target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, type: Int) {
        if (dyUnconsumed < 0) {
            setHeaderTopBottomOffset(coordinatorLayout, child,
                    topBottomOffsetForScrollingSibling - dyUnconsumed, -child.downNestedScrollRange, 0, type)
        }
    }

    override fun onStopNestedScroll(coordinatorLayout: CoordinatorLayout, abl: AppBarLayout, target: View, type: Int) {
        super.onStopNestedScroll(coordinatorLayout, abl, target, type)

        if (type == TYPE_NON_TOUCH) {
            resetFlingAnimator()
        }
        checkShouldSpringRecover(coordinatorLayout, abl)
    }

    private fun checkShouldSpringRecover(coordinatorLayout: CoordinatorLayout?, abl: AppBarLayout) {
        if (offsetSpring > 0) animateRecoverBySpring(coordinatorLayout, abl)
    }

    private fun animateFlingSpring(coordinatorLayout: CoordinatorLayout?, abl: AppBarLayout, originNew: Int) {
        if (mFlingAnimator == null) {
            mFlingAnimator = ValueAnimator()
            mFlingAnimator!!.duration = 200
            mFlingAnimator!!.interpolator = DecelerateInterpolator()
            mFlingAnimator!!.addUpdateListener { animation -> updateSpringHeaderHeight(coordinatorLayout, abl, animation.animatedValue as Int) }
            mFlingAnimator!!.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    checkShouldSpringRecover(coordinatorLayout, abl)
                }
            })
        } else {
            if (mFlingAnimator!!.isRunning) {
                mFlingAnimator!!.cancel()
            }
        }
        mFlingAnimator!!.setIntValues(offsetSpring, Math.min(mPreHeadHeight * 3 / 2, originNew))
        mFlingAnimator!!.start()
    }

    private fun animateRecoverBySpring(coordinatorLayout: CoordinatorLayout?, abl: AppBarLayout) {
        if (mSpringRecoverAnimator == null) {
            mSpringRecoverAnimator = ValueAnimator()
            mSpringRecoverAnimator!!.duration = 200
            mSpringRecoverAnimator!!.interpolator = DecelerateInterpolator()
            mSpringRecoverAnimator!!.addUpdateListener { animation -> updateSpringHeaderHeight(coordinatorLayout, abl, animation.animatedValue as Int) }
        } else {
            if (mSpringRecoverAnimator!!.isRunning) {
                mSpringRecoverAnimator!!.cancel()
            }
        }
        mSpringRecoverAnimator!!.setIntValues(offsetSpring, 0)
        mSpringRecoverAnimator!!.start()
    }

    override fun onMeasureChild(parent: CoordinatorLayout, child: AppBarLayout, parentWidthMeasureSpec: Int, widthUsed: Int, parentHeightMeasureSpec: Int, heightUsed: Int): Boolean {
        val b = super.onMeasureChild(parent, child, parentWidthMeasureSpec, widthUsed, parentHeightMeasureSpec, heightUsed)
        if (mPreHeadHeight == 0 && child.height != 0) {
            mPreHeadHeight = getHeaderExpandedHeight(child)
        }
        return b
    }

    private fun getHeaderExpandedHeight(appBarLayout: AppBarLayout): Int {
        var range = 0
        var i = 0
        val z = appBarLayout.childCount
        while (i < z) {
            val child = appBarLayout.getChildAt(i)
            val lp = child.layoutParams as AppBarLayout.LayoutParams
            var childHeight = child.measuredHeight
            childHeight += lp.topMargin + lp.bottomMargin
            range += childHeight
            i++
        }
        return Math.max(0, range)
    }

    override fun onFlingFinished(parent: CoordinatorLayout?, layout: AppBarLayout) {
        snapToChildIfNeeded(parent, layout)
        animateRecoverBySpring(parent, layout)
    }

    private fun snapToChildIfNeeded(coordinatorLayout: CoordinatorLayout?, abl: AppBarLayout) {
        val offset = topBottomOffsetForScrollingSibling
        val offsetChildIndex = getChildIndexOnOffset(abl, offset)
        if (offsetChildIndex >= 0) {
            val offsetChild = abl.getChildAt(offsetChildIndex)
            val lp = offsetChild.layoutParams as AppBarLayout.LayoutParams
            val flags = lp.getScrollFlags()

            if (flags and AppBarLayout.LayoutParams.FLAG_SNAP == AppBarLayout.LayoutParams.FLAG_SNAP) {
                // We're set the snap, so animate the offset to the nearest edge
                var snapTop = -offsetChild.top
                var snapBottom = -offsetChild.bottom

                if (offsetChildIndex == abl.childCount - 1) {
                    // If this is the last child, we need to take the top inset into account
                    snapBottom += abl.topInset
                }

                if (checkFlag(flags, AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED)) {
                    // If the view is set only exit until it is collapsed, we'll abide by that
                    snapBottom += ViewCompat.getMinimumHeight(offsetChild)
                } else if (checkFlag(flags, AppBarLayout.LayoutParams.FLAG_QUICK_RETURN or AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS)) {
                    // If it's set to always enter collapsed, it actually has two states. We
                    // select the state and then snap within the state
                    val seam = snapBottom + ViewCompat.getMinimumHeight(offsetChild)
                    if (offset < seam) {
                        snapTop = seam
                    } else {
                        snapBottom = seam
                    }
                }

                val newOffset = if (offset < (snapBottom + snapTop) / 2)
                    snapBottom
                else
                    snapTop
                animateOffsetTo(coordinatorLayout, abl,
                        clamp(newOffset, -abl.totalScrollRange, 0), 0f)
            }
        }
    }

    private fun animateOffsetTo(coordinatorLayout: CoordinatorLayout?,
                                child: AppBarLayout, offset: Int, velocity: Float) {
        val distance = Math.abs(topBottomOffsetForScrollingSibling - offset)

        val duration: Int
        duration = if (Math.abs(velocity) > 0) {
            3 * Math.round(1000 * (distance / Math.abs(velocity)))
        } else {
            val distanceRatio = distance.toFloat() / child.height
            ((distanceRatio + 1) * 150).toInt()
        }

        animateOffsetWithDuration(coordinatorLayout, child, offset, duration)
    }

    private fun animateOffsetWithDuration(coordinatorLayout: CoordinatorLayout?,
                                          child: AppBarLayout, offset: Int, duration: Int) {
        val currentOffset = topBottomOffsetForScrollingSibling
        if (currentOffset == offset) {
            if (mOffsetAnimator != null && mOffsetAnimator!!.isRunning) {
                mOffsetAnimator!!.cancel()
            }
            return
        }

        if (mOffsetAnimator == null) {
            mOffsetAnimator = ValueAnimator()
            mOffsetAnimator!!.interpolator = AnimationUtils.DECELERATE_INTERPOLATOR
            mOffsetAnimator!!.addUpdateListener { animator ->
                setHeaderTopBottomOffset(coordinatorLayout, child,
                        animator.animatedValue as Int)
            }
        } else {
            mOffsetAnimator!!.cancel()
        }

        mOffsetAnimator!!.duration = Math.min(duration, MAX_OFFSET_ANIMATION_DURATION).toLong()
        mOffsetAnimator!!.setIntValues(currentOffset, offset)
        mOffsetAnimator!!.start()
    }

    private fun getChildIndexOnOffset(abl: AppBarLayout, offset: Int): Int {
        var i = 0
        val count = abl.childCount
        while (i < count) {
            val child = abl.getChildAt(i)
            if (child.top <= -offset && child.bottom >= -offset) {
                return i
            }
            i++
        }
        return -1
    }

    override fun setHeaderTopBottomOffset(coordinatorLayout: CoordinatorLayout?,
                                          appBarLayout: AppBarLayout?, newOffset: Int, minOffset: Int, maxOffset: Int): Int {
        return setHeaderTopBottomOffset(coordinatorLayout, appBarLayout, newOffset, minOffset, maxOffset, -1)
    }

    private fun setHeaderTopBottomOffset(coordinatorLayout: CoordinatorLayout?,
                                         appBarLayout: AppBarLayout?, offset: Int, minOffset: Int, maxOffset: Int, type: Int): Int {
        var newOffset = offset
        val curOffset = topBottomOffsetForScrollingSibling
        var consumed = 0
        if (offsetSpring != 0 && newOffset < 0) {
            var newSpringOffset = offsetSpring + offset
            if (newSpringOffset < 0) {
                newOffset = newSpringOffset
                newSpringOffset = 0
            }
            updateSpringOffsetByscroll(coordinatorLayout, appBarLayout, newSpringOffset)
            consumed = topBottomOffsetForScrollingSibling - offset
            if (newSpringOffset >= 0)
                return consumed
        }

        if (offsetSpring > 0 && appBarLayout!!.height >= mPreHeadHeight && newOffset > 0) {
            consumed = updateSpringByScroll(coordinatorLayout, appBarLayout, type, offset)
            return consumed
        }

        if (minOffset != 0 && curOffset >= minOffset && curOffset <= maxOffset) {
            newOffset = clamp(newOffset, minOffset, maxOffset)
            if (curOffset != newOffset) {
                val interpolatedOffset = if (appBarLayout!!.hasChildWithInterpolator())
                    interpolateOffset(appBarLayout, newOffset)
                else
                    newOffset

                val offsetChanged = setTopAndBottomOffset(interpolatedOffset)
                consumed = curOffset - newOffset
                mOffsetDelta = newOffset - interpolatedOffset
                if (!offsetChanged && appBarLayout.hasChildWithInterpolator()) {
                    coordinatorLayout!!.dispatchDependentViewsChanged(appBarLayout)
                }
                appBarLayout.dispatchOffsetUpdates(topAndBottomOffset)
                updateAppBarLayoutDrawableState(coordinatorLayout, appBarLayout, newOffset,
                        if (newOffset < curOffset) -1 else 1, false)
            } else if (curOffset != minOffset) {
                consumed = updateSpringByScroll(coordinatorLayout, appBarLayout!!, type, offset)
            }
        } else {
            mOffsetDelta = 0
        }
        return consumed
    }

    private fun updateSpringByScroll(coordinatorLayout: CoordinatorLayout?, appBarLayout: AppBarLayout, type: Int, originNew: Int): Int {
        val consumed: Int = topBottomOffsetForScrollingSibling - originNew
        if (appBarLayout.height >= mPreHeadHeight && type == 1) {
            if (mFlingAnimator == null)
                animateFlingSpring(coordinatorLayout, appBarLayout, originNew)
            return originNew
        }
        updateSpringOffsetByscroll(coordinatorLayout, appBarLayout, offsetSpring + originNew / 3)

        return consumed
    }

    internal override fun getTopBottomOffsetForScrollingSibling(): Int {
        return topAndBottomOffset + mOffsetDelta
    }

    private fun interpolateOffset(layout: AppBarLayout, offset: Int): Int {
        val absOffset = Math.abs(offset)

        var i = 0
        val z = layout.childCount
        while (i < z) {
            val child = layout.getChildAt(i)
            val childLp = child.layoutParams as AppBarLayout.LayoutParams
            val interpolator = childLp.getScrollInterpolator()

            if (absOffset >= child.top && absOffset <= child.bottom) {
                if (interpolator != null) {
                    var childScrollableHeight = 0
                    val flags = childLp.getScrollFlags()
                    if (flags and AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL != 0) {
                        // We're set to scroll so add the child's height plus margin
                        childScrollableHeight += (child.height + childLp.topMargin
                                + childLp.bottomMargin)

                        if (flags and AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED != 0) {
                            // For a collapsing scroll, we to take the collapsed height
                            // into account.
                            childScrollableHeight -= ViewCompat.getMinimumHeight(child)
                        }
                    }

                    if (ViewCompat.getFitsSystemWindows(child)) {
                        childScrollableHeight -= layout.topInset
                    }

                    if (childScrollableHeight > 0) {
                        val offsetForView = absOffset - child.top
                        val interpolatedDiff = Math.round(childScrollableHeight * interpolator.getInterpolation(
                                offsetForView / childScrollableHeight.toFloat()))

                        return Integer.signum(offset) * (child.top + interpolatedDiff)
                    }
                }

                // If we get to here then the view on the offset isn't suitable for interpolated
                // scrolling. So break out of the loop
                break
            }
            i++
        }

        return offset
    }

    private fun shouldJumpElevationState(parent: CoordinatorLayout, layout: AppBarLayout): Boolean {
        // We should jump the elevated state if we have a dependent scrolling view which has
        // an overlapping top (i.e. overlaps us)
        val dependencies = parent.getDependents(layout)
        var i = 0
        val size = dependencies.size
        while (i < size) {
            val dependency = dependencies[i]
            val lp = dependency.layoutParams as CoordinatorLayout.LayoutParams
            val behavior = lp.behavior

            if (behavior is AppBarLayout.ScrollingViewBehavior) {
                return behavior.overlayTop != 0
            }
            i++
        }
        return false
    }

    private fun updateSpringOffsetByscroll(coordinatorLayout: CoordinatorLayout?, appBarLayout: AppBarLayout?, offset: Int) {
        if (mSpringRecoverAnimator != null && mSpringRecoverAnimator!!.isRunning)
            mSpringRecoverAnimator!!.cancel()
        updateSpringHeaderHeight(coordinatorLayout, appBarLayout!!, offset)
    }

    private fun updateSpringHeaderHeight(coordinatorLayout: CoordinatorLayout?, appBarLayout: AppBarLayout, offset: Int) {
        if (appBarLayout.height < mPreHeadHeight || offset < 0) return
        offsetSpring = offset
        if (springOffsetCallback != null) springOffsetCallback!!.springCallback(offsetSpring)
        val layoutParams = appBarLayout.layoutParams as CoordinatorLayout.LayoutParams
        layoutParams.height = mPreHeadHeight + offset
        appBarLayout.layoutParams = layoutParams
        coordinatorLayout!!.dispatchDependentViewsChanged(appBarLayout)
    }

    @VisibleForTesting
    internal override fun isOffsetAnimatorRunning(): Boolean {
        return mOffsetAnimator != null && mOffsetAnimator!!.isRunning
    }

    private fun updateAppBarLayoutDrawableState(parent: CoordinatorLayout?,
                                                layout: AppBarLayout, offset: Int, direction: Int,
                                                forceJump: Boolean) {
        val child = getAppBarChildOnOffset(layout, offset)
        if (child != null) {
            val childLp = child.layoutParams as AppBarLayout.LayoutParams
            val flags = childLp.getScrollFlags()
            var collapsed = false

            if (flags and AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL != 0) {
                val minHeight = ViewCompat.getMinimumHeight(child)

                if (direction > 0 && flags and (AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS or AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS_COLLAPSED) != 0) {
                    // We're set to enter always collapsed so we are only collapsed when
                    // being scrolled down, and in a collapsed offset
                    collapsed = -offset >= child.bottom - minHeight - layout.topInset
                } else if (flags and AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED != 0) {
                    // We're set to exit until collapsed, so any offset which results in
                    // the minimum height (or less) being shown is collapsed
                    collapsed = -offset >= child.bottom - minHeight - layout.topInset
                }
            }

            val changed = layout.setLiftedState(collapsed)

            if (Build.VERSION.SDK_INT >= 11 && (forceJump || changed && shouldJumpElevationState(parent!!, layout))) {
                // If the collapsed state changed, we may need to
                // jump to the current state if we have an overlapping view
                layout.jumpDrawablesToCurrentState()
            }
        }
    }

    private fun clamp(value: Int, min: Int, max: Int): Int {
        if (value < min) {
            return min
        } else if (value > max) {
            return max
        }
        return value
    }

    private fun checkFlag(flags: Int, check: Int): Boolean {
        return flags and check == check
    }

    private fun getAppBarChildOnOffset(layout: AppBarLayout, offset: Int): View? {
        val absOffset = Math.abs(offset)
        var i = 0
        val z = layout.childCount
        while (i < z) {
            val child = layout.getChildAt(i)
            if (absOffset >= child.top && absOffset <= child.bottom) {
                return child
            }
            i++
        }
        return null
    }

    companion object {
        private const val MAX_OFFSET_ANIMATION_DURATION = 600 // ms
    }
}