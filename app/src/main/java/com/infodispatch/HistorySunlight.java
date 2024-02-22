package com.infodispatch;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adapters.HistoryListAdapter;
import com.appcontroller.LanguageController;
import com.log.MyLog;
import com.infodispatch.R;
import com.sqlite.DBHelper;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by AK on 12/23/2016.
 */

public class HistorySunlight extends AppCompatActivity implements View.OnClickListener{
    ListView layout_header;
    DBHelper db;
    Typeface textFonts;
    SwitchCompat toolbar_switch;
    ArrayList<HashMap<String, String>> historyData= new ArrayList<HashMap<String, String>>();
    HistoryListAdapter listAdapter;
    ArrayList<Integer> todayJobs;
    Button btnToday,btnYest,btnThisWeek,btnLastWeek,btnViewALl;
    TextView toolbar_title,toolbar_date,txt_switch;
    Toolbar toolbar;
    RelativeLayout rlNoJobsLayout;
    public String DEBUG_KEY="HistorySunlight";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        LanguageController.setLanguage(HistorySunlight.this);
        setContentView(R.layout.activity_history_sunlight);
        try {
            textFonts = Typeface.createFromAsset(this.getAssets(), "truenorg.otf");
            db = new DBHelper(this);
           // LanguageController.setLanguage(HistorySunlight.this);
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);
            toolbar_title.setText(R.string.history);
            toolbar_title.setTypeface(textFonts,Typeface.BOLD);
            toolbar_date = (TextView) toolbar.findViewById(R.id.toolbar_date);
            toolbar_date.setVisibility(View.GONE);
            toolbar_switch = (SwitchCompat)toolbar.findViewById(R.id.toolbar_switch);
            toolbar_switch.setVisibility(View.GONE);
            txt_switch = (TextView) toolbar.findViewById(R.id.txt_switch);
            txt_switch.setVisibility(View.GONE);
            setSupportActionBar(toolbar);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_left);
            setUiElements();
            rlNoJobsLayout =(RelativeLayout)findViewById(R.id.rlNoJobsLayout);
            layout_header =(ListView)findViewById(R.id.listview_history);
            ArrayList<HashMap<String,String>> historyData = db.getHistoryData("Today");
            Collections.reverse(historyData);
            btnToday.setBackgroundColor(getResources().getColor(R.color.color_toolbar));
            if(historyData.size()==0){
                layout_header.setVisibility(View.GONE);
                rlNoJobsLayout.setVisibility(View.VISIBLE);
            }
            else{
                layout_header.setVisibility(View.VISIBLE);
                rlNoJobsLayout.setVisibility(View.GONE);
                listAdapter = new HistoryListAdapter(this,historyData);
                layout_header.setAdapter(listAdapter);
                new GetSingleJobDataAsyncTask().execute();
            }
        }catch (Exception e){
            MyLog.appendLog(DEBUG_KEY+""+e.getMessage());
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
       // LanguageController.setLanguage(HistorySunlight.this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btnToday:
                try {
                    ArrayList<HashMap<String,String>> todayData = db.getHistoryData("Today");
                    Collections.reverse(todayData);
                    btnToday.setBackgroundColor(getResources().getColor(R.color.color_toolbar));
                    btnYest.setBackgroundColor(getResources().getColor(R.color.color_history));
                    btnThisWeek.setBackgroundColor(getResources().getColor(R.color.color_history));
                    btnLastWeek.setBackgroundColor(getResources().getColor(R.color.color_history));

                    if(todayData.size()==0){
                        layout_header.setVisibility(View.GONE);
                        rlNoJobsLayout.setVisibility(View.VISIBLE);
                    }
                    else{
                        layout_header.setVisibility(View.VISIBLE);
                        rlNoJobsLayout.setVisibility(View.GONE);
                        listAdapter = new HistoryListAdapter(this,todayData);
                        layout_header.setAdapter(listAdapter);

                    }
                }catch (Exception e){
                    MyLog.appendLog(DEBUG_KEY+"btnToday"+e.getMessage());
                }

                break;
            case R.id.btnYest:
                try {
                    btnYest.setBackgroundColor(getResources().getColor(R.color.color_toolbar));

                    btnToday.setBackgroundColor(getResources().getColor(R.color.color_history));
                    btnThisWeek.setBackgroundColor(getResources().getColor(R.color.color_history));
                    btnLastWeek.setBackgroundColor(getResources().getColor(R.color.color_history));

                    ArrayList<HashMap<String,String>> yestData = db.getHistoryData("Yest");
                    Collections.reverse(yestData);
                    if(yestData.size()==0){
                        layout_header.setVisibility(View.GONE);
                        rlNoJobsLayout.setVisibility(View.VISIBLE);
                    }
                    else{
                        layout_header.setVisibility(View.VISIBLE);
                        rlNoJobsLayout.setVisibility(View.GONE);
                        listAdapter = new HistoryListAdapter(this,yestData);
                        layout_header.setAdapter(listAdapter);

                    }
                }catch (Exception e){
                    MyLog.appendLog(DEBUG_KEY+"btnYest"+e.getMessage());
                }

                break;
            case R.id.btnThisWeek:
                try {
                    btnThisWeek.setBackgroundColor(getResources().getColor(R.color.color_toolbar));

                    btnToday.setBackgroundColor(getResources().getColor(R.color.color_history));
                    btnLastWeek.setBackgroundColor(getResources().getColor(R.color.color_history));
                    btnYest.setBackgroundColor(getResources().getColor(R.color.color_history));

                    ArrayList<HashMap<String,String>> thisWeek = db.getHistoryData("ThisWeek");
                    Collections.reverse(thisWeek);
                    if(thisWeek.size()==0){
                        layout_header.setVisibility(View.GONE);
                        rlNoJobsLayout.setVisibility(View.VISIBLE);
                    }
                    else{
                        layout_header.setVisibility(View.VISIBLE);
                        rlNoJobsLayout.setVisibility(View.GONE);
                        listAdapter = new HistoryListAdapter(this,thisWeek);
                        layout_header.setAdapter(listAdapter);
                    }
                }catch (Exception e){
                    MyLog.appendLog(DEBUG_KEY+"btnThisWeek"+e.getMessage());
                }

                break;
            case R.id.btnLastWeek:
                try {
                    btnLastWeek.setBackgroundColor(getResources().getColor(R.color.color_toolbar));

                    btnToday.setBackgroundColor(getResources().getColor(R.color.color_history));
                    btnYest.setBackgroundColor(getResources().getColor(R.color.color_history));
                    btnThisWeek.setBackgroundColor(getResources().getColor(R.color.color_history));
                    ArrayList<HashMap<String,String>> lastWeek = db.getHistoryData("LastWeek");
                    Collections.reverse(lastWeek);
                    if(lastWeek.size()==0){
                        layout_header.setVisibility(View.GONE);
                        rlNoJobsLayout.setVisibility(View.VISIBLE);
                    }
                    else{
                        layout_header.setVisibility(View.VISIBLE);
                        rlNoJobsLayout.setVisibility(View.GONE);
                        listAdapter = new HistoryListAdapter(this,lastWeek);
                        layout_header.setAdapter(listAdapter);
                    }
                }catch (Exception e){
                    MyLog.appendLog(DEBUG_KEY+"btnLastWeek"+e.getMessage());
                }
                break;
            case R.id.btnViewALl:
                break;
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(HistorySunlight.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        finish();
        startActivity(intent);
    }

    class GetSingleJobDataAsyncTask extends AsyncTask<String, Integer, Integer> {
        @Override
        protected Integer doInBackground(String... params) {
            todayJobs =db.getHistoryDataCount();
            return null;
        }
        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            todayJobs = db.getHistoryDataCount();
            btnToday.setText(getResources().getString(R.string.lbl_today) + "\n" + todayJobs.get(0));
            btnYest.setText(getResources().getString(R.string.lbl_yesterday)+"\n" + todayJobs.get(1));
            btnThisWeek.setText(getResources().getString(R.string.lbl_this_week)+"\n" + todayJobs.get(2));
            btnLastWeek.setText(getResources().getString(R.string.lbl_last_week)+"\n" + todayJobs.get(3));
           // lbl_21days.setText("Last 14 Days \n" + todayJobs.get(3));
        }
    }

    public void setUiElements() {
        try {
            layout_header =(ListView)findViewById(R.id.listview_history);
            btnToday = (Button)findViewById(R.id.btnToday);
            btnToday.setOnClickListener(this);
            btnYest = (Button)findViewById(R.id.btnYest);
            btnYest.setOnClickListener(this);
            btnThisWeek = (Button)findViewById(R.id.btnThisWeek);
            btnThisWeek.setOnClickListener(this);
            btnLastWeek = (Button)findViewById(R.id.btnLastWeek);
            btnLastWeek.setOnClickListener(this);
            btnViewALl = (Button)findViewById(R.id.btnViewALl);

            btnToday.setTypeface(textFonts, Typeface.BOLD);
            btnYest.setTypeface(textFonts, Typeface.BOLD);
            btnThisWeek.setTypeface(textFonts, Typeface.BOLD);
            btnLastWeek.setTypeface(textFonts, Typeface.BOLD);
            btnViewALl.setTypeface(textFonts, Typeface.BOLD);
        }catch (Exception e){
            MyLog.appendLog(DEBUG_KEY+"setUiElements"+e.getMessage());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(HistorySunlight.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            finish();
            startActivity(intent);
        }
        return (super.onOptionsItemSelected(item));
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(!hasFocus)
        {
//            Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
//            sendBroadcast(closeDialog);
            try {
                @SuppressLint("WrongConstant") Object service  = getSystemService("statusbar");
                Class<?> statusbarManager = Class.forName("android.app.StatusBarManager");
                Method collapse = statusbarManager.getMethod("collapse");
                collapse .setAccessible(true);
                collapse .invoke(service);
            }
            catch(Exception Eww)
            {}
        }
    }
}
