package com.imageliner;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.RequestConfiguration.Builder;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.skydoves.colorpickerview.ActionMode;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.ColorPickerView;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.view.UCropView;

import org.aviran.cookiebar2.CookieBar;
import org.aviran.cookiebar2.OnActionClickListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import info.hoang8f.widget.FButton;
import top.defaults.colorpicker.ColorPickerPopup;

import static com.imageliner.ShopActivity.BitmapToString;
import static com.imageliner.ShopActivity.getCurrentIndex;


public class MakeLine extends AppCompatActivity  implements View.OnClickListener {

    static {
        System.loadLibrary("opencv_java4");
        System.loadLibrary("native-lib");
    }

    int line = Color.BLACK;
    int background = Color.WHITE;
    int function = 0;
    int prasecolor = 0;

    Bitmap decodeFile = null;
    private Bitmap cbitmapOutput;
    private Bitmap bitmapOutput1;
    private Bitmap bitmapOutput2;
    private Bitmap bitmapOutput3;
    private Bitmap bitmapOutput4;
    ImageView imageVIewOuput;
    ImageView can1;
    ImageView can2;
    ImageView can3;
    ImageView can4;
    ImageView back;
    private Mat img_input;
    private Mat cmat;
    private Mat img_output;
    private Mat img_output2;
    private Mat img_output3;
    private Mat img_output4;
    private int threshold1=50;
    private int threshold2=150;

    FButton cb;
    FButton cc;
    private static final String TAG = "opencv";
    private final int GET_GALLERY_IMAGE = 200;

    boolean isReady = false;

    int type;
    ////////////카메라//////////
    private String imageFilePath;
    private Uri photoUri;
    private static final int REQUEST_IMAGE_CAPTURE = 672;

    boolean next = false;

    private RewardedVideoAd mRewardedVideoAd;
    private AdView mAdView;

    public static void startWithUri(@NonNull Context context, @NonNull Uri uri) {
        Intent intent = new Intent(context, MakeLine.class);
        intent.setData(uri);
        intent.putExtra("type",1);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_makeline);

//        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");

        this.mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        this.mRewardedVideoAd.loadAd("ca-app-pub-1992325656759505/8226553469", new AdRequest.Builder().build());
        loadRewardedVideoAd();

        mAdView = findViewById(R.id.adView1);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        imageVIewOuput = (ImageView)findViewById(R.id.imageViewOutput);
        can1 = (ImageView)findViewById(R.id.candidate1);
        can2 = (ImageView)findViewById(R.id.candidate2);
        can3 = (ImageView)findViewById(R.id.candidate3);
        can4 = (ImageView)findViewById(R.id.candidate4);

        can1.setOnClickListener(this);
        can2.setOnClickListener(this);
        can3.setOnClickListener(this);
        can4.setOnClickListener(this);

        cb = (FButton)findViewById(R.id.changebackground);
        cb.setOnClickListener(this);
        cb.setButtonColor(Color.WHITE);
        cc = (FButton)findViewById(R.id.changecolor);
        cc.setOnClickListener(this);
        cc.setButtonColor(Color.BLACK);
        cc.setTextColor(Color.WHITE);

        back = (ImageView)findViewById(R.id.line_back);
        back.setOnClickListener(this);

