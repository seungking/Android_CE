package com.imageliner;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    Handler handler = new Handler();

    /* Access modifiers changed, original: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_splash);
        getWindow().setFlags(1024, 1024);
        this.handler.postDelayed(new Runnable() {
            public void run() {
                SplashActivity.this.startActivity(new Intent(SplashActivity.this.getApplicationContext(), MainActivity.class));
                SplashActivity.this.finish();
                SplashActivity.this.overridePendingTransition(R.anim.anim_slide_out_top, R.anim.anim_slide_in_bottom);
            }
        }, 700);
    }
}
