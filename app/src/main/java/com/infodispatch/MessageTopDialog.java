package com.infodispatch;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.infodispatch.R;
import com.sqlite.DBHelper;

/**
 * Created by info on 31-01-2017.
 */

public class MessageTopDialog extends Activity {
    DBHelper db;
    Typeface textFonts;
    TextView dialog_are_you_sure;
    Button  dialog_btn_yes;
    String messageContent,btnText="Ok",playStoreUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.message_dialog);
        textFonts = Typeface.createFromAsset(this.getAssets(), "truenorg.otf");
        Intent intent = getIntent();
        messageContent =intent.getStringExtra("notifyDataChanged");
        if(messageContent.contains("update")){
            btnText=intent.getStringExtra("buttonVal");
            playStoreUrl=intent.getStringExtra("url");
        }
        setUiElements();
        dialog_btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(messageContent.contains("update")){
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+playStoreUrl)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        Toast.makeText(getApplicationContext(),"Play store not installed.",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    finish();
                }
            }
        });
    }
    public void setUiElements() {
        dialog_are_you_sure = (TextView)findViewById(R.id.dialog_are_you_sure);
        dialog_are_you_sure.setText(String.valueOf(messageContent));
        dialog_btn_yes = (Button)findViewById(R.id.dialog_btn_yes);
        dialog_are_you_sure.setTypeface(textFonts);
        dialog_btn_yes.setTypeface(textFonts);
        dialog_btn_yes.setText(btnText);
    }
}
