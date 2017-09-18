package com.phacsin.gd.guessthatmovie;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by one on 3/12/15.
 */
public class HollywoodTextView extends AppCompatTextView {

    public HollywoodTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public HollywoodTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HollywoodTextView(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Hollywood.ttf");
            setTypeface(tf);
        }
    }

}