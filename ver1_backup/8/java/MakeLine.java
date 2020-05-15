package com.imageliner;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore.Images.Media;
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
import com.sdsmdg.harjot.crollerTest.Croller;
import com.sdsmdg.harjot.crollerTest.Croller.onProgressChangedListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

public class MakeLine extends AppCompatActivity {
    private final int GET_GALLERY_IMAGE = 200;
    Bitmap bitmapOutput;
    int check_start = 0;
    ImageView imageVIewOuput;
    private Mat img_input;
    private Mat img_output;
    boolean isReady = false;
    private AdView mAdView;
    private int threshold1 = 50;
    private int threshold2 = 150;

    public native void imageprocessing(long j, long j2, int i, int i2);

    static {
        System.loadLibrary("native-lib");
        System.loadLibrary("opencv_java4");
    }

    /* Access modifiers changed, original: protected */
    public void onCreate(Bundle bundle) {
        Intent intent = new Intent("android.intent.action.PICK");
        intent.setData(Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, 200);
        super.onCreate(bundle);
        setContentView(R.layout.activity_makeline);
        getWindow().setFlags(1024, 1024);
        this.mAdView = (AdView) findViewById(R.id.adView);
        this.mAdView.loadAd(new Builder().addTestDevice("B3EEABB8EE11C2BE770B684D95219ECB").build());
        Croller croller = (Croller) findViewById(R.id.croller);
        croller.setOnProgressChangedListener(new onProgressChangedListener() {
            public void onProgressChanged(int i) {
                if (MakeLine.this.check_start != 0) {
                    MakeLine.this.threshold1 = i;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("threshold1");
                    stringBuilder.append(MakeLine.this.threshold1);
                    Log.d("a", stringBuilder.toString());
                    MakeLine makeLine = MakeLine.this;
                    makeLine.imageprocess_and_showResult(makeLine.threshold1, MakeLine.this.threshold2);
                }
            }
        });
        croller.setProgress(50);
        this.imageVIewOuput = (ImageView) findViewById(R.id.imageViewOutput);
        croller = (Croller) findViewById(R.id.croller1);
        croller.setOnProgressChangedListener(new onProgressChangedListener() {
            public void onProgressChanged(int i) {
                if (MakeLine.this.check_start != 0) {
                    MakeLine.this.threshold2 = i;
                    MakeLine makeLine = MakeLine.this;
                    makeLine.imageprocess_and_showResult(makeLine.threshold1, MakeLine.this.threshold2);
                }
            }
        });
        croller.setProgress(50);
        ((TextView) findViewById(R.id.line_next)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(MakeLine.this.getApplicationContext(), MakeLine_color.class);
                ArrayList arrayList = new ArrayList();
                arrayList.add(MakeLine.BitmapToString(MakeLine.this.bitmapOutput));
                MakeLine.this.setStringArrayPref(view.getContext(), "pass_image", arrayList);
                MakeLine.this.startActivity(intent);
                MakeLine.this.finish();
                MakeLine.this.overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
            }
        });
        ((ImageButton) findViewById(R.id.button_back)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                MakeLine.this.finish();
                MakeLine.this.overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
            }
        });
    }

    /* Access modifiers changed, original: protected */
    public void onResume() {
        super.onResume();
        this.isReady = true;
    }

    private void imageprocess_and_showResult(int i, int i2) {
        if (this.isReady) {
            if (this.img_output == null) {
                this.img_output = new Mat();
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("threshold1");
            stringBuilder.append(i);
            stringBuilder.append("\nthreshold2");
            stringBuilder.append(i2);
            String str = "a";
            Log.d(str, stringBuilder.toString());
            imageprocessing(this.img_input.getNativeObjAddr(), this.img_output.getNativeObjAddr(), i, i2);
            this.bitmapOutput = Bitmap.createBitmap(this.img_output.cols(), this.img_output.rows(), Config.ARGB_8888);
            Utils.matToBitmap(this.img_output, this.bitmapOutput);
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("bitmap");
            stringBuilder2.append(BitmapToString(this.bitmapOutput));
            Log.d(str, stringBuilder2.toString());
            this.bitmapOutput = replaceColor(this.bitmapOutput, -16777216, -1);
            this.imageVIewOuput.setImageBitmap(this.bitmapOutput);
        }
    }

    /* Access modifiers changed, original: protected */
    public void onStart() {
        super.onStart();
    }

    /* Access modifiers changed, original: protected */
    public void onActivityResult(int i, int i2, Intent intent) {
        if (i != 200) {
            finish();
        } else if (intent.getData() != null) {
            Uri data = intent.getData();
            try {
                Bitmap rotatedBitmap = getRotatedBitmap(Media.getBitmap(getContentResolver(), data), getOrientationOfImage(getRealPathFromURI(data)));
                this.img_input = new Mat();
                Utils.bitmapToMat(rotatedBitmap.copy(Config.ARGB_8888, true), this.img_input);
                this.check_start = 1;
            } catch (Exception e) {
                Log.d("a", "errrrrrrr");
                e.printStackTrace();
                finish();
            }
        }
    }

    private String getRealPathFromURI(Uri uri) {
        String str = "_data";
        Cursor query = getContentResolver().query(uri, new String[]{str}, null, null, null);
        query.moveToFirst();
        return query.getString(query.getColumnIndexOrThrow(str));
    }

    public int getOrientationOfImage(String str) {
        try {
            int attributeInt = new ExifInterface(str).getAttributeInt("Orientation", -1);
            if (attributeInt != -1) {
                if (attributeInt == 3) {
                    return 180;
                }
                if (attributeInt == 6) {
                    return 90;
                }
                if (attributeInt == 8) {
                    return 270;
                }
            }
            return 0;
        } catch (IOException e) {
            Log.d("@@@", e.toString());
            return -1;
        }
    }

    public Bitmap getRotatedBitmap(Bitmap bitmap, int i) throws Exception {
        if (bitmap == null) {
            return null;
        }
        if (i == 0) {
            return bitmap;
        }
        Matrix matrix = new Matrix();
        matrix.setRotate((float) i, ((float) bitmap.getWidth()) / 2.0f, ((float) bitmap.getHeight()) / 2.0f);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private void setStringArrayPref(Context context, String str, ArrayList<String> arrayList) {
        Editor edit = PreferenceManager.getDefaultSharedPreferences(context).edit();
        JSONArray jSONArray = new JSONArray();
        for (int i = 0; i < arrayList.size(); i++) {
            jSONArray.put(arrayList.get(i));
        }
        if (arrayList.isEmpty()) {
            edit.putString(str, null);
        } else {
            edit.putString(str, jSONArray.toString());
        }
        edit.apply();
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

    public static String BitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.PNG, 70, byteArrayOutputStream);
        return Base64.encodeToString(byteArrayOutputStream.toByteArray(), 0);
    }

    public Bitmap replaceColor(Bitmap bitmap, int i, int i2) {
        if (bitmap == null) {
            return null;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] iArr = new int[(width * height)];
        bitmap.getPixels(iArr, 0, width, 0, 0, width, height);
        for (int i3 = 0; i3 < iArr.length; i3++) {
            iArr[i3] = iArr[i3] == i ? i2 : -16777216;
        }
        bitmap = Bitmap.createBitmap(width, height, bitmap.getConfig());
        bitmap.setPixels(iArr, 0, width, 0, 0, width, height);
        return bitmap;
    }
}
