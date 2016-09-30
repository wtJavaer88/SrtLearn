package com.wnc.srtlearn.modules.video;

/**
 * @author gr
 */

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;

/**
 * @author gr
 * 
 */
public class MyVideoView extends SurfaceView
{

    private Context context;
    private GestureDetector mGestureDetector;

    public MyVideoView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init(context);
    }

    public MyVideoView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }

    public MyVideoView(Context context)
    {
        super(context);
        init(context);
        setFocusable(true);// 使用Key event,setFocusable(true)可以聚焦
    }

    private void init(Context context)
    {
        this.context = context;
        mGestureDetector = new GestureDetector(this.context,
                new MyOnGestureListener());
        setOnTouchListener(new OnTouchListener()
        {

            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                // if (event.getPointerCount() > 1)
                // {
                // return false;
                // }
                mGestureDetector.onTouchEvent(event);
                return true;
            }
        });
    }

    private class MyOnGestureListener extends SimpleOnGestureListener
    {

        @Override
        public boolean onSingleTapUp(MotionEvent e)
        {
            Log.i(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>",
                    "mapview onSingleTapUp click");
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e)
        {

            Log.i(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>",
                    "mapview onDoubleTapEvent click");
            if (clickLinster != null)
            {
                clickLinster.onDoubleClick();
            }
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e)
        {
            Log.i(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>",
                    "mapview onSingleTapConfirmed click");
            if (clickLinster != null)
            {
                clickLinster.onClick();
            }
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e)
        {
            Log.i(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>", "mapview onDown click");
            return true;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        return super.onTouchEvent(event);
    }

    private OnVideoClickLinster clickLinster;

    public void setOnVideoClickLinster(OnVideoClickLinster clickLinster)
    {
        this.clickLinster = clickLinster;
    }

    public interface OnVideoClickLinster
    {
        public void onClick();

        public void onDoubleClick();
    }
}