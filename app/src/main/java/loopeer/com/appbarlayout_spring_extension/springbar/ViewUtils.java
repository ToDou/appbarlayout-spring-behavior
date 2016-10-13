package loopeer.com.appbarlayout_spring_extension.springbar;

import android.graphics.PorterDuff;
import android.os.Build;

class ViewUtils {

    static final ValueAnimatorCompat.Creator DEFAULT_ANIMATOR_CREATOR
            = new ValueAnimatorCompat.Creator() {
        @Override
        public ValueAnimatorCompat createAnimator() {
            return new ValueAnimatorCompat(Build.VERSION.SDK_INT >= 12
                    ? new ValueAnimatorCompatImplHoneycombMr1()
                    : new ValueAnimatorCompatImplGingerbread());
        }
    };

    static ValueAnimatorCompat createAnimator() {
        return DEFAULT_ANIMATOR_CREATOR.createAnimator();
    }

    static boolean objectEquals(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b));
    }

    static PorterDuff.Mode parseTintMode(int value, PorterDuff.Mode defaultMode) {
        switch (value) {
            case 3:
                return PorterDuff.Mode.SRC_OVER;
            case 5:
                return PorterDuff.Mode.SRC_IN;
            case 9:
                return PorterDuff.Mode.SRC_ATOP;
            case 14:
                return PorterDuff.Mode.MULTIPLY;
            case 15:
                return PorterDuff.Mode.SCREEN;
            default:
                return defaultMode;
        }
    }

}