        type = getIntent().getIntExtra("type",-1);
        if(type == 1) {
            Log.d("LOG1", "type1");
            Uri uri = getIntent().getData();
            if (uri != null) {
                try {
                    Log.d("LOG1", "type1 1");
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    Log.d("LOG1", "type1 2");
                    img_input = new Mat();
                    Bitmap bmp32 = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                    Utils.bitmapToMat(bmp32, img_input);
                    Log.d("LOG1", "type1 3");
                    imageVIewOuput.setImageURI(uri);
                    Log.d("LOG1", "type1 4");
                } catch (Exception e) {
                    Log.e(TAG, "setImageUri", e);
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }
        if(type == 2) {
            Log.d("LOG1", "type2");
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            if (intent.resolveActivity(getPackageManager()) != null) {
                File file = null;
                try {
                    file = createImageFile();
                } catch (IOException unused) {
                }
                if (file != null) {
                    Log.d("LOG1", "type2");
                    this.photoUri = FileProvider.getUriForFile(getApplicationContext(), getPackageName(), file);
                    intent.putExtra("output", this.photoUri);
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                }
            }
        }

        findViewById(R.id.line_next).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                if(next == false){

                    ((ImageView) findViewById(R.id.imageViewOutput)).setImageBitmap(cbitmapOutput);

                    findViewById(R.id.makeline_seekbar).setVisibility(View.INVISIBLE);
                    findViewById(R.id.makeline_color).setVisibility(View.VISIBLE);
                    findViewById(R.id.makeline_color_layout).setVisibility(View.VISIBLE);
                    next = true;

                    ImageView line_done = (ImageView)findViewById(R.id.line_next);
                    line_done.setImageResource(R.drawable.ic_done_black_24dp);

                }
                else if(next == true){

                    ArrayList<Item> data =  new ArrayList<Item>();
                    ArrayList<String> titles = getStringArrayPref(MakeLine.this,"titles");
                    ArrayList<String> dates = getStringArrayPref(MakeLine.this,"dates");
                    ArrayList<String> images = getStringArrayPref(MakeLine.this,"images");
                    ArrayList<String> simages = getStringArrayPref(MakeLine.this,"simages");

                    String imagetitle = "Untitled";
                    int index=1;
                    while(true){
                        Log.d("LOG1",imagetitle+String.valueOf(index));
                        if(titles.indexOf(imagetitle+String.valueOf(index))==-1) {
                            imagetitle=imagetitle+String.valueOf(index);
                            Log.d("LOG1","titles1");
                            break;
                        }
                        else  index++;
                        Log.d("LOG1","titles2");
                    }
                    titles.add(imagetitle);
                    dates.add(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date(System.currentTimeMillis())));
                    images.add(BitmapToString(cbitmapOutput));
                    simages.add(BitmapToString(Bitmap.createScaledBitmap(cbitmapOutput, (int) cbitmapOutput.getWidth()/2, (int) cbitmapOutput.getHeight()/2, true)));

                    setStringArrayPref(MakeLine.this,"titles",titles);
                    setStringArrayPref(MakeLine.this,"dates",dates);
                    setStringArrayPref(MakeLine.this,"images",images);
                    setStringArrayPref(MakeLine.this,"simages",simages);

                    if (mRewardedVideoAd.isLoaded()) mRewardedVideoAd.show();

                    finish();
                }
            }
        });

    }

    private void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917",
                new AdRequest.Builder().build());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("LOG1", "RESUME");
        isReady = true;
        if(type==1)imageprocess_and_showResult(5, 5);
    }

    @Override
    protected void onStart() {
        Log.d("LOG1", "START");
        super.onStart();
//        if(decodeFile==null)finish();
    }

    public native void imageprocessing1(long inputImage, long outputImage, int th1, int th2);
    public native void imagebalckwhite1(long inputImage, long outputImage, int th1, int th2);

    public native void imageprocessing2(long inputImage, long outputImage, int th1, int th2);
    public native void imagebalckwhite2(long inputImage, long outputImage, int th1, int th2);

    public native void imageprocessing3(long inputImage, long outputImage, int th1, int th2);
    public native void imagebalckwhite3(long inputImage, long outputImage, int th1, int th2);

    public native void imageprocessing4(long inputImage, long outputImage, int th1, int th2);
    public native void imagebalckwhite4(long inputImage, long outputImage, int th1, int th2);

    private void imageprocess_and_showResult(int th1, int th2) {

        Log.d("LOG1", "imageprocess_and_showResult");
        if (isReady==false) return;
        Log.d("LOG1", "imageprocess_and_showResult1");
        if (img_output == null)
            img_output = new Mat();
        if (img_output2 == null)
            img_output2 = new Mat();
        if (img_output3 == null)
            img_output3 = new Mat();
        if (img_output4 == null)
            img_output4 = new Mat();
        if (img_input == null)
            img_input = new Mat();
        Log.d("LOG1", "imageprocess_and_showResult2");
        imageprocessing1(img_input.getNativeObjAddr(), img_output.getNativeObjAddr(), th1, th2);
        imagebalckwhite1(img_output.getNativeObjAddr(), img_output.getNativeObjAddr(), 0, 0);
        Log.d("LOG1", "imageprocess_and_showResult3");
        imageprocessing2(img_input.getNativeObjAddr(), img_output2.getNativeObjAddr(), th1, th2);
        imagebalckwhite2(img_output2.getNativeObjAddr(), img_output2.getNativeObjAddr(), 0, 0);
        Log.d("LOG1", "imageprocess_and_showResult4");
        imageprocessing3(img_input.getNativeObjAddr(), img_output3.getNativeObjAddr(), th1, th2);
        imagebalckwhite3(img_output3.getNativeObjAddr(), img_output3.getNativeObjAddr(), 0, 0);
        Log.d("LOG1", "imageprocess_and_showResult5");
        imageprocessing4(img_input.getNativeObjAddr(), img_output4.getNativeObjAddr(), th1, th2);
        imagebalckwhite4(img_output4.getNativeObjAddr(), img_output4.getNativeObjAddr(), 0, 0);
        Log.d("LOG1", "imageprocess_and_showResult6");

        bitmapOutput1 = Bitmap.createBitmap(img_output.cols(), img_output.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(img_output, bitmapOutput1);
        imageVIewOuput.setImageBitmap(bitmapOutput1);
        can1.setImageBitmap(bitmapOutput1);
        cbitmapOutput = bitmapOutput1;
        Log.d("LOG1", "imageprocess_and_showResult7");

        bitmapOutput2 = Bitmap.createBitmap(img_output2.cols(), img_output2.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(img_output2, bitmapOutput2);
        can2.setImageBitmap(bitmapOutput2);
        Log.d("LOG1", "imageprocess_and_showResult8");

        bitmapOutput3 = Bitmap.createBitmap(img_output3.cols(), img_output3.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(img_output3, bitmapOutput3);
        can3.setImageBitmap(bitmapOutput3);
        Log.d("LOG1", "imageprocess_and_showResult9");

        bitmapOutput4 = Bitmap.createBitmap(img_output4.cols(), img_output4.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(img_output4, bitmapOutput4);
        can4.setImageBitmap(bitmapOutput4);
        Log.d("LOG1", "imageprocess_and_showResult10");
    }


    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult( requestCode, resultCode, data );
        if ( requestCode == GET_GALLERY_IMAGE){

            if (data.getData() != null) {
                Uri uri = data.getData();
                Uri destinationUri = data.getData();

                try {
                    String path = getRealPathFromURI(uri);
                    int orientation = getOrientationOfImage(path); // 런타임 퍼미션 필요
                    Bitmap temp = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    Bitmap bitmap = getRotatedBitmap(temp, orientation);

                    img_input = new Mat();
                    Bitmap bmp32 = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                    ((ImageView) findViewById(R.id.imageViewOutput)).setImageBitmap(bmp32);
                    Utils.bitmapToMat(bmp32, img_input);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }
        else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == -1) {
            decodeFile = BitmapFactory.decodeFile(this.imageFilePath);
//            ExifInterface exifInterface = null;
//            try {
//                exifInterface = new ExifInterface(this.imageFilePath);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            if (exifInterface != null) {
//                exifOrientationToDegress(exifInterface.getAttributeInt("Orientation", 1));
//            }
            decodeFile = rotate(decodeFile, -270.0f);
            SampleActivity.startWithUri(MakeLine.this, getImageUri(MakeLine.this, decodeFile));
//            SampleActivity.startWithUri(MakeLine.this, getImageUri(MakeLine.this, decodeFile));
            finish();
//            ((ImageView) findViewById(R.id.imageViewOutput)).setImageBitmap(decodeFile);
//            this.img_input = new Mat();
//            Utils.bitmapToMat(decodeFile, this.img_input);
        }

    }

    private int exifOrientationToDegress(int i) {
        return i == 6 ? 90 : i == 3 ? 180 : i == 8 ? 270 : 0;
    }

    private Bitmap rotate(Bitmap bitmap, float f) {
        Matrix matrix = new Matrix();
        matrix.postRotate(f);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }


    private String getRealPathFromURI(Uri contentUri) {

        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        cursor.moveToFirst();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        return cursor.getString(column_index);
    }

    public int getOrientationOfImage(String filepath) {
        ExifInterface exif = null;

        try {
            exif = new ExifInterface(filepath);
        } catch (IOException e) {
            Log.d("@@@", e.toString());
            return -1;
        }

        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);

        if (orientation != -1) {
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return 90;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    return 180;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    return 270;
            }
        }

        return 0;
    }

    public int getPrasecolor(){
        return prasecolor;
    }

    public void setPraseColor(int color){
        prasecolor = color;
    }

    public int getFunction(){
        return function;
    }

    public void setFunction(int color){
        function = color;
    }

    public void setBackground(int color){
        cbitmapOutput = replaceColor(cbitmapOutput, background, getPrasecolor());
        background = getPrasecolor();
        imageVIewOuput.setImageBitmap(cbitmapOutput);
    }

    public void setColor(int color){
        cbitmapOutput = replaceColor(cbitmapOutput, line, getPrasecolor());
        line = getPrasecolor();
        imageVIewOuput.setImageBitmap(cbitmapOutput);
    }

    public void setLine(int color){
        cbitmapOutput = replaceColor(cbitmapOutput, line, getPrasecolor());
        line = getPrasecolor();
        imageVIewOuput.setImageBitmap(cbitmapOutput);
    }

    public Bitmap getRotatedBitmap(Bitmap bitmap, int degrees) throws Exception {
        if(bitmap == null) return null;
        if (degrees == 0) return bitmap;

        Matrix m = new Matrix();
        m.setRotate(degrees, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
    }

    private File createImageFile() throws IOException {
        new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File createTempFile = File.createTempFile("TEST__", ".jpg", getExternalFilesDir(Environment.DIRECTORY_PICTURES));
        this.imageFilePath = createTempFile.getAbsolutePath();
        return createTempFile;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.line_back:
                finish();
                break;
            case R.id.candidate1 :
                Log.d("LOG1", "can1");
                cmat = img_output;
                cbitmapOutput = bitmapOutput1;
                imageVIewOuput.setImageBitmap(bitmapOutput1);
                break;
            case R.id.candidate2 :
                Log.d("LOG1", "can2");
                cmat = img_output2;
                cbitmapOutput = bitmapOutput2;
                imageVIewOuput.setImageBitmap(bitmapOutput2);
                break;
            case R.id.candidate3 :
                Log.d("LOG1", "can3");
                cmat = img_output3;
                cbitmapOutput = bitmapOutput3;
                imageVIewOuput.setImageBitmap(bitmapOutput3);
                break;
            case R.id.candidate4 :
                Log.d("LOG1", "can4");
                cmat = img_output4;
                cbitmapOutput = bitmapOutput4;
                imageVIewOuput.setImageBitmap(bitmapOutput4);
                break;
            case R.id.changebackground :
                new ColorPickerPopup.Builder(this)
                        .initialColor(Color.RED) // Set initial color
                        .enableBrightness(true) // Enable brightness slider or not
                        .enableAlpha(false) // Enable alpha slider or not
                        .okTitle("Choose")
                        .cancelTitle("Cancel")
                        .showIndicator(true)
                        .showIndicator(true)
                        .showValue(false)
                        .build()
                        .show(v, new ColorPickerPopup.ColorPickerObserver() {
                            @Override
                            public void onColorPicked(int color) {
//                                v.setBackgroundColor(color);
                                setPraseColor(color);
                                setFunction(2);
                                setBackground(color);
                                cb.setButtonColor(color);
                            }
                        });
                break;
            case R.id.changecolor :
                new ColorPickerPopup.Builder(this)
                        .initialColor(Color.RED) // Set initial color
                        .enableBrightness(true) // Enable brightness slider or not
                        .enableAlpha(false) // Enable alpha slider or not
                        .okTitle("Choose")
                        .cancelTitle("Cancel")
                        .showIndicator(true)
                        .showIndicator(true)
                        .showValue(false)
                        .build()
                        .show(v, new ColorPickerPopup.ColorPickerObserver() {
                            @Override
                            public void onColorPicked(int color) {
//                                v.setBackgroundColor(color);
                                setPraseColor(color);
                                setFunction(1);
                                setColor(color);
                                cc.setButtonColor(color);
                            }
                        });
                break;
            default:
                break;
        }
    }

    public Bitmap replaceColor(Bitmap bitmap, int i, int i2) {
        if (bitmap == null) {
            return null;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] iArr = new int[(width * height)];
        bitmap.getPixels(iArr, 0, width, 0, 0, width, height);
        int i3 = 0;
        while (i3 < iArr.length) {
            iArr[i3] = iArr[i3] == i ? i2 : iArr[i3];
            i3++;
        }
        bitmap = Bitmap.createBitmap(width, height, bitmap.getConfig());
        bitmap.setPixels(iArr, 0, width, 0, 0, width, height);
        return bitmap;
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

    public void setStringArrayPref(Context context, String str, ArrayList<String> arrayList) {
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(context).edit();
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

    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}