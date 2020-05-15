package com.imageliner;

import android.annotation.SuppressLint;
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
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

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

import static com.imageliner.ShopActivity.BitmapToString;


public class MakeLine extends AppCompatActivity implements View.OnClickListener {

    static {
        System.loadLibrary("opencv_java4");
        System.loadLibrary("native-lib");
    }

    int line = Color.BLACK;
    int background = Color.WHITE;

    Bitmap bitmapOutput;
    ImageView imageVIewOuput;
    private Mat img_input;
    private Mat img_output;
    private int threshold1=50;
    private int threshold2=150;

    private static final String TAG = "opencv";
    private final int GET_GALLERY_IMAGE = 200;

    boolean isReady = false;

    ////////////카메라//////////
    private String imageFilePath;
    private Uri photoUri;
    private static final int REQUEST_IMAGE_CAPTURE = 672;

    boolean next = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_makeline);

        Log.d("LOG1", "start");
        imageVIewOuput = (ImageView)findViewById(R.id.imageViewOutput);

        int type = getIntent().getIntExtra("type",-1);
        Log.d("LOG1", "type" + String.valueOf(type));
        if(type == 1) {
            Log.d("LOG1", "image");
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(intent, GET_GALLERY_IMAGE);
        }
        if(type == 2) {
            Log.d("LOG1", "camera");
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
        }

        final TextView textView1 = (TextView)findViewById(R.id.textView_threshold1);
        SeekBar seekBar1=(SeekBar)findViewById(R.id.seekBar_threshold1);
        seekBar1.setProgress(threshold1);
        seekBar1.setMax(200);
//        seekBar1.setMin(0);
        seekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                threshold1 = progress;
                textView1.setText(threshold1+"");
                imageprocess_and_showResult(threshold1, threshold2);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        final TextView textView2 = (TextView)findViewById(R.id.textView_threshold2);
        SeekBar seekBar2=(SeekBar)findViewById(R.id.seekBar_threshold2);
        seekBar2.setProgress(threshold2);
        seekBar2.setMax(200);
