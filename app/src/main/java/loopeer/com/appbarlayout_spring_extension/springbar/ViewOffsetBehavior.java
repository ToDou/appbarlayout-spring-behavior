package loopeer.com.appbarlayout_spring_extension.springbar;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

class ViewOffsetBehavior<V extends View> extends CoordinatorLayout.Behavior<V> {

    private ViewOffsetHelper mViewOffsetHelper;

    private int mTempTopBottomOffset = 0;
    private int mTempLeftRightOffset = 0;

    public ViewOffsetBehavior() {}

    public ViewOffsetBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, V child, int layoutDirection) {
        // First let lay the child out
        layoutChild(parent, child, layoutDirection);

        if (mViewOffsetHelper == null) {
            mViewOffsetHelper = new ViewOffsetHelper(child);
        }
        mViewOffsetHelper.onViewLayout();

        if (mTempTopBottomOffset != 0) {
            mViewOffsetHelper.setTopAndBottomOffset(mTempTopBottomOffset);
            mTempTopBottomOffset = 0;
        }
        if (mTempLeftRightOffset != 0) {
            mViewOffsetHelper.setLeftAndRightOffset(mTempLeftRightOffset);
            mTempLeftRightOffset = 0;
        }

        return true;
    }

    protected void layoutChild(CoordinatorLayout parent, V child, int layoutDirection) {
        // Let the parent lay it out by default
        parent.onLayoutChild(child, layoutDirection);
    }

    public boolean setTopAndBottomOffset(int offset) {
        if (mViewOffsetHelper != null) {
            return mViewOffsetHelper.setTopAndBottomOffset(offset);
        } else {
            mTempTopBottomOffset = offset;
        }
        return false;
    }

    public boolean setLeftAndRightOffset(int offset) {
        if (mViewOffsetHelper != null) {
            return mViewOffsetHelper.setLeftAndRightOffset(offset);
        } else {
            mTempLeftRightOffset = offset;
        }
        return false;
    }

    public int getTopAndBottomOffset() {
        return mViewOffsetHelper != null ? mViewOffsetHelper.getTopAndBottomOffset() : 0;
    }

    public int getLeftAndRightOffset() {
        return mViewOffsetHelper != null ? mViewOffsetHelper.getLeftAndRightOffset() : 0;
    }
}