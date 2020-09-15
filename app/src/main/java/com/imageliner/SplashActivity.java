package com.imageliner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;
import com.imageliner.manage.ManagePref;
import com.imageliner.manage.functions;
import com.imageliner.models.Item;

import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        //Lottie Animation
        LottieAnimationView animationView = (LottieAnimationView) findViewById(R.id.splash_logo);
        animationView.setAnimation("loading3.json");
        animationView.loop(true);
        //Lottie Animation start
        animationView.playAnimation();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                isFirstTime();
            }
        }, 1700);
    }


    private void isFirstTime() {
        //맨처음 시작했는지 확인
        SharedPreferences preferences = getApplication().getSharedPreferences("onBoard", Context.MODE_PRIVATE);
        boolean isFirstTime = preferences.getBoolean("isFirstTime", true);

        if (isFirstTime) {

            ManagePref managePref = new ManagePref();

            ArrayList<String> check = new ArrayList<String>();
            ArrayList<String> titles = new ArrayList<String>();
            ArrayList<String> dates = new ArrayList<String>();
            ArrayList<String> images = new ArrayList<String>();
            ArrayList<String> simages = new ArrayList<String>();

            check = managePref.getStringArrayPref(this,"check");
            titles = managePref.getStringArrayPref(this,"titles");
            dates = managePref.getStringArrayPref(this,"dates");
            images = managePref.getStringArrayPref(this,"images");
            simages = managePref.getStringArrayPref(this,"simages");
            
            //Log.d("LOG1","onstart");
            check.add("started");
            titles.add("Welcome");
            dates.add("Make Your Image!");
            images.add(new functions().BitmapToString(BitmapFactory.decodeResource(getResources(), R.drawable.welcom)));
            simages.add(new functions().BitmapToString(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.welcom), (int) BitmapFactory.decodeResource(getResources(), R.drawable.welcom).getWidth()/2, (int) BitmapFactory.decodeResource(getResources(), R.drawable.welcom).getHeight()/2, true)));

            managePref.setStringArrayPref(this,"check",check);
            managePref.setStringArrayPref(this,"titles",titles);
            managePref.setStringArrayPref(this,"dates",dates);
            managePref.setStringArrayPref(this,"images",images);
            managePref.setStringArrayPref(this,"simages",simages);

            //처음
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("isFirstTime", false);
            editor.apply();

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor1 = sharedPreferences.edit();
            editor1.putBoolean("switch_background", true);
            editor1.apply();

            //액티비티 이동
            startActivity(new Intent(SplashActivity.this, OnBoardActivity.class));
            finish();
        } else {
            //처음 아님
            startActivity(new Intent(SplashActivity.this, MainActivity_home.class));
            finish();
        }
    }
}