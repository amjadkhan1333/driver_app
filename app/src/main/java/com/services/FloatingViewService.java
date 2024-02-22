package com.services;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.appcontroller.AppController;
import com.infodispatch.CallCenterActivity;
import com.infodispatch.KerbActivity;

import com.infodispatch.R;
import com.sqlite.DBHelper;

//public class FloatingViewService extends Service {
//
//    private WindowManager mWindowManager;
//    private View mFloatingView;
//    DBHelper db;
//
//    public FloatingViewService() {
//    }
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        //Inflate the floating view layout we created
//        mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_widget, null);
//        db = new DBHelper(AppController.getInstance());
//        //Add the view to the window.
//        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
//                WindowManager.LayoutParams.WRAP_CONTENT,
//                WindowManager.LayoutParams.WRAP_CONTENT,
//                WindowManager.LayoutParams.TYPE_PHONE,
//                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
//                PixelFormat.TRANSLUCENT);
//
//        //Specify the view position
//        params.gravity = Gravity.CENTER | Gravity.LEFT;        //Initially view will be added to top-left corner
//        params.x = 0;
//        params.y = 100;
//
//        //Add the view to the window
//        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
//        mWindowManager.addView(mFloatingView, params);
//
//        //The root element of the collapsed view layout
//        final View collapsedView = mFloatingView.findViewById(R.id.collapse_view);
//        //The root element of the expanded view layout
//        final View expandedView = mFloatingView.findViewById(R.id.expanded_container);
//
//        mFloatingView.findViewById(R.id.root_container).setOnTouchListener(new View.OnTouchListener() {
//            private int initialX;
//            private int initialY;
//            private float initialTouchX;
//            private float initialTouchY;
//
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
//
//                        //The check for Xdiff <10 && YDiff< 10 because sometime elements moves a little while clicking.
//                        //So that is click event.
//                        if (Xdiff < 10 && Ydiff < 10) {
//                            if (isViewCollapsed()) {
//                                if(db.getCurrentScreenVal().equalsIgnoreCase("KERB_SCREEN")){
//                                    Intent i = new Intent(FloatingViewService.this, KerbActivity.class);
//                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                                    FloatingViewService.this.startActivity(i);
//                                }
//                                else  if(db.getCurrentScreenVal().equalsIgnoreCase("CALL_CENTER")){
//                                    Intent i = new Intent(FloatingViewService.this, CallCenterActivity.class);
//                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                                    FloatingViewService.this.startActivity(i);
//                                }
//                            }
//                        }
//                        return true;
//                    case MotionEvent.ACTION_MOVE:
//                        //Calculate the X and Y coordinates of the view.
//                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
//                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
//
//                        //Update the layout with new X & Y coordinate
//                        mWindowManager.updateViewLayout(mFloatingView, params);
//                        return false;
//                }
//                return false;
//            }
//        });
//    }
//
//    /**
//     * Detect if the floating view is collapsed or expanded.
//     *
//     * @return true if the floating view is collapsed.
//     */
//    private boolean isViewCollapsed() {
//        return mFloatingView == null || mFloatingView.findViewById(R.id.collapse_view).getVisibility() == View.VISIBLE;
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        if (mFloatingView != null) mWindowManager.removeView(mFloatingView);
//    }
//}