package com.edu.flinnt.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.widget.TextView;

import com.edu.flinnt.R;

/**
 * Created by user on 11/6/16.
 */
public class FadedTextView extends TextView {

    // Width of the fade effect from end of the view.
    private int mFadeWidth;

    // Add other constructors too.
    public FadedTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FadedTextView);
        mFadeWidth = a.getDimensionPixelSize(R.styleable.FadedTextView_fadeWidth, 0);
//        TypedArray a = null;
//        mFadeWidth = 0;
        a.recycle();
    }

    @Override
    public void onDraw(Canvas canvas) {
        int width = getMeasuredWidth();

        // Layout doesn't return a proper width for getWidth().
        // Instead check the width of the first line, as we've restricted to just one line.
        if (getLayout().getLineWidth(0) > width) {
            // Always get the current text color before setting the gradient.
            int color = getCurrentTextColor();

            // [0..stop] will be current text color, [stop..1] will be the actual gradient
            float stop = ((float) (width - mFadeWidth) / (float) width);

            // Set up a linear gradient.
            LinearGradient gradient = new LinearGradient(0, 0, width, 0,
                    new int[] { color, color, Color.TRANSPARENT },
                    new float[] { 0, stop, 1.0f },
                    Shader.TileMode.CLAMP);
            getPaint().setShader(gradient);
        } else {
            getPaint().setShader(null);
        }

        // Do a default draw.
        super.onDraw(canvas);
    }
}