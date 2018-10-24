package com.verve.toilettracker.service;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.verve.toilettracker.R;
import com.verve.toilettracker.activity.MainActivity;
public class FloatingViewService extends Service {
    private WindowManager mWindowManager;
    private View mFloatingView, collapsedView;
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs";
    String strUserType, strUserId, strUserMobile;
    public FloatingViewService() {
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_widget, null);
        sharedpreferences = getApplicationContext().getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        strUserId = sharedpreferences.getString("user_ID", null);
        strUserMobile = sharedpreferences.getString("user_Mobile", null);
        strUserType = sharedpreferences.getString("user_Type", null);
        //Add the view to the window.
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        //Specify the view position
        params.gravity = Gravity.TOP | Gravity.LEFT;        //Initially view will be added to top-left corner
        params.x = 0;
        params.y = 100;
        //Add the view to the window
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingView, params);
        collapsedView = mFloatingView.findViewById(R.id.collapse_view);
        collapsedView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FloatingViewService.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                stopSelf();
            }
        });
//        collapsedView.setOnTouchListener(new View.OnTouchListener() {
//            private int initialX;
//            private int initialY;
//            private float initialTouchX;
//            private float initialTouchY;
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        //remember the initial position.
//                        initialX = params.x;
//                        initialY = params.y;
//                        //get the touch location
//                        initialTouchX = event.getRawX();
//                        initialTouchY = event.getRawY();
//                        return true;
//                    case MotionEvent.ACTION_UP:
//                        int Xdiff = (int) (event.getRawX() - initialTouchX);
//                        int Ydiff = (int) (event.getRawY() - initialTouchY);
//                        //The check for Xdiff <10 && YDiff< 10 because sometime elements moves a little while clicking.
//                        //So that is click event.
//                        if (Xdiff < 10 && Ydiff < 10) {
//                            if (isViewCollapsed()) {
////                                collapsedView.setVisibility(View.GONE);
////                                expandedView.setVisibility(View.VISIBLE);
//                            }
//                        }
//                        return true;
//                    case MotionEvent.ACTION_MOVE:
//                        //Calculate the X and Y coordinates of the view.
//                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
//                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
//                        //Update the layout with new X & Y coordinate
//                        mWindowManager.updateViewLayout(mFloatingView, params);
//                        return true;
//                }
//                return false;
//            }
//        });
    }
    /**
     * Detect if the floating view is collapsed or expanded.
     *
     * @return true if the floating view is collapsed.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatingView != null) mWindowManager.removeView(mFloatingView);
    }
}
