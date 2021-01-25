package com.example.clowix;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.RecyclerView;

public class Listener extends RecyclerView.SimpleOnItemTouchListener {


    private static final String TAG = "Listener";

    private final GestureDetectorCompat gestureDetectorCompat;


    public Listener(RecyclerView recyclerView, CallBackFromListener callBackFromListener,Context context) {

        this.gestureDetectorCompat=new GestureDetectorCompat(context,new GestureDetector.SimpleOnGestureListener()
        {


            @Override
            public boolean onSingleTapUp(MotionEvent e) {

                View view=recyclerView.findChildViewUnder(e.getX(),e.getY());
                if(view!=null && callBackFromListener!=null)
                {

                    callBackFromListener.simpleTab(recyclerView.getChildAdapterPosition(view));

                }

                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {

                View view=recyclerView.findChildViewUnder(e.getX(),e.getY());

                if(view!=null && callBackFromListener!=null)
                {

                    callBackFromListener.longTab(recyclerView.getChildAdapterPosition(view));

                }

                super.onLongPress(e);
            }
        });
    }

    interface CallBackFromListener
    {

        void simpleTab(int position);
        void longTab(int position);
    }


    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

        if(gestureDetectorCompat!=null)
        {

            return gestureDetectorCompat.onTouchEvent(e);
        }
        else
        {

            return false;
        }
    }
}
