package com.imageliner;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class license extends AppCompatActivity {
    /* Access modifiers changed, original: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_license);
        getWindow().setFlags(1024, 1024);
        ((ImageButton) findViewById(R.id.back_license)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                license.this.finish();
            }
        });
    }
}
