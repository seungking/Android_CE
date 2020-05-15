package com.imageliner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MakeColor extends AppCompatActivity{
//public class MakeColor extends AppCompatActivity implements View.OnClickListener {

    int line = Color.WHITE;
    int background = Color.BLACK;
    Bitmap image_out;
    ImageView BigImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_color);
//
//        Intent intent = getIntent();
//        byte[] arr = getIntent().getByteArrayExtra("image");
//        Bitmap[] image = {BitmapFactory.decodeByteArray(arr, 0, arr.length)};
//        BigImage = (ImageView)findViewById(R.id.imageViewOutput_c);
//        image_out = image[0];
//        BigImage.setImageBitmap(image_out);
//
//        findViewById(R.id.color_back).setOnClickListener(this);
//        findViewById(R.id.color_next).setOnClickListener(this);
//
//        findViewById(R.id.background_1).setOnClickListener(this);
//        findViewById(R.id.background_2).setOnClickListener(this);
//        findViewById(R.id.background_3).setOnClickListener(this);
//        findViewById(R.id.background_4).setOnClickListener(this);
//        findViewById(R.id.background_5).setOnClickListener(this);
//        findViewById(R.id.background_6).setOnClickListener(this);
//        findViewById(R.id.background_7).setOnClickListener(this);
//        findViewById(R.id.background_8).setOnClickListener(this);
//        findViewById(R.id.background_9).setOnClickListener(this);
//        findViewById(R.id.background_10).setOnClickListener(this);
//
//        findViewById(R.id.line_1).setOnClickListener(this);
//        findViewById(R.id.line_2).setOnClickListener(this);
//        findViewById(R.id.line_3).setOnClickListener(this);
//        findViewById(R.id.line_4).setOnClickListener(this);
//        findViewById(R.id.line_5).setOnClickListener(this);
//        findViewById(R.id.line_6).setOnClickListener(this);
//        findViewById(R.id.line_7).setOnClickListener(this);
//        findViewById(R.id.line_8).setOnClickListener(this);
//        findViewById(R.id.line_9).setOnClickListener(this);
//        findViewById(R.id.line_10).setOnClickListener(this);
    }
