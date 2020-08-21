package com.imageliner;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;
import com.google.android.gms.ads.AdRequest.Builder;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.yarolegovich.discretescrollview.DSVOrientation;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.Pivot.X;
import com.yarolegovich.discretescrollview.transform.Pivot.Y;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;

public class MainActivity extends AppCompatActivity implements OnNavigationItemSelectedListener {
    Animation FabClose;
    Animation FabOpen;
    Animation FabRClockWise;
    Animation FabRanticlockwise;
    ArrayList<CardItem> dataList = new ArrayList();
    DrawerLayout drawerLayout;
    FloatingActionButton fab_album;
    FloatingActionButton fab_plus;
    FloatingActionButton fab_take_photo;
    ArrayList<String> file_name;
    TextView image_count;
    TextView image_date;
    TextView image_title;
    boolean isOpen = false;
    int layout;
    ArrayList<String> list;
    ArrayList<String> list_image;
    private AdView mAdView;
    MyRecyclerAdapter mAdapter;
    private RewardedVideoAd mRewardedVideoAd;
    NavigationView navigationView;
    PermissionListener permissionListener = new PermissionListener() {
        public void onPermissionDenied(ArrayList<String> arrayList) {
        }

        public void onPermissionGranted() {
        }
    };
    int position;
    RecyclerView recyclerView;
    DiscreteScrollView scrollView;
    ActionBarDrawerToggle toggle;
    Toolbar toolbar;

    private void loadRewardedVideoAd() {
    }

