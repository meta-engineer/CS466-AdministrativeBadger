package com.yys.cs446.es.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

public class iconTextView extends android.support.v7.widget.AppCompatTextView {
    private Context context;

    public iconTextView(Context context) {
        super(context);
        this.context = context;
        createView();
    }

    public iconTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        createView();
    }

    public iconTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        createView();
    }

    private void createView() {
        setGravity(Gravity.CENTER);
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fa_fr.otf");
        setTypeface(tf);
        //setTypeface(Typeface.createFromAsset(context.getAssets(), "raw/fa_fr.otf"));
    }
}