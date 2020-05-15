package com.imageliner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class info extends AppCompatActivity {
    /* Access modifiers changed, original: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_info);
        getWindow().setFlags(1024, 1024);
        ((ImageButton) findViewById(R.id.back_info)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                info.this.finish();
            }
        });
        ((TextView) findViewById(R.id.golicense)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                info.this.startActivity(new Intent(info.this.getApplicationContext(), license.class));
            }
        });
    }
}
