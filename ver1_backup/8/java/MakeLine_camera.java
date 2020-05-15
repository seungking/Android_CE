package com.imageliner;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import com.google.android.gms.ads.AdRequest.Builder;
import com.google.android.gms.ads.AdView;
import com.sdsmdg.harjot.crollerTest.Croller;
import com.sdsmdg.harjot.crollerTest.Croller.onProgressChangedListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import org.json.JSONArray;
import org.json.JSONException;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

public class MakeLine_camera extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 672;
    private static final String TAG = "opencv";
    private final int GET_GALLERY_IMAGE = 200;
    Bitmap bitmapOutput;
    int check_start = 0;
    private String imageFilePath;
    ImageView imageVIewOuput;
    private Mat img_input;
    private Mat img_output;
    boolean isReady = false;
    private AdView mAdView;
    private Uri photoUri;
    private int threshold1 = 50;
    private int threshold2 = 150;

    private int exifOrientationToDegress(int i) {
        return i == 6 ? 90 : i == 3 ? 180 : i == 8 ? 270 : 0;
    }

    public native void imageprocessing(long j, long j2, int i, int i2);

    static {
        System.loadLibrary("opencv_java4");
        System.loadLibrary("native-lib");
    }

    /* Access modifiers changed, original: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_make_line_camera);
        getWindow().setFlags(1024, 1024);
        this.mAdView = (AdView) findViewById(R.id.adView);
        this.mAdView.loadAd(new Builder().addTestDevice("B3EEABB8EE11C2BE770B684D95219ECB").build());
        this.imageVIewOuput = (ImageView) findViewById(R.id.imageViewOutput_camera);
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        if (intent.resolveActivity(getPackageManager()) != null) {
            File file = null;
            try {
                file = createImageFile();
            } catch (IOException unused) {
            }
            if (file != null) {
                this.photoUri = FileProvider.getUriForFile(getApplicationContext(), getPackageName(), file);
                intent.putExtra("output", this.photoUri);
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            }
        }
        ((TextView) findViewById(R.id.camera_next)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(MakeLine_camera.this.getApplicationContext(), MakeLine_color.class);
                Display defaultDisplay = MakeLine_camera.this.getWindowManager().getDefaultDisplay();
                Point point = new Point();
                defaultDisplay.getSize(point);
                int i = point.x;
                int i2 = point.y;
                Bitmap.createScaledBitmap(MakeLine_camera.this.bitmapOutput, i, (MakeLine_camera.this.bitmapOutput.getHeight() * i) / MakeLine_camera.this.bitmapOutput.getWidth(), true);
                ArrayList arrayList = new ArrayList();
                arrayList.add(MakeLine_camera.BitmapToString(MakeLine_camera.this.bitmapOutput));
                MakeLine_camera.this.setStringArrayPref(view.getContext(), "pass_image", arrayList);
                MakeLine_camera.this.startActivity(intent);
                Log.d("cameraimage", "!!!!!!!!!!!!!!");
                MakeLine_camera.this.finish();
                MakeLine_camera.this.overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
            }
        });
        ((ImageButton) findViewById(R.id.button_back_camera)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                MakeLine_camera.this.finish();
                MakeLine_camera.this.overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
            }
        });
        Croller croller = (Croller) findViewById(R.id.croller_camera);
        croller.setOnProgressChangedListener(new onProgressChangedListener() {
            public void onProgressChanged(int i) {
                if (MakeLine_camera.this.check_start != 0) {
                    MakeLine_camera.this.threshold1 = i;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("threshold1");
                    stringBuilder.append(MakeLine_camera.this.threshold1);
                    Log.d("a", stringBuilder.toString());
                    MakeLine_camera makeLine_camera = MakeLine_camera.this;
                    makeLine_camera.imageprocess_and_showResult(makeLine_camera.threshold1, MakeLine_camera.this.threshold2);
                }
            }
        });
        croller.setProgress(50);
        croller = (Croller) findViewById(R.id.croller1_camera);
        croller.setOnProgressChangedListener(new onProgressChangedListener() {
            public void onProgressChanged(int i) {
                if (MakeLine_camera.this.check_start != 0) {
                    MakeLine_camera.this.threshold2 = i;
                    MakeLine_camera makeLine_camera = MakeLine_camera.this;
                    makeLine_camera.imageprocess_and_showResult(makeLine_camera.threshold1, MakeLine_camera.this.threshold2);
                }
            }
        });
        croller.setProgress(50);
    }

    private File createImageFile() throws IOException {
        new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File createTempFile = File.createTempFile("TEST__", ".jpg", getExternalFilesDir(Environment.DIRECTORY_PICTURES));
        this.imageFilePath = createTempFile.getAbsolutePath();
        return createTempFile;
    }

    /* Access modifiers changed, original: protected */
    public void onActivityResult(int i, int i2, Intent intent) {
        if (i == REQUEST_IMAGE_CAPTURE && i2 == -1) {
            Bitmap decodeFile = BitmapFactory.decodeFile(this.imageFilePath);
            ExifInterface exifInterface = null;
            try {
                exifInterface = new ExifInterface(this.imageFilePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (exifInterface != null) {
                exifOrientationToDegress(exifInterface.getAttributeInt("Orientation", 1));
            }
            decodeFile = rotate(decodeFile, -270.0f);
            ((ImageView) findViewById(R.id.imageViewOutput_camera)).setImageBitmap(decodeFile);
            this.img_input = new Mat();
            Utils.bitmapToMat(decodeFile, this.img_input);
            this.check_start = 1;
        }
    }

    private Bitmap rotate(Bitmap bitmap, float f) {
        Matrix matrix = new Matrix();
        matrix.postRotate(f);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private void imageprocess_and_showResult(int i, int i2) {
        if (this.isReady) {
            if (this.img_output == null) {
                this.img_output = new Mat();
            }
            imageprocessing(this.img_input.getNativeObjAddr(), this.img_output.getNativeObjAddr(), i, i2);
            this.bitmapOutput = Bitmap.createBitmap(this.img_output.cols(), this.img_output.rows(), Config.ARGB_8888);
            Utils.matToBitmap(this.img_output, this.bitmapOutput);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("width ");
            stringBuilder.append(this.bitmapOutput.getWidth());
            String str = "cameraimage";
            Log.d(str, stringBuilder.toString());
            stringBuilder = new StringBuilder();
            stringBuilder.append("height ");
            stringBuilder.append(this.bitmapOutput.getHeight());
            Log.d(str, stringBuilder.toString());
            this.bitmapOutput = replaceColor(this.bitmapOutput, -16777216, -1);
            this.imageVIewOuput.setImageBitmap(this.bitmapOutput);
        }
    }

    /* Access modifiers changed, original: protected */
    public void onStart() {
        super.onStart();
    }

    /* Access modifiers changed, original: protected */
    public void onResume() {
        super.onResume();
        this.isReady = true;
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
