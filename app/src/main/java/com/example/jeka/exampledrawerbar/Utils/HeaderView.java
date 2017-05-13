package com.example.jeka.exampledrawerbar.Utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jeka.exampledrawerbar.R;


public class HeaderView extends LinearLayout {

    private TextView title;
    private TextView subtitle;

    public HeaderView(Context context) {
        super(context);
    }

    public HeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public HeaderView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFinishInflate(){
        super.onFinishInflate();
        title = (TextView) findViewById(R.id.header_view_title);
        subtitle = (TextView) findViewById(R.id.header_view_subtitle);
    }

    public void bindTo(String title){
        bindTo(title, "");
    }

    public void bindTo(String title, String subtitle) {
        hideOrSetText(this.title, title);
        hideOrSetText(this.subtitle, subtitle);
    }

    private void hideOrSetText(TextView textView, String text){
        if (text == null || text.equals("")){
            textView.setVisibility(GONE);
        }else {
            textView.setText(text);
        }
    }

}
