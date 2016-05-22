package com.smiler.basketball_scoreboard.panels;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class SlidingRelativeLayout extends LinearLayout {

    public SlidingRelativeLayout(Context context) {
        super(context);
    }

    public SlidingRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SlidingRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    public void setYFraction(final float fraction) {
        float translationY = getHeight() * fraction;
        setTranslationY(translationY);
    }
    public void setXFraction(final float fraction) {
        float translationX = getWidth() * fraction;
        setTranslationX(translationX);
    }

    public float getYFraction() {
        if (getHeight() == 0) {
            return 0;
        }
        return getTranslationY() / getHeight();
    }
    public float getXFraction() {
        if (getWidth() == 0) {
            return 0;
        }
        return getTranslationX() / getHeight();
    }
}
