package com.edu.flinnt.util;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Resolution class for text spanning issue in JellyBean devices
 */
public class JellyBeanSpanFixTextView extends TextView {

    private static class FixingResult {
        public final boolean fixed;
        public final List<Object> spansWithSpacesBefore;
        public final List<Object> spansWithSpacesAfter;

        public static FixingResult fixed(List<Object> spansWithSpacesBefore, List<Object> spansWithSpacesAfter) {
            return new FixingResult(true, spansWithSpacesBefore, spansWithSpacesAfter);
        }

        public static FixingResult notFixed() {
            return new FixingResult(false, null, null);
        }

        private FixingResult(boolean fixed, List<Object> spansWithSpacesBefore, List<Object> spansWithSpacesAfter) {
            this.fixed = fixed;
            this.spansWithSpacesBefore = spansWithSpacesBefore;
            this.spansWithSpacesAfter = spansWithSpacesAfter;
        }
    }

    private static final String TAG = JellyBeanSpanFixTextView.class.getSimpleName();

    public JellyBeanSpanFixTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        super.setTextIsSelectable(false);
    }

    public JellyBeanSpanFixTextView(Context context, AttributeSet attrs) {
        super(context, attrs);        super.setTextIsSelectable(false);

    }

    public JellyBeanSpanFixTextView(Context context) {
        super(context);        super.setTextIsSelectable(false);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        try {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } catch (IndexOutOfBoundsException e) {
            fixOnMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    /**
     * If possible, fixes the Spanned text by adding spaces around spans when
     * needed.
     */
    private void fixOnMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        CharSequence text = getText();
        if (text instanceof Spanned) {
            SpannableStringBuilder builder = new SpannableStringBuilder(text);
            fixSpannedWithSpaces(builder, widthMeasureSpec, heightMeasureSpec);
        } else {
            Log.d(TAG, "The text isn't a Spanned");
            fallbackToString(widthMeasureSpec, heightMeasureSpec);
        }
    }

    /**
     * Add spaces around spans until the text is fixed, and then removes the
     * unneeded spaces
     */
    private void fixSpannedWithSpaces(SpannableStringBuilder builder, int widthMeasureSpec, int heightMeasureSpec) {
        long startFix = System.currentTimeMillis();

        FixingResult result = addSpacesAroundSpansUntilFixed(builder, widthMeasureSpec, heightMeasureSpec);

        if (result.fixed) {
            removeUnneededSpaces(widthMeasureSpec, heightMeasureSpec, builder, result);
        } else {
            fallbackToString(widthMeasureSpec, heightMeasureSpec);
        }

        if (LogWriter.isValidLevel(Log.DEBUG)) {
            long fixDuration = System.currentTimeMillis() - startFix;
            Log.d(TAG, "fixSpannedWithSpaces() duration in ms: " + fixDuration);
        }
    }

    private FixingResult addSpacesAroundSpansUntilFixed(SpannableStringBuilder builder, int widthMeasureSpec, int heightMeasureSpec) {

        Object[] spans = builder.getSpans(0, builder.length(), Object.class);
        List<Object> spansWithSpacesBefore = new ArrayList<Object>(spans.length);
        List<Object> spansWithSpacesAfter = new ArrayList<Object>(spans.length);

        for (Object span : spans) {
            int spanStart = builder.getSpanStart(span);
            if (isNotSpace(builder, spanStart - 1)) {
                builder.insert(spanStart, " ");
                spansWithSpacesBefore.add(span);
            }

            int spanEnd = builder.getSpanEnd(span);
            if (isNotSpace(builder, spanEnd)) {
                builder.insert(spanEnd, " ");
                spansWithSpacesAfter.add(span);
            }

            try {
                setTextAndMeasure(builder, widthMeasureSpec, heightMeasureSpec);
                return FixingResult.fixed(spansWithSpacesBefore, spansWithSpacesAfter);
            } catch (IndexOutOfBoundsException notFixed) {
            }
        }

        Log.d(TAG, "Could not fix the Spanned by adding spaces around spans");
        return FixingResult.notFixed();
    }

    private boolean isNotSpace(CharSequence text, int where) {
        return text.charAt(where) != ' ';
    }

    private void setTextAndMeasure(CharSequence text, int widthMeasureSpec, int heightMeasureSpec) {
        setText(text);
        super.measure(widthMeasureSpec, heightMeasureSpec);
    }

    private void removeUnneededSpaces(int widthMeasureSpec, int heightMeasureSpec, SpannableStringBuilder builder, FixingResult result) {

        for (Object span : result.spansWithSpacesAfter) {
            int spanEnd = builder.getSpanEnd(span);
            builder.delete(spanEnd, spanEnd + 1);
            try {
                setTextAndMeasure(builder, widthMeasureSpec, heightMeasureSpec);
            } catch (IndexOutOfBoundsException ignored) {
                builder.insert(spanEnd, " ");
            }
        }

        boolean needReset = true;
        for (Object span : result.spansWithSpacesBefore) {
            int spanStart = builder.getSpanStart(span);
            builder.delete(spanStart - 1, spanStart);
            try {
                setTextAndMeasure(builder, widthMeasureSpec, heightMeasureSpec);
                needReset = false;
            } catch (IndexOutOfBoundsException ignored) {
                needReset = true;
                int newSpanStart = spanStart - 1;
                builder.insert(newSpanStart, " ");
            }
        }

        if (needReset) {
            setText(builder);
            super.measure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    private void fallbackToString(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d(TAG, "Fallback to unspanned text");
        String fallbackText = getText().toString();
        setTextAndMeasure(fallbackText, widthMeasureSpec, heightMeasureSpec);
    }


    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {

      /*  if (selStart == -1 || selEnd == -1) {
            LogWriter.write("In side of onSelectionChanged if");
            // @hack : https://code.google.com/p/android/issues/detail?id=137509
            if(!getText().toString().equalsIgnoreCase("") && getText().toString() != null){
                CharSequence text = getText();
                if (text instanceof Spannable) {
                    Selection.setSelection((Spannable) text, 0, 0);//replace to 1 didn't work);
                }
            }
        } else {

            LogWriter.write("In side of onSelectionChanged else");
            super.onSelectionChanged(selStart, selEnd);
        }*/
    }

    @Override
    public void setTextIsSelectable(boolean selectable) {
        super.setTextIsSelectable(false);
    }

}