//
//    public Bitmap replaceColor(Bitmap bitmap, int i, int i2) {
//        if (bitmap == null) {
//            return null;
//        }
//        int width = bitmap.getWidth();
//        int height = bitmap.getHeight();
//        int[] iArr = new int[(width * height)];
//        bitmap.getPixels(iArr, 0, width, 0, 0, width, height);
//        int i3 = 0;
//        while (i3 < iArr.length) {
//            iArr[i3] = iArr[i3] == i ? i2 : iArr[i3];
//            i3++;
//        }
//        bitmap = Bitmap.createBitmap(width, height, bitmap.getConfig());
//        bitmap.setPixels(iArr, 0, width, 0, 0, width, height);
//        return bitmap;
//    }
//
//    @Override
//    public void onClick(View v) {
//
//        int function = 0;
//        int parseColor = 0;
//        switch (v.getId()) {
//            case R.id.color_back:
//                finish();
//                break;
//            case R.id.color_next:
//                ArrayList<Item> data =  new ArrayList<Item>();
//                ArrayList<String> titles = getStringArrayPref(this,"titles");
//                ArrayList<String> dates = getStringArrayPref(this,"dates");
//                ArrayList<String> images = getStringArrayPref(this,"images");
//                ArrayList<String> simages = getStringArrayPref(this,"simages");
//
//                titles.add("Untitled");
//                dates.add(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date(System.currentTimeMillis())));
//                images.add(BitmapToString(image_out));
//                simages.add(BitmapToString(Bitmap.createScaledBitmap(image_out, (int) 280, (int) 240, true)));
//
//                setStringArrayPref(this,"titles",titles);
//                setStringArrayPref(this,"dates",dates);
//                setStringArrayPref(this,"images",images);
//                setStringArrayPref(this,"simages",simages);
//
//                finish();
//
//                break;
//            case R.id.background_1:
//                parseColor = Color.parseColor("#D65353");
//                function=1;
//                break;
//            case R.id.background_2:
//                parseColor = Color.parseColor("#FD8B64");
//                function=1;
//                break;
//            case R.id.background_3:
//                parseColor = Color.parseColor("#FCD353");
//                function=1;
//                break;
//            case R.id.background_4:
//                parseColor = Color.parseColor("#99D45D");
//                function=1;
//                break;
//            case R.id.background_5:
//                parseColor = Color.parseColor("#0181BB");
//                function=1;
//                break;
//            case R.id.background_6:
//                parseColor = Color.parseColor("#234257");
//                function=1;
//                break;
//            case R.id.background_7:
//                parseColor = Color.parseColor("#9979C1");
//                function=1;
//                break;
//            case R.id.background_8:
//                parseColor = Color.parseColor("#FFFFFF");
//                function=1;
//                break;
//            case R.id.background_9:
//                parseColor = Color.parseColor("#6D7172");
//                function=1;
//                break;
//            case R.id.background_10:
//                parseColor = Color.parseColor("#FB050505");
//                function=1;
//                break;
//            case R.id.line_1:
//                parseColor = Color.parseColor("#D65353");
//                function=2;
//                break;
//            case R.id.line_2:
//                parseColor = Color.parseColor("#FD8B64");
//                function=2;
//                break;
//            case R.id.line_3:
//                parseColor = Color.parseColor("#FCD353");
//                function=2;
//                break;
//            case R.id.line_4:
//                parseColor = Color.parseColor("#99D45D");
//                function=2;
//                break;
//            case R.id.line_5:
//                parseColor = Color.parseColor("#0181BB");
//                function=2;
//                break;
//            case R.id.line_6:
//                parseColor = Color.parseColor("#234257");
//                function=2;
//                break;
//            case R.id.line_7:
//                parseColor = Color.parseColor("#9979C1");
//                function=2;
//                break;
//            case R.id.line_8:
//                parseColor = Color.parseColor("#FFFFFF");
//                function=2;
//                break;
//            case R.id.line_9:
//                parseColor = Color.parseColor("#6D7172");
//                function=2;
//                break;
//            case R.id.line_10:
//                parseColor = Color.parseColor("#FB050505");
//                function=2;
//                break;
//            default:
//                break;
//        }
//
//        if (function==1 && parseColor != background && parseColor != line) {
//            image_out = replaceColor(image_out, background, parseColor);
//            background = parseColor;
//            BigImage.setImageBitmap(image_out);
//        }
//        if (function==2 && parseColor != background && parseColor != line) {
//            image_out = replaceColor(image_out, line, parseColor);
//            line = parseColor;
//            BigImage.setImageBitmap(image_out);
//        }
//    }
//
//    private ArrayList<String> getStringArrayPref(Context context, String str) {
//        String string = PreferenceManager.getDefaultSharedPreferences(context).getString(str, null);
//        ArrayList arrayList = new ArrayList();
//        if (string != null) {
//            try {
//                JSONArray jSONArray = new JSONArray(string);
//                for (int i = 0; i < jSONArray.length(); i++) {
//                    arrayList.add(jSONArray.optString(i));
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//        return arrayList;
//    }
//
//    public static String BitmapToString(Bitmap bitmap) {
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG, 70, byteArrayOutputStream);
//        return Base64.encodeToString(byteArrayOutputStream.toByteArray(), 0);
//    }
//
//    public void setStringArrayPref(Context context, String str, ArrayList<String> arrayList) {
//        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(context).edit();
//        JSONArray jSONArray = new JSONArray();
//        for (int i = 0; i < arrayList.size(); i++) {
//            jSONArray.put(arrayList.get(i));
//        }
//        if (arrayList.isEmpty()) {
//            edit.putString(str, null);
//        } else {
//            edit.putString(str, jSONArray.toString());
//        }
//        edit.apply();
//    }

}