    /* Access modifiers changed, original: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(1024, 1024);
        MobileAds.initialize(this, "ca-app-pub-5524900074277693~2965677484");
        this.mAdView = (AdView) findViewById(R.id.adView_main);
        String str = "B3EEABB8EE11C2BE770B684D95219ECB";
        this.mAdView.loadAd(new Builder().addTestDevice(str).build());
        this.mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        this.mRewardedVideoAd.loadAd("ca-app-pub-5524900074277693/8727757168", new Builder().addTestDevice(str).build());
        loadRewardedVideoAd();
        this.position = 0;
        this.layout = 0;
        this.drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.navigationView = (NavigationView) findViewById(R.id.navigationView);
        setSupportActionBar(this.toolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        this.toggle = new ActionBarDrawerToggle(this, this.drawerLayout, this.toolbar, R.string.drawerOpen, R.string.drawerClose);
        this.drawerLayout.addDrawerListener(this.toggle);
        this.toggle.syncState();
        this.navigationView.setNavigationItemSelectedListener(this);
        this.image_count = (TextView) findViewById(R.id.image_count);
        this.fab_plus = (FloatingActionButton) findViewById(R.id.fab_plus);
        this.fab_take_photo = (FloatingActionButton) findViewById(R.id.fab_take_photo);
        this.fab_album = (FloatingActionButton) findViewById(R.id.fab_album);
        this.FabOpen = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        this.FabClose = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        this.FabRClockWise = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_clockwise);
        this.FabRanticlockwise = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_anticlockwise);
        this.fab_plus.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                MainActivity mainActivity;
                if (MainActivity.this.isOpen) {
                    MainActivity.this.fab_take_photo.setClickable(false);
                    MainActivity.this.fab_album.setClickable(false);
                    mainActivity = MainActivity.this;
                    mainActivity.isOpen = false;
                    mainActivity.fab_take_photo.setVisibility(4);
                    MainActivity.this.fab_album.setVisibility(4);
                    MainActivity.this.fab_take_photo.setAnimation(MainActivity.this.FabClose);
                    MainActivity.this.fab_album.setAnimation(MainActivity.this.FabClose);
                    MainActivity.this.fab_plus.setAnimation(MainActivity.this.FabRanticlockwise);
                    return;
                }
                MainActivity.this.fab_take_photo.setClickable(true);
                MainActivity.this.fab_album.setClickable(true);
                mainActivity = MainActivity.this;
                mainActivity.isOpen = true;
                mainActivity.fab_take_photo.setVisibility(0);
                MainActivity.this.fab_album.setVisibility(0);
                MainActivity.this.fab_take_photo.setAnimation(MainActivity.this.FabOpen);
                MainActivity.this.fab_album.setAnimation(MainActivity.this.FabOpen);
                MainActivity.this.fab_plus.setAnimation(MainActivity.this.FabRClockWise);
            }
        });
        this.fab_take_photo.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                MainActivity.this.startActivity(new Intent(MainActivity.this.getApplicationContext(), MakeLine_camera.class));
                MainActivity.this.overridePendingTransition(R.anim.layout_fadeout, R.anim.layout_fadein);
            }
        });
        this.fab_album.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                MainActivity.this.startActivity(new Intent(MainActivity.this.getApplicationContext(), MakeLine.class));
                MainActivity.this.overridePendingTransition(R.anim.layout_fadeout, R.anim.layout_fadein);
            }
        });
        this.recyclerView = (RecyclerView) findViewById(R.id.picker_grid);
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
        defaultItemAnimator.setAddDuration(100);
        defaultItemAnimator.setRemoveDuration(100);
        defaultItemAnimator.setMoveDuration(100);
        defaultItemAnimator.setChangeDuration(100);
        this.recyclerView.setItemAnimator(defaultItemAnimator);
        this.scrollView = (DiscreteScrollView) findViewById(R.id.picker);
        this.scrollView.setSlideOnFling(true);
        this.scrollView.setOrientation(DSVOrientation.HORIZONTAL);
        this.scrollView.setItemTransformer(new ScaleTransformer.Builder().setMaxScale(1.0f).setMinScale(0.85f).setPivotX(X.CENTER).setPivotY(Y.CENTER).build());
        this.image_title = (TextView) findViewById(R.id.image_title);
        this.image_title.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (MainActivity.this.list.size() > 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle("New Title");
                    builder.setMessage("Please enter a new title.");
                    final EditText editText = new EditText(view.getContext());
                    editText.setFilters(new InputFilter[]{new LengthFilter(100)});
                    builder.setView(editText);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String obj = editText.getText().toString();
                            MainActivity.this.image_title.setText(obj);
                            MainActivity.this.file_name = new ArrayList();
                            String str = "file_name";
                            MainActivity.this.file_name = MainActivity.this.getStringArrayPref(MainActivity.this.getApplicationContext(), str);
                            MainActivity.this.file_name.set(MainActivity.this.position, obj);
                            MainActivity.this.setStringArrayPref(MainActivity.this.getApplicationContext(), str, MainActivity.this.file_name);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
                    builder.show();
                }
            }
        });
        this.image_date = (TextView) findViewById(R.id.image_date);
        this.scrollView.addOnScrollListener(new OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                super.onScrollStateChanged(recyclerView, i);
                if (i == 0 && MainActivity.this.dataList.size() > 0) {
                    MainActivity mainActivity = MainActivity.this;
                    mainActivity.position = mainActivity.scrollView.getCurrentItem();
                    MainActivity.this.image_date.setText(((CardItem) MainActivity.this.dataList.get(MainActivity.this.position)).getTitle());
                    MainActivity.this.image_title.setText((CharSequence) MainActivity.this.file_name.get(MainActivity.this.position));
                }
            }
        });
        final FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.download);
        floatingActionButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                MainActivity mainActivity = MainActivity.this;
                mainActivity.SaveImage(((CardItem) mainActivity.dataList.get(MainActivity.this.position)).getImage());
                Toast.makeText(MainActivity.this.getApplicationContext(), "Save completed", 1).show();
                if (MainActivity.this.mRewardedVideoAd.isLoaded()) {
                    MainActivity.this.mRewardedVideoAd.show();
                }
            }
        });
        final ImageButton imageButton = (ImageButton) findViewById(R.id.button_delete);
        imageButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (MainActivity.this.list.size() > 0) {
                    int i = MainActivity.this.position;
                    String str = "list_image";
                    String str2 = "list";
                    String str3 = "file_name";
                    if (MainActivity.this.position != 0 || (MainActivity.this.position == 0 && MainActivity.this.list.size() == 1)) {
                        if (MainActivity.this.list.size() > 1) {
                            MainActivity.this.image_title.setText((CharSequence) MainActivity.this.file_name.get(MainActivity.this.position - 1));
                        }
                        if (MainActivity.this.list.size() > 1) {
                            MainActivity.this.image_date.setText(((CardItem) MainActivity.this.dataList.get(MainActivity.this.position - 1)).getTitle());
                        }
                        MainActivity.this.mAdapter.removeItem(MainActivity.this.position);
                        MainActivity.this.file_name.remove(MainActivity.this.position);
                        MainActivity.this.list.remove(MainActivity.this.position);
                        MainActivity.this.list_image.remove(MainActivity.this.position);
                        if (MainActivity.this.list.size() == 0) {
                            MainActivity.this.image_title.setText("Add new Image");
                        }
                        if (MainActivity.this.list.size() == 0) {
                            MainActivity.this.image_date.setText("Please press + button");
                        }
                        MainActivity.this.setStringArrayPref(view.getContext(), str3, MainActivity.this.file_name);
                        MainActivity.this.setStringArrayPref(view.getContext(), str2, MainActivity.this.list);
                        MainActivity.this.setStringArrayPref(view.getContext(), str, MainActivity.this.list_image);
                        if (i != 0) {
                            MainActivity.this.scrollView.scrollToPosition(i - 1);
                        }
                        MainActivity mainActivity = MainActivity.this;
                        mainActivity.position--;
                        return;
                    }
                    MainActivity.this.image_title.setText((CharSequence) MainActivity.this.file_name.get(MainActivity.this.position + 1));
                    MainActivity.this.image_date.setText(((CardItem) MainActivity.this.dataList.get(MainActivity.this.position + 1)).getTitle());
                    MainActivity.this.mAdapter.removeItem(MainActivity.this.position);
                    MainActivity.this.file_name.remove(MainActivity.this.position);
                    MainActivity.this.list.remove(MainActivity.this.position);
                    MainActivity.this.list_image.remove(MainActivity.this.position);
                    MainActivity.this.setStringArrayPref(view.getContext(), str3, MainActivity.this.file_name);
                    MainActivity.this.setStringArrayPref(view.getContext(), str2, MainActivity.this.list);
                    MainActivity.this.setStringArrayPref(view.getContext(), str, MainActivity.this.list_image);
                    MainActivity.this.scrollView.scrollToPosition(MainActivity.this.position + 1);
                }
            }
        });
        final ImageButton imageButton2 = (ImageButton) findViewById(R.id.button_paint);
        imageButton2.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (MainActivity.this.list.size() > 0) {
                    Intent intent = new Intent(MainActivity.this.getApplicationContext(), Painting.class);
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(MainActivity.this.list_image.get(MainActivity.this.position));
                    MainActivity.this.setStringArrayPref(view.getContext(), "pass_image", arrayList);
                    MainActivity.this.startActivity(intent);
                    MainActivity.this.overridePendingTransition(R.anim.layout_fadein, R.anim.layout_fadeout);
                }
            }
        });
        FloatingActionButton floatingActionButton2 = (FloatingActionButton) findViewById(R.id.grid_layout);
        final FloatingActionButton floatingActionButton3 = floatingActionButton2;
        floatingActionButton2.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (MainActivity.this.layout == 0) {
                    MainActivity.this.recyclerView.setVisibility(0);
                    MainActivity.this.image_count.setVisibility(0);
                    floatingActionButton.setVisibility(4);
                    MainActivity.this.scrollView.setVisibility(4);
                    imageButton.setVisibility(4);
                    MainActivity.this.image_date.setVisibility(4);
                    MainActivity.this.image_title.setVisibility(4);
                    floatingActionButton.setVisibility(4);
                    imageButton2.setVisibility(4);
                    floatingActionButton3.setImageResource(R.drawable.ic_view_carousel_black_24dp);
                    MainActivity mainActivity = MainActivity.this;
                    mainActivity.layout = 1;
                    TextView textView = mainActivity.image_count;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Total of ");
                    stringBuilder.append(MainActivity.this.list.size());
                    stringBuilder.append(" images");
                    textView.setText(stringBuilder.toString());
                    return;
                }
                MainActivity.this.recyclerView.setVisibility(4);
                MainActivity.this.image_count.setVisibility(4);
                floatingActionButton.setVisibility(0);
                MainActivity.this.scrollView.setVisibility(0);
                imageButton.setVisibility(0);
                MainActivity.this.image_date.setVisibility(0);
                MainActivity.this.image_title.setVisibility(0);
                floatingActionButton.setVisibility(0);
                imageButton2.setVisibility(0);
                floatingActionButton3.setImageResource(R.drawable.ic_view_compact_black_24dp);
                MainActivity.this.layout = 0;
            }
        });
    }

    /* Access modifiers changed, original: protected */
    public void onStart() {
        super.onStart();
        ((TedPermission.Builder) ((TedPermission.Builder) ((TedPermission.Builder) ((TedPermission.Builder) TedPermission.with(getApplicationContext()).setPermissionListener(this.permissionListener)).setRationaleMessage((CharSequence) "")).setDeniedMessage((CharSequence) "거부하셨습니다.")).setPermissions("android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.CAMERA")).check();
        this.file_name = new ArrayList();
        String str = "file_name";
        this.file_name = getStringArrayPref(this, str);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("file_name size : ");
        stringBuilder.append(this.file_name.size());
        String str2 = "A";
        //Log.d(str2, stringBuilder.toString());
        this.list = new ArrayList();
        String str3 = "list";
        this.list = getStringArrayPref(this, str3);
        this.list_image = new ArrayList();
        String str4 = "list_image";
        this.list_image = getStringArrayPref(this, str4);
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append("list image size : ");
        stringBuilder2.append(this.list_image.size());
        //Log.d(str2, stringBuilder2.toString());
        String str5 = "check_first";
        ArrayList stringArrayPref = getStringArrayPref(getApplicationContext(), str5);
        if (stringArrayPref.size() == 0) {
            this.file_name.add("Make your image");
            this.list.add("press + button");
            this.list_image.add(BitmapToString(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.welcome)));
            setStringArrayPref(getApplicationContext(), str, this.file_name);
            setStringArrayPref(getApplicationContext(), str3, this.list);
            setStringArrayPref(getApplicationContext(), str4, this.list_image);
            stringArrayPref.add("1");
            setStringArrayPref(getApplicationContext(), str5, stringArrayPref);
        }
        this.dataList = new ArrayList();
        for (int i = 0; i < this.list.size(); i++) {
            this.dataList.add(new CardItem((String) this.list.get(i), StringToBitmap((String) this.list_image.get(i))));
        }
        if (this.list.size() > 0) {
            this.image_title.setText((CharSequence) this.file_name.get(this.position));
        }
        if (this.list.size() > 0) {
            this.image_date.setText(((CardItem) this.dataList.get(this.position)).getTitle());
        }
        this.mAdapter = new MyRecyclerAdapter(this.dataList);
        this.recyclerView.setAdapter(this.mAdapter);
        this.scrollView.setAdapter(this.mAdapter);
        this.scrollView.scrollToPosition(this.position);
    }

    /* Access modifiers changed, original: protected */
    public void onStop() {
        super.onStop();
    }

    public boolean onNavigationItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 2131296399) {
            startActivity(new Intent(getApplicationContext(), info.class));
        }
        return false;
    }

    public <E> List<E> getJsonArrayList(Context context, String str) {
        String string = PreferenceManager.getDefaultSharedPreferences(context).getString(str, null);
        ArrayList arrayList = new ArrayList();
        if (string != null) {
            try {
                JSONArray jSONArray = new JSONArray(string);
                for (int i = 0; i < jSONArray.length(); i++) {
                    arrayList.add(jSONArray.opt(i));
                }
            } catch (JSONException e) {
                Log.e("TEST", e.toString());
            }
        }
        return arrayList;
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

    public static Bitmap StringToBitmap(String str) {
        try {
            byte[] decode = Base64.decode(str, 0);
            return BitmapFactory.decodeByteArray(decode, 0, decode.length);
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public static String BitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.PNG, 70, byteArrayOutputStream);
        return Base64.encodeToString(byteArrayOutputStream.toByteArray(), 0);
    }

    public void setStringArrayPref(Context context, String str, ArrayList<String> arrayList) {
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

    public void SaveImage(Bitmap bitmap) {
        String str = Environment.getExternalStorageDirectory().getAbsolutePath().toString();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(str);
        str = "/Image_Liner/";
        stringBuilder.append(str);
        File file = new File(stringBuilder.toString());
        file.mkdirs();
        String format = new SimpleDateFormat("yyyy_MM_dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append(format);
        stringBuilder2.append(".jpg");
        format = stringBuilder2.toString();
        File file2 = new File(file, format);
        if (file2.exists()) {
            file2.delete();
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file2);
            bitmap.compress(CompressFormat.JPEG, 90, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        StringBuilder stringBuilder3 = new StringBuilder();
        stringBuilder3.append("content://");
        stringBuilder3.append(Environment.getExternalStorageDirectory().toString());
        stringBuilder3.append(str);
        stringBuilder3.append(format);
        String str2 = "android.intent.action.MEDIA_SCANNER_SCAN_FILE";
        sendBroadcast(new Intent(str2, Uri.parse(stringBuilder3.toString())));
        getApplicationContext().sendBroadcast(new Intent(str2, Uri.fromFile(file2)));
    }
}
