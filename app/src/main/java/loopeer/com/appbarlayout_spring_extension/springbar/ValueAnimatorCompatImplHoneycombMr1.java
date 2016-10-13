package loopeer.com.appbarlayout_spring_extension.springbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.view.animation.Interpolator;

class ValueAnimatorCompatImplHoneycombMr1 extends ValueAnimatorCompat.Impl {

    private final ValueAnimator mValueAnimator;

    ValueAnimatorCompatImplHoneycombMr1() {
        mValueAnimator = new ValueAnimator();
    }

    @Override
    public void start() {
        mValueAnimator.start();
    }

    @Override
    public boolean isRunning() {
        return mValueAnimator.isRunning();
    }

    @Override
    public void setInterpolator(Interpolator interpolator) {
        mValueAnimator.setInterpolator(interpolator);
    }

    @Override
    public void addUpdateListener(final AnimatorUpdateListenerProxy updateListener) {
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                updateListener.onAnimationUpdate();
            }
        });
    }

    @Override
    public void addListener(final AnimatorListenerProxy listener) {
        mValueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animator) {
                listener.onAnimationStart();
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                listener.onAnimationEnd();
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                listener.onAnimationCancel();
            }
        });
    }

    @Override
    public void setIntValues(int from, int to) {
        mValueAnimator.setIntValues(from, to);
    }

    @Override
    public int getAnimatedIntValue() {
        return (int) mValueAnimator.getAnimatedValue();
    }

    @Override
    public void setFloatValues(float from, float to) {
        mValueAnimator.setFloatValues(from, to);
    }

    @Override
    public float getAnimatedFloatValue() {
        return (float) mValueAnimator.getAnimatedValue();
    }

    @Override
    public void setDuration(long duration) {
        mValueAnimator.setDuration(duration);
    }

    @Override
    public void cancel() {
        mValueAnimator.cancel();
    }

    @Override
    public float getAnimatedFraction() {
        return mValueAnimator.getAnimatedFraction();
    }

    @Override
    public void end() {
        mValueAnimator.end();
    }

    @Override
    public long getDuration() {
        return mValueAnimator.getDuration();
    }
}