//        seekBar2.setMin(0);
        seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                threshold2 = progress;
                textView2.setText(threshold2+"");
                imageprocess_and_showResult(threshold1, threshold2);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        findViewById(R.id.line_back).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.line_next).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                if(next == false){
//                    BitmapDrawable d = (BitmapDrawable)((ImageView) findViewById(R.id.imageViewOutput)).getDrawable();
//                    image_out = d.getBitmap();

                    imagebalckwhite(img_output.getNativeObjAddr(), img_output.getNativeObjAddr(), 0, 0);
                    bitmapOutput = Bitmap.createBitmap(img_output.cols(), img_output.rows(), Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(img_output, bitmapOutput);
                    ((ImageView) findViewById(R.id.imageViewOutput)).setImageBitmap(bitmapOutput);

                    findViewById(R.id.makeline_seekbar).setVisibility(View.INVISIBLE);
                    findViewById(R.id.makeline_color).setVisibility(View.VISIBLE);
                    next = true;


                }
                else if(next == true){
                    ArrayList<Item> data =  new ArrayList<Item>();
                    ArrayList<String> titles = getStringArrayPref(MakeLine.this,"titles");
                    ArrayList<String> dates = getStringArrayPref(MakeLine.this,"dates");
                    ArrayList<String> images = getStringArrayPref(MakeLine.this,"images");
                    ArrayList<String> simages = getStringArrayPref(MakeLine.this,"simages");

                    titles.add("Untitled");
                    dates.add(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date(System.currentTimeMillis())));
                    images.add(BitmapToString(bitmapOutput));
                    simages.add(BitmapToString(Bitmap.createScaledBitmap(bitmapOutput, (int) bitmapOutput.getWidth()/2, (int) bitmapOutput.getHeight()/2, true)));

                    setStringArrayPref(MakeLine.this,"titles",titles);
                    setStringArrayPref(MakeLine.this,"dates",dates);
                    setStringArrayPref(MakeLine.this,"images",images);
                    setStringArrayPref(MakeLine.this,"simages",simages);

                    finish();
                }
            }
        });

        findViewById(R.id.background_1).setOnClickListener(this);
        findViewById(R.id.background_2).setOnClickListener(this);
        findViewById(R.id.background_3).setOnClickListener(this);
        findViewById(R.id.background_4).setOnClickListener(this);
        findViewById(R.id.background_5).setOnClickListener(this);
        findViewById(R.id.background_6).setOnClickListener(this);
        findViewById(R.id.background_7).setOnClickListener(this);
        findViewById(R.id.background_8).setOnClickListener(this);
        findViewById(R.id.background_9).setOnClickListener(this);
        findViewById(R.id.background_10).setOnClickListener(this);

        findViewById(R.id.line_1).setOnClickListener(this);
        findViewById(R.id.line_2).setOnClickListener(this);
        findViewById(R.id.line_3).setOnClickListener(this);
        findViewById(R.id.line_4).setOnClickListener(this);
        findViewById(R.id.line_5).setOnClickListener(this);
        findViewById(R.id.line_6).setOnClickListener(this);
        findViewById(R.id.line_7).setOnClickListener(this);
        findViewById(R.id.line_8).setOnClickListener(this);
        findViewById(R.id.line_9).setOnClickListener(this);
        findViewById(R.id.line_10).setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        isReady = true;
    }

    public native void imageprocessing(long inputImage, long outputImage, int th1, int th2);

    public native void imagebalckwhite(long inputImage, long outputImage, int th1, int th2);

    private void imageprocess_and_showResult(int th1, int th2) {

        if (isReady==false) return;

        if (img_output == null)
            img_output = new Mat();
        if (img_input == null)
            img_input = new Mat();

        imageprocessing(img_input.getNativeObjAddr(), img_output.getNativeObjAddr(), th1, th2);


        bitmapOutput = Bitmap.createBitmap(img_output.cols(), img_output.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(img_output, bitmapOutput);
        imageVIewOuput.setImageBitmap(bitmapOutput);
    }


    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if ( requestCode == GET_GALLERY_IMAGE){

            if (data.getData() != null) {
                Uri uri = data.getData();

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
            ((ImageView) findViewById(R.id.imageViewOutput)).setImageBitmap(decodeFile);
            this.img_input = new Mat();
            Utils.bitmapToMat(decodeFile, this.img_input);
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
                int function = 0;
        int parseColor = 0;
        switch (v.getId()) {
            case R.id.background_1:
                parseColor = Color.parseColor("#D65353");
                function=1;
                break;
            case R.id.background_2:
                parseColor = Color.parseColor("#FD8B64");
                function=1;
                break;
            case R.id.background_3:
                parseColor = Color.parseColor("#FCD353");
                function=1;
                break;
            case R.id.background_4:
                parseColor = Color.parseColor("#99D45D");
                function=1;
                break;
            case R.id.background_5:
                parseColor = Color.parseColor("#0181BB");
                function=1;
                break;
            case R.id.background_6:
                parseColor = Color.parseColor("#234257");
                function=1;
                break;
            case R.id.background_7:
                parseColor = Color.parseColor("#9979C1");
                function=1;
                break;
            case R.id.background_8:
                parseColor = Color.parseColor("#FFFFFF");
                function=1;
                break;
            case R.id.background_9:
                parseColor = Color.parseColor("#6D7172");
                function=1;
                break;
            case R.id.background_10:
                parseColor = Color.parseColor("#FB050505");
                function=1;
                break;
            case R.id.line_1:
                parseColor = Color.parseColor("#D65353");
                function=2;
                break;
            case R.id.line_2:
                parseColor = Color.parseColor("#FD8B64");
                function=2;
                break;
            case R.id.line_3:
                parseColor = Color.parseColor("#FCD353");
                function=2;
                break;
            case R.id.line_4:
                parseColor = Color.parseColor("#99D45D");
                function=2;
                break;
            case R.id.line_5:
                parseColor = Color.parseColor("#0181BB");
                function=2;
                break;
            case R.id.line_6:
                parseColor = Color.parseColor("#234257");
                function=2;
                break;
            case R.id.line_7:
                parseColor = Color.parseColor("#9979C1");
                function=2;
                break;
            case R.id.line_8:
                parseColor = Color.parseColor("#FFFFFF");
                function=2;
                break;
            case R.id.line_9:
                parseColor = Color.parseColor("#6D7172");
                function=2;
                break;
            case R.id.line_10:
                parseColor = Color.parseColor("#FB050505");
                function=2;
                break;
            default:
                break;
        }

        if (function==1 && parseColor != background && parseColor != line) {
            bitmapOutput = replaceColor(bitmapOutput, background, parseColor);
            background = parseColor;
            imageVIewOuput.setImageBitmap(bitmapOutput);
        }
        if (function==2 && parseColor != background && parseColor != line) {
            bitmapOutput = replaceColor(bitmapOutput, line, parseColor);
            line = parseColor;
            imageVIewOuput.setImageBitmap(bitmapOutput);
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
}