package com.example.sridh.vdiary;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import static com.example.sridh.vdiary.prefs.isFirst;
import static com.example.sridh.vdiary.prefs.get;

public class splashScreen extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        vClass.setStatusBar(getWindow(),getApplicationContext(),R.color.colorPrimaryDark);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(isFirstLaunch()){
                    startActivity(new Intent(splashScreen.this,TutorialActivity.class));
                }
                else {
                    if(scrapper.readFromPrefs(getApplicationContext())){
                        startActivity(new Intent(splashScreen.this, workSpace.class));
                        overridePendingTransition(R.anim.slide_in_up,R.anim.slide_out_up);
                        finish();
                    }
                    else {
                        startActivity(new Intent(splashScreen.this, scrapper.class));
                        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                    }
                }
            }
        },500);
    }

    boolean isFirstLaunch(){
        return get(getApplicationContext(),isFirst,true);//return prefs.getBoolean(IS_FIRST,true);
    }

}
