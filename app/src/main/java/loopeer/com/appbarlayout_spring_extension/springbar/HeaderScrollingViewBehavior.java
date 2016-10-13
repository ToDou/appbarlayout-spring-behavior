package loopeer.com.appbarlayout_spring_extension.springbar;

import android.content.Context;
import android.graphics.Rect;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.WindowInsetsCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Field;
import java.util.List;

abstract class HeaderScrollingViewBehavior extends ViewOffsetBehavior<View> {

    final Rect mTempRect1 = new Rect();
    final Rect mTempRect2 = new Rect();

    private int mVerticalLayoutGap = 0;
    private int mOverlayTop;

    public HeaderScrollingViewBehavior() {}

    public HeaderScrollingViewBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onMeasureChild(CoordinatorLayout parent, View child,
                                  int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec,
                                  int heightUsed) {
        final int childLpHeight = child.getLayoutParams().height;
        if (childLpHeight == ViewGroup.LayoutParams.MATCH_PARENT
                || childLpHeight == ViewGroup.LayoutParams.WRAP_CONTENT) {
            // If the menu's height is set to match_parent/wrap_content then measure it
            // with the maximum visible height

            final List<View> dependencies = parent.getDependencies(child);
            final View header = findFirstDependency(dependencies);
            if (header != null) {
                if (ViewCompat.getFitsSystemWindows(header)
                        && !ViewCompat.getFitsSystemWindows(child)) {
                    // If the header is fitting system windows then we need to also,
                    // otherwise we'll get CoL's compatible measuring
                    ViewCompat.setFitsSystemWindows(child, true);

                    if (ViewCompat.getFitsSystemWindows(child)) {
                        // If the set succeeded, trigger a new layout and return true
                        child.requestLayout();
                        return true;
                    }
                }

                int availableHeight = View.MeasureSpec.getSize(parentHeightMeasureSpec);
                if (availableHeight == 0) {
                    // If the measure spec doesn't specify a size, use the current height
                    availableHeight = parent.getHeight();
                }

                final int height = availableHeight - header.getMeasuredHeight()
                        + getScrollRange(header);
                final int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(height,
                        childLpHeight == ViewGroup.LayoutParams.MATCH_PARENT
                                ? View.MeasureSpec.EXACTLY
                                : View.MeasureSpec.AT_MOST);

                // Now measure the scrolling view with the correct height
                parent.onMeasureChild(child, parentWidthMeasureSpec,
                        widthUsed, heightMeasureSpec, heightUsed);

                return true;
            }
        }
        return false;
    }

    @Override
    protected void layoutChild(final CoordinatorLayout parent, final View child,
            final int layoutDirection) {
        final List<View> dependencies = parent.getDependencies(child);
        final View header = findFirstDependency(dependencies);

        if (header != null) {
            final CoordinatorLayout.LayoutParams lp =
                    (CoordinatorLayout.LayoutParams) child.getLayoutParams();
            final Rect available = mTempRect1;
            available.set(parent.getPaddingLeft() + lp.leftMargin,
                    header.getBottom() + lp.topMargin,
                    parent.getWidth() - parent.getPaddingRight() - lp.rightMargin,
                    parent.getHeight() + header.getBottom()
                            - parent.getPaddingBottom() - lp.bottomMargin);


            WindowInsetsCompat parentInsets = null;
            try {
                Field field = parent.getClass().getDeclaredField("mLastInsets");
                field.setAccessible(true);
                parentInsets = (WindowInsetsCompat) field.get(parent);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (parentInsets != null && ViewCompat.getFitsSystemWindows(parent)
                    && !ViewCompat.getFitsSystemWindows(child)) {
                // If we're set to handle insets but this child isn't, then it has been measured as
                // if there are no insets. We need to lay it out to match horizontally.
                // Top and bottom and already handled in the logic above
                available.left += parentInsets.getSystemWindowInsetLeft();
                available.right -= parentInsets.getSystemWindowInsetRight();
            }

            final Rect out = mTempRect2;
            GravityCompat.apply(resolveGravity(lp.gravity), child.getMeasuredWidth(),
                    child.getMeasuredHeight(), available, out, layoutDirection);

            final int overlap = getOverlapPixelsForOffset(header);

            child.layout(out.left, out.top - overlap, out.right, out.bottom - overlap);
            mVerticalLayoutGap = out.top - header.getBottom();
        } else {
            // If we don't have a dependency, let super handle it
            super.layoutChild(parent, child, layoutDirection);
            mVerticalLayoutGap = 0;
        }
    }

    float getOverlapRatioForOffset(final View header) {
        return 1f;
    }

    final int getOverlapPixelsForOffset(final View header) {
        return mOverlayTop == 0 ? 0 : MathUtils.constrain(
                (int) (getOverlapRatioForOffset(header) * mOverlayTop), 0, mOverlayTop);
    }

    private static int resolveGravity(int gravity) {
        return gravity == Gravity.NO_GRAVITY ? GravityCompat.START | Gravity.TOP : gravity;
    }

    abstract View findFirstDependency(List<View> views);

    int getScrollRange(View v) {
        return v.getMeasuredHeight();
    }

    /**
     * The gap between the top of the scrolling view and the bottom of the header layout in pixels.
     */
    final int getVerticalLayoutGap() {
        return mVerticalLayoutGap;
    }

    /**
     * Set the distance that this view should overlap any {@link }.
     *
     * @param overlayTop the distance in px
     */
    public final void setOverlayTop(int overlayTop) {
        mOverlayTop = overlayTop;
    }

    /**
     * Returns the distance that this view should overlap any {@link }.
     */
    public final int getOverlayTop() {
        return mOverlayTop;
    }
}