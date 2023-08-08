package com.app.ecolive.utils

import android.view.MotionEvent
import android.view.View

class TouchEffect: View.OnTouchListener
{
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if(event?.action==MotionEvent.ACTION_DOWN)
            v?.alpha=0.6f
        else if(event?.action==MotionEvent.ACTION_UP||event?.action==MotionEvent.ACTION_CANCEL)
            v?.alpha=1f;
        return false
    }
}