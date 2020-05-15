package com.imageliner;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog.Builder;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.ads.InterstitialAd;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import org.json.JSONArray;
import org.json.JSONException;

public class Painting extends AppCompatActivity {
    private static final String TAG = "Main";
    String Front_Image = "";
    Button SaveBtn;
    int height;
    Bitmap lined_image;
    int mHeight;
    private InterstitialAd mInterstitialAd;
    private LinearLayout mLlCanvas;
    private PaintView mPaintView;
    int mWidth;
    int width;

    /* Access modifiers changed, original: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_painting);
        getWindow().setFlags(1024, 1024);
        this.lined_image = StringToBitmap(getIntent().getStringExtra("bitmap"));
        this.mLlCanvas = (LinearLayout) findViewById(R.id.llCanvas);
        LayoutParams layoutParams = this.mLlCanvas.getLayoutParams();
        Bitmap StringToBitmap = StringToBitmap((String) getStringArrayPref(getApplicationContext(), "pass_image").get(0));
        Display defaultDisplay = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        defaultDisplay.getSize(point);
        this.width = point.x;
        this.height = point.y;
        layoutParams.height = (this.width * StringToBitmap.getHeight()) / StringToBitmap.getWidth();
        this.mLlCanvas.setLayoutParams(layoutParams);
        this.mLlCanvas.setBackground(new BitmapDrawable(StringToBitmap));
        this.mPaintView = new PaintView(getApplicationContext(), null, StringToBitmap, StringToBitmap.getWidth(), StringToBitmap.getHeight());
        this.mLlCanvas.addView(this.mPaintView);
        this.mPaintView.requestFocus();
        final SeekBar seekBar = (SeekBar) findViewById(R.id.paint_size);
        seekBar.setProgress(15);
        seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                Painting.this.mPaintView.setsize(i);
            }
        });
        ((TextView) findViewById(R.id.paint_done)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                Builder builder = new Builder(view.getContext(), R.style.AlertDialog);
                builder.setTitle("Save?");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String str;
                        Painting.this.mLlCanvas.setDrawingCacheEnabled(true);
                        Painting.this.mLlCanvas.buildDrawingCache();
                        Bitmap drawingCache = Painting.this.mLlCanvas.getDrawingCache();
                        drawingCache = Bitmap.createScaledBitmap(drawingCache, Painting.this.width, (Painting.this.width * drawingCache.getHeight()) / drawingCache.getWidth(), true);
                        String str2 = "file_name";
                        ArrayList access$200 = Painting.this.getStringArrayPref(Painting.this.getApplicationContext(), str2);
                        if (access$200.size() == 0) {
                            access$200.add("Untitled 0");
                        } else {
                            int size = access$200.size();
                            int i2 = 0;
                            while (true) {
                                str = "Untitled ";
                                if (i2 >= access$200.size()) {
                                    break;
                                }
                                String str3 = (String) access$200.get(i2);
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append(str);
                                stringBuilder.append(size);
                                if (str3.contains(stringBuilder.toString())) {
                                    size++;
                                }
                                i2++;
                            }
                            StringBuilder stringBuilder2 = new StringBuilder();
                            stringBuilder2.append(str);
                            stringBuilder2.append(size);
                            access$200.add(stringBuilder2.toString());
                        }
                        String format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
                        str = "list";
                        ArrayList access$2002 = Painting.this.getStringArrayPref(Painting.this.getApplicationContext(), str);
                        access$2002.add(format);
                        String BitmapToString = Painting.BitmapToString(drawingCache);
                        String str4 = "list_image";
                        ArrayList access$2003 = Painting.this.getStringArrayPref(Painting.this.getApplicationContext(), str4);
                        access$2003.add(BitmapToString);
                        Painting.this.setStringArrayPref(Painting.this.getApplicationContext(), str2, access$200);
                        Painting.this.setStringArrayPref(Painting.this.getApplicationContext(), str, access$2002);
                        Painting.this.setStringArrayPref(Painting.this.getApplicationContext(), str4, access$2003);
                        Painting.this.finish();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                builder.show();
            }
        });
        ((ImageButton) findViewById(R.id.button_back_paint)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                Painting.this.finish();
                Painting.this.overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
            }
        });
        ((ImageButton) findViewById(R.id.reset)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                Painting.this.mPaintView.clear();
            }
        });
        ((ImageView) findViewById(R.id.button_undo)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                Painting.this.mPaintView.undo();
            }
        });
        ((ImageView) findViewById(R.id.button_redo)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                Painting.this.mPaintView.redo();
            }
        });
        ((Button) findViewById(R.id.paint_color_1)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                seekBar.setThumb(Painting.this.getResources().getDrawable(R.drawable.seekbar_seekbar1_thumb));
                Painting.this.mPaintView.change_color("#D65353");
            }
        });
        ((Button) findViewById(R.id.paint_color_2)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                seekBar.setThumb(Painting.this.getResources().getDrawable(R.drawable.seekbar_seekbar2_thumb));
                Painting.this.mPaintView.change_color("#FD8B64");
            }
        });
        ((Button) findViewById(R.id.paint_color_3)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                seekBar.setThumb(Painting.this.getResources().getDrawable(R.drawable.seekbar_seekbar3_thumb));
                Painting.this.mPaintView.change_color("#FCD353");
            }
        });
        ((Button) findViewById(R.id.paint_color_4)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                seekBar.setThumb(Painting.this.getResources().getDrawable(R.drawable.seekbar_seekbar4_thumb));
                Painting.this.mPaintView.change_color("#99D45D");
            }
        });
        ((Button) findViewById(R.id.paint_color_5)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                seekBar.setThumb(Painting.this.getResources().getDrawable(R.drawable.seekbar_seekbar5_thumb));
                Painting.this.mPaintView.change_color("#0181BB");
            }
        });
        ((Button) findViewById(R.id.paint_color_6)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                seekBar.setThumb(Painting.this.getResources().getDrawable(R.drawable.seekbar_seekbar6_thumb));
                Painting.this.mPaintView.change_color("#234257");
            }
        });
        ((Button) findViewById(R.id.paint_color_7)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                seekBar.setThumb(Painting.this.getResources().getDrawable(R.drawable.seekbar_seekbar7_thumb));
                Painting.this.mPaintView.change_color("#9979C1");
            }
        });
        ((Button) findViewById(R.id.paint_color_8)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                seekBar.setThumb(Painting.this.getResources().getDrawable(R.drawable.seekbar_seekbar8_thumb));
                Painting.this.mPaintView.change_color("#FFFFFF");
            }
        });
        ((Button) findViewById(R.id.paint_color_9)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                seekBar.setThumb(Painting.this.getResources().getDrawable(R.drawable.seekbar_seekbar9_thumb));
                Painting.this.mPaintView.change_color("#6D7172");
            }
        });
        ((Button) findViewById(R.id.paint_color_10)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                seekBar.setThumb(Painting.this.getResources().getDrawable(R.drawable.seekbar_seekbar10_thumb));
                Painting.this.mPaintView.change_color("#FB050505");
            }
        });
    }

    private void savingFile() {
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(externalStorageDirectory.getAbsolutePath());
        stringBuilder.append("/MyFiles1");
        File file = new File(stringBuilder.toString());
        if (!file.exists()) {
            boolean mkdirs = file.mkdirs();
            stringBuilder = new StringBuilder();
            stringBuilder.append(" ");
            stringBuilder.append(mkdirs);
            Log.i(TAG, stringBuilder.toString());
        }
        externalStorageDirectory = new File(file, "BodyFront.png");
        if (!externalStorageDirectory.exists()) {
            try {
                externalStorageDirectory.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(externalStorageDirectory);
            Bitmap bitmap = this.mPaintView.getBitmap();
            Bitmap createBitmap = Bitmap.createBitmap(this.width, this.height, Config.ARGB_8888);
            Paint paint = new Paint();
            paint.setColor(-1);
            Canvas canvas = new Canvas(createBitmap);
            canvas.drawRect(new Rect(0, 0, this.width, this.height), paint);
            canvas.drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()), new Rect(0, 0, this.width, this.height), null);
            createBitmap.compress(CompressFormat.PNG, 100, null);
            BitMapToString(createBitmap);
            Toast.makeText(getApplicationContext(), "File saved", 0).show();
        } catch (NullPointerException e2) {
            e2.printStackTrace();
            Toast.makeText(getApplicationContext(), "Null error", 0).show();
        } catch (FileNotFoundException e3) {
            e3.printStackTrace();
            Toast.makeText(getApplicationContext(), "File error", 0).show();
        }
    }

    public String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.PNG, 60, byteArrayOutputStream);
        this.Front_Image = Base64.encodeToString(byteArrayOutputStream.toByteArray(), 0);
        return this.Front_Image;
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
}
