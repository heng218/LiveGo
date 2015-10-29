package com.v.heng.livego.view;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;



public class MyToast extends Toast {

    private View view;
    private TextView textView;

    public MyToast(Context context, String text, int duration) {
        super(context);

//        view = LayoutInflater.from(context).inflate(R.layout.my_toast, null);
//        textView = (TextView) view.findViewById(R.id.textView);

//        setGravity(Gravity.CENTER, 0, 0);
//        setView(view);
//        textView.setText(text);
        setText(text);
        setDuration(duration);
    }

    public void setText(String text, int duration) {
        setGravity(Gravity.CENTER, 0, 0);
        setView(view);
        textView.setText(text);
        setDuration(duration);
    }


    @Override
    public void setDuration(int duration) {
        super.setDuration(duration);
    }

    @Override
    public void setGravity(int gravity, int xOffset, int yOffset) {
        super.setGravity(gravity, xOffset, yOffset);
    }

    @Override
    public void setMargin(float horizontalMargin, float verticalMargin) {
        super.setMargin(horizontalMargin, verticalMargin);
    }

    @Override
    public void setText(CharSequence s) {
        super.setText(s);
    }

    @Override
    public void setText(int resId) {
        super.setText(resId);
    }

    @Override
    public void setView(View view) {
        super.setView(view);
    }


}
