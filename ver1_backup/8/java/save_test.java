package com.imageliner;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.ads.AdRequest.Builder;
import com.google.android.gms.ads.AdView;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import org.json.JSONArray;
import org.json.JSONException;

public class save_test extends AppCompatActivity {
    Bitmap bitmapOutput;
    ImageView imageVIewOuput;
    private AdView mAdView;
    TextView title;

    /* Access modifiers changed, original: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_save_test);
        getWindow().setFlags(1024, 1024);
        this.mAdView = (AdView) findViewById(R.id.adView);
        this.mAdView.loadAd(new Builder().addTestDevice("B3EEABB8EE11C2BE770B684D95219ECB").build());
        this.bitmapOutput = StringToBitmap((String) getStringArrayPref(getApplicationContext(), "pass_image").get(0));
        Log.d("checkk", "1112221");
        this.imageVIewOuput = (ImageView) findViewById(R.id.imageView_save);
        this.imageVIewOuput.setImageBitmap(this.bitmapOutput);
        ((ImageButton) findViewById(R.id.button_back_save)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                save_test.this.finish();
            }
        });
    }

    public static Bitmap StringToBitmap(String str) {
        try {
            byte[] decode = Base64.decode(str, 0);
            return BitmapFactory.decodeByteArray(decode, 0, decode.length);
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public void SaveImage(Bitmap bitmap) {
        String file = Environment.getExternalStorageDirectory().toString();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(file);
        file = "/Image_Liner/";
        stringBuilder.append(file);
        File file2 = new File(stringBuilder.toString());
        file2.mkdirs();
        String format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append(format);
        stringBuilder2.append(".jpg");
        format = stringBuilder2.toString();
        File file3 = new File(file2, format);
        if (file3.exists()) {
            file3.delete();
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file3);
            bitmap.compress(CompressFormat.JPEG, 90, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        StringBuilder stringBuilder3 = new StringBuilder();
        stringBuilder3.append("file://");
        stringBuilder3.append(Environment.getExternalStorageDirectory().toString());
        stringBuilder3.append(file);
        stringBuilder3.append(format);
        sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.parse(stringBuilder3.toString())));
    }

    private ArrayList<String> getStringArrayPref(Context context, String str) {
        String string = PreferenceManager.getDefaultSharedPreferences(context).getString(str, null);
        ArrayList arrayList = new ArrayList();
        if (string != null) {
            try {
                JSONArray jSONArray = new JSONArray(string);
                for (int i = 0; i < jSONArray.length(); i++) {
                    arrayList.add(jSONArray.optString(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return arrayList;
    }
}
