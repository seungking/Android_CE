package com.imageliner;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.ads.InterstitialAd;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import org.json.JSONArray;
import org.json.JSONException;

public class MakeLine_color extends AppCompatActivity {
    int background = -1;
    Bitmap bitmapOutput;
    ImageView imageVIewOuput;
    int line = -16777216;
    private InterstitialAd mInterstitialAd;

    static {
        System.loadLibrary("opencv_java4");
        System.loadLibrary("native-lib");
    }

    /* Access modifiers changed, original: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_make_line_color);
        getWindow().setFlags(1024, 1024);
        //Log.d("cameraimage", "started color");
        this.bitmapOutput = StringToBitmap((String) getStringArrayPref(getApplicationContext(), "pass_image").get(0));
        this.imageVIewOuput = (ImageView) findViewById(R.id.imageViewOutput_color);
        this.imageVIewOuput.setImageBitmap(this.bitmapOutput);
        ((TextView) findViewById(R.id.color_done)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                String str;
                String str2 = "file_name";
                ArrayList access$000 = MakeLine_color.this.getStringArrayPref(view.getContext(), str2);
                if (access$000.size() == 0) {
                    access$000.add("Untitled 0");
                } else {
                    int size = access$000.size();
                    int i = 0;
                    while (true) {
                        str = "Untitled ";
                        if (i >= access$000.size()) {
                            break;
                        }
                        String str3 = (String) access$000.get(i);
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(str);
                        stringBuilder.append(size);
                        if (str3.contains(stringBuilder.toString())) {
                            size++;
                        }
                        i++;
                    }
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append(str);
                    stringBuilder2.append(size);
                    access$000.add(stringBuilder2.toString());
                }
                String format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
                str = "list";
                ArrayList access$0002 = MakeLine_color.this.getStringArrayPref(view.getContext(), str);
                access$0002.add(format);
                format = MakeLine_color.BitmapToString(MakeLine_color.this.bitmapOutput);
                String str4 = "list_image";
                ArrayList access$0003 = MakeLine_color.this.getStringArrayPref(view.getContext(), str4);
                access$0003.add(format);
                MakeLine_color.this.setStringArrayPref(view.getContext(), str2, access$000);
                MakeLine_color.this.setStringArrayPref(view.getContext(), str, access$0002);
                MakeLine_color.this.setStringArrayPref(view.getContext(), str4, access$0003);
                MakeLine_color.this.startActivity(new Intent(MakeLine_color.this.getApplicationContext(), MainActivity.class));
                Toast.makeText(MakeLine_color.this.getApplicationContext(), "Done", 1).show();
                MakeLine_color.this.finish();
            }
        });
        ((ImageButton) findViewById(R.id.button_back_color)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                MakeLine_color.this.finish();
                MakeLine_color.this.overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
            }
        });
        ((Button) findViewById(R.id.background_1)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                int parseColor = Color.parseColor("#D65353");
                if (parseColor != MakeLine_color.this.background && parseColor != MakeLine_color.this.line) {
                    MakeLine_color makeLine_color = MakeLine_color.this;
                    makeLine_color.bitmapOutput = makeLine_color.replaceColor(makeLine_color.bitmapOutput, MakeLine_color.this.background, parseColor);
                    makeLine_color = MakeLine_color.this;
                    makeLine_color.background = parseColor;
                    makeLine_color.imageVIewOuput.setImageBitmap(MakeLine_color.this.bitmapOutput);
                }
            }
        });
        ((Button) findViewById(R.id.background_2)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                int parseColor = Color.parseColor("#FD8B64");
                if (parseColor != MakeLine_color.this.background && parseColor != MakeLine_color.this.line) {
                    MakeLine_color makeLine_color = MakeLine_color.this;
                    makeLine_color.bitmapOutput = makeLine_color.replaceColor(makeLine_color.bitmapOutput, MakeLine_color.this.background, parseColor);
                    makeLine_color = MakeLine_color.this;
                    makeLine_color.background = parseColor;
                    makeLine_color.imageVIewOuput.setImageBitmap(MakeLine_color.this.bitmapOutput);
                }
            }
        });
        ((Button) findViewById(R.id.background_3)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                int parseColor = Color.parseColor("#FCD353");
                if (parseColor != MakeLine_color.this.background && parseColor != MakeLine_color.this.line) {
                    MakeLine_color makeLine_color = MakeLine_color.this;
                    makeLine_color.bitmapOutput = makeLine_color.replaceColor(makeLine_color.bitmapOutput, MakeLine_color.this.background, parseColor);
                    makeLine_color = MakeLine_color.this;
                    makeLine_color.background = parseColor;
                    makeLine_color.imageVIewOuput.setImageBitmap(MakeLine_color.this.bitmapOutput);
                }
            }
        });
        ((Button) findViewById(R.id.background_4)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                int parseColor = Color.parseColor("#99D45D");
                if (parseColor != MakeLine_color.this.background && parseColor != MakeLine_color.this.line) {
                    MakeLine_color makeLine_color = MakeLine_color.this;
                    makeLine_color.bitmapOutput = makeLine_color.replaceColor(makeLine_color.bitmapOutput, MakeLine_color.this.background, parseColor);
                    makeLine_color = MakeLine_color.this;
                    makeLine_color.background = parseColor;
                    makeLine_color.imageVIewOuput.setImageBitmap(MakeLine_color.this.bitmapOutput);
                }
            }
        });
        ((Button) findViewById(R.id.background_5)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                int parseColor = Color.parseColor("#0181BB");
                if (parseColor != MakeLine_color.this.background && parseColor != MakeLine_color.this.line) {
                    MakeLine_color makeLine_color = MakeLine_color.this;
                    makeLine_color.bitmapOutput = makeLine_color.replaceColor(makeLine_color.bitmapOutput, MakeLine_color.this.background, parseColor);
                    makeLine_color = MakeLine_color.this;
                    makeLine_color.background = parseColor;
                    makeLine_color.imageVIewOuput.setImageBitmap(MakeLine_color.this.bitmapOutput);
                }
            }
        });
        ((Button) findViewById(R.id.background_6)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                int parseColor = Color.parseColor("#234257");
                if (parseColor != MakeLine_color.this.background && parseColor != MakeLine_color.this.line) {
                    MakeLine_color makeLine_color = MakeLine_color.this;
                    makeLine_color.bitmapOutput = makeLine_color.replaceColor(makeLine_color.bitmapOutput, MakeLine_color.this.background, parseColor);
                    makeLine_color = MakeLine_color.this;
                    makeLine_color.background = parseColor;
                    makeLine_color.imageVIewOuput.setImageBitmap(MakeLine_color.this.bitmapOutput);
                }
            }
        });
        ((Button) findViewById(R.id.background_7)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                int parseColor = Color.parseColor("#9979C1");
                if (parseColor != MakeLine_color.this.background && parseColor != MakeLine_color.this.line) {
                    MakeLine_color makeLine_color = MakeLine_color.this;
                    makeLine_color.bitmapOutput = makeLine_color.replaceColor(makeLine_color.bitmapOutput, MakeLine_color.this.background, parseColor);
                    makeLine_color = MakeLine_color.this;
                    makeLine_color.background = parseColor;
                    makeLine_color.imageVIewOuput.setImageBitmap(MakeLine_color.this.bitmapOutput);
                }
            }
        });
        ((Button) findViewById(R.id.background_8)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                int parseColor = Color.parseColor("#FFFFFF");
                if (parseColor != MakeLine_color.this.background && parseColor != MakeLine_color.this.line) {
                    MakeLine_color makeLine_color = MakeLine_color.this;
                    makeLine_color.bitmapOutput = makeLine_color.replaceColor(makeLine_color.bitmapOutput, MakeLine_color.this.background, parseColor);
                    makeLine_color = MakeLine_color.this;
                    makeLine_color.background = parseColor;
                    makeLine_color.imageVIewOuput.setImageBitmap(MakeLine_color.this.bitmapOutput);
                }
            }
        });
        ((Button) findViewById(R.id.background_9)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                int parseColor = Color.parseColor("#6D7172");
                if (parseColor != MakeLine_color.this.background && parseColor != MakeLine_color.this.line) {
                    MakeLine_color makeLine_color = MakeLine_color.this;
                    makeLine_color.bitmapOutput = makeLine_color.replaceColor(makeLine_color.bitmapOutput, MakeLine_color.this.background, parseColor);
                    makeLine_color = MakeLine_color.this;
                    makeLine_color.background = parseColor;
                    makeLine_color.imageVIewOuput.setImageBitmap(MakeLine_color.this.bitmapOutput);
                }
            }
        });
        ((Button) findViewById(R.id.background_10)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                int parseColor = Color.parseColor("#FB050505");
                if (parseColor != MakeLine_color.this.background && parseColor != MakeLine_color.this.line) {
                    MakeLine_color makeLine_color = MakeLine_color.this;
                    makeLine_color.bitmapOutput = makeLine_color.replaceColor(makeLine_color.bitmapOutput, MakeLine_color.this.background, parseColor);
                    makeLine_color = MakeLine_color.this;
                    makeLine_color.background = parseColor;
                    makeLine_color.imageVIewOuput.setImageBitmap(MakeLine_color.this.bitmapOutput);
                }
            }
        });
        ((Button) findViewById(R.id.line_1)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                int parseColor = Color.parseColor("#D65353");
                if (parseColor != MakeLine_color.this.line && parseColor != MakeLine_color.this.background) {
                    MakeLine_color makeLine_color = MakeLine_color.this;
                    makeLine_color.bitmapOutput = makeLine_color.replaceColor(makeLine_color.bitmapOutput, MakeLine_color.this.line, parseColor);
                    makeLine_color = MakeLine_color.this;
                    makeLine_color.line = parseColor;
                    makeLine_color.imageVIewOuput.setImageBitmap(MakeLine_color.this.bitmapOutput);
                }
            }
        });
        ((Button) findViewById(R.id.line_2)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                int parseColor = Color.parseColor("#FD8B64");
                if (parseColor != MakeLine_color.this.line && parseColor != MakeLine_color.this.background) {
                    MakeLine_color makeLine_color = MakeLine_color.this;
                    makeLine_color.bitmapOutput = makeLine_color.replaceColor(makeLine_color.bitmapOutput, MakeLine_color.this.line, parseColor);
                    makeLine_color = MakeLine_color.this;
                    makeLine_color.line = parseColor;
                    makeLine_color.imageVIewOuput.setImageBitmap(MakeLine_color.this.bitmapOutput);
                }
            }
        });
        ((Button) findViewById(R.id.line_3)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                int parseColor = Color.parseColor("#FCD353");
                if (parseColor != MakeLine_color.this.line && parseColor != MakeLine_color.this.background) {
                    MakeLine_color makeLine_color = MakeLine_color.this;
                    makeLine_color.bitmapOutput = makeLine_color.replaceColor(makeLine_color.bitmapOutput, MakeLine_color.this.line, parseColor);
                    makeLine_color = MakeLine_color.this;
                    makeLine_color.line = parseColor;
                    makeLine_color.imageVIewOuput.setImageBitmap(MakeLine_color.this.bitmapOutput);
                }
            }
        });
        ((Button) findViewById(R.id.line_4)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                int parseColor = Color.parseColor("#99D45D");
                if (parseColor != MakeLine_color.this.line && parseColor != MakeLine_color.this.background) {
                    MakeLine_color makeLine_color = MakeLine_color.this;
                    makeLine_color.bitmapOutput = makeLine_color.replaceColor(makeLine_color.bitmapOutput, MakeLine_color.this.line, parseColor);
                    makeLine_color = MakeLine_color.this;
                    makeLine_color.line = parseColor;
                    makeLine_color.imageVIewOuput.setImageBitmap(MakeLine_color.this.bitmapOutput);
                }
            }
        });
        ((Button) findViewById(R.id.line_5)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                int parseColor = Color.parseColor("#0181BB");
                if (parseColor != MakeLine_color.this.line && parseColor != MakeLine_color.this.background) {
                    MakeLine_color makeLine_color = MakeLine_color.this;
                    makeLine_color.bitmapOutput = makeLine_color.replaceColor(makeLine_color.bitmapOutput, MakeLine_color.this.line, parseColor);
                    makeLine_color = MakeLine_color.this;
                    makeLine_color.line = parseColor;
                    makeLine_color.imageVIewOuput.setImageBitmap(MakeLine_color.this.bitmapOutput);
                }
            }
        });
        ((Button) findViewById(R.id.line_6)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                int parseColor = Color.parseColor("#234257");
                if (parseColor != MakeLine_color.this.line && parseColor != MakeLine_color.this.background) {
                    MakeLine_color makeLine_color = MakeLine_color.this;
                    makeLine_color.bitmapOutput = makeLine_color.replaceColor(makeLine_color.bitmapOutput, MakeLine_color.this.line, parseColor);
                    makeLine_color = MakeLine_color.this;
                    makeLine_color.line = parseColor;
                    makeLine_color.imageVIewOuput.setImageBitmap(MakeLine_color.this.bitmapOutput);
                }
            }
        });
        ((Button) findViewById(R.id.line_7)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                int parseColor = Color.parseColor("#9979C1");
                if (parseColor != MakeLine_color.this.line && parseColor != MakeLine_color.this.background) {
                    MakeLine_color makeLine_color = MakeLine_color.this;
                    makeLine_color.bitmapOutput = makeLine_color.replaceColor(makeLine_color.bitmapOutput, MakeLine_color.this.line, parseColor);
                    makeLine_color = MakeLine_color.this;
                    makeLine_color.line = parseColor;
                    makeLine_color.imageVIewOuput.setImageBitmap(MakeLine_color.this.bitmapOutput);
                }
            }
        });
        ((Button) findViewById(R.id.line_8)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                int parseColor = Color.parseColor("#FFFFFF");
                if (parseColor != MakeLine_color.this.line && parseColor != MakeLine_color.this.background) {
                    MakeLine_color makeLine_color = MakeLine_color.this;
                    makeLine_color.bitmapOutput = makeLine_color.replaceColor(makeLine_color.bitmapOutput, MakeLine_color.this.line, parseColor);
                    makeLine_color = MakeLine_color.this;
                    makeLine_color.line = parseColor;
                    makeLine_color.imageVIewOuput.setImageBitmap(MakeLine_color.this.bitmapOutput);
                }
            }
        });
        ((Button) findViewById(R.id.line_9)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                int parseColor = Color.parseColor("#6D7172");
                if (parseColor != MakeLine_color.this.line && parseColor != MakeLine_color.this.background) {
                    MakeLine_color makeLine_color = MakeLine_color.this;
                    makeLine_color.bitmapOutput = makeLine_color.replaceColor(makeLine_color.bitmapOutput, MakeLine_color.this.line, parseColor);
                    makeLine_color = MakeLine_color.this;
                    makeLine_color.line = parseColor;
                    makeLine_color.imageVIewOuput.setImageBitmap(MakeLine_color.this.bitmapOutput);
                }
            }
        });
        ((Button) findViewById(R.id.line_10)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                int parseColor = Color.parseColor("#FB050505");
                if (parseColor != MakeLine_color.this.line && parseColor != MakeLine_color.this.background) {
                    MakeLine_color makeLine_color = MakeLine_color.this;
                    makeLine_color.bitmapOutput = makeLine_color.replaceColor(makeLine_color.bitmapOutput, MakeLine_color.this.line, parseColor);
                    makeLine_color = MakeLine_color.this;
                    makeLine_color.line = parseColor;
                    makeLine_color.imageVIewOuput.setImageBitmap(MakeLine_color.this.bitmapOutput);
                }
            }
        });
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
        int i3 = 0;
        while (i3 < iArr.length) {
            iArr[i3] = iArr[i3] == i ? i2 : iArr[i3];
            i3++;
        }
        bitmap = Bitmap.createBitmap(width, height, bitmap.getConfig());
        bitmap.setPixels(iArr, 0, width, 0, 0, width, height);
        return bitmap;
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

    /* Access modifiers changed, original: protected */
    public void onStop() {
        super.onStop();
        finish();
        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
    }
}
