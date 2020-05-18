package com.imageliner;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.yarolegovich.discretescrollview.DSVOrientation;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.InfiniteScrollAdapter;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import org.aviran.cookiebar2.CookieBar;
import org.aviran.cookiebar2.OnActionClickListener;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ShopActivity extends AppCompatActivity implements DiscreteScrollView.OnItemChangedListener,
        View.OnClickListener {

    private ArrayList<Item> data = new ArrayList<Item>();
    ArrayList<String> check = new ArrayList<String>();
    ArrayList<String> titles = new ArrayList<String>();
    ArrayList<String> dates = new ArrayList<String>();
    ArrayList<String> images = new ArrayList<String>();
    ArrayList<String> simages = new ArrayList<String>();

    TextView currentItemName;
    TextView currentItemPrice;
    ImageView rateItemButton;
    ImageView itemdelete;
    DiscreteScrollView itemPicker;
    static InfiniteScrollAdapter infiniteAdapter;
    ShopAdapter shopAdapter;

    Animation FabClose;
    Animation FabOpen;
    Animation FabRClockWise;
    Animation FabRanticlockwise;

    Boolean isOpen = false;

    Context context;
    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
//            initView(); // 권한이 승인되었을 때 실행할 함수
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            Toast.makeText(ShopActivity.this, "권한 허용을 하지 않으면 서비스를 이용할 수 없습니다.", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        context = this.getBaseContext();
        Log.d("LOG1", "start");

        currentItemName = (TextView) findViewById(R.id.item_name);
        currentItemPrice = (TextView) findViewById(R.id.item_price);
        rateItemButton = (ImageView) findViewById(R.id.painting);
        itemdelete = (ImageView)findViewById(R.id.item_delete);

        findViewById(R.id.photo).setOnClickListener(this);
        findViewById(R.id.album).setOnClickListener(this);
        findViewById(R.id.add).setOnClickListener(this);

        findViewById(R.id.painting).setOnClickListener(this);
        findViewById(R.id.download).setOnClickListener(this);
        findViewById(R.id.item_delete).setOnClickListener(this);

        findViewById(R.id.menu).setOnClickListener(this);
        findViewById(R.id.btn_smooth_scroll).setOnClickListener(this);
        findViewById(R.id.btn_transition_time).setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
//        checkPermissions();
        ((TedPermission.Builder) ((TedPermission.Builder) ((TedPermission.Builder) ((TedPermission.Builder) TedPermission.with(getApplicationContext())
                .setPermissionListener(this.permissionlistener))
                .setRationaleMessage((CharSequence) ""))
                .setDeniedMessage((CharSequence) "거부하셨습니다."))
                .setPermissions("android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.CAMERA")).check();

        data = new ArrayList<Item>();
        check = getStringArrayPref(this,"check");
        titles = getStringArrayPref(this,"titles");
        dates = getStringArrayPref(this,"dates");
        images = getStringArrayPref(this,"images");
        simages = getStringArrayPref(this,"simages");
        if(check.size()==0){

            Log.d("LOG1","onstart");
            check.add("started");
            titles.add("Welcome");
            dates.add("Add it");
            images.add(BitmapToString(BitmapFactory.decodeResource(context.getResources(), R.drawable.welcom)));
            simages.add(BitmapToString(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.welcom), (int) BitmapFactory.decodeResource(context.getResources(), R.drawable.welcom).getWidth()/2, (int) BitmapFactory.decodeResource(context.getResources(), R.drawable.welcom).getHeight()/2, true)));

            setStringArrayPref(this,"check",check);
            setStringArrayPref(this,"titles",titles);
            setStringArrayPref(this,"dates",dates);
            setStringArrayPref(this,"images",images);
            setStringArrayPref(this,"simages",simages);

            data.add(new Item( "Welcome", "Add it", BitmapFactory.decodeResource(context.getResources(), R.drawable.welcom), Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.welcom), (int) BitmapFactory.decodeResource(context.getResources(), R.drawable.welcom).getWidth()/2, (int) BitmapFactory.decodeResource(context.getResources(), R.drawable.welcom).getHeight()/2, true)));
        }
        else{
            for(int i=0; i<titles.size(); i++) {
                data.add(new Item(titles.get(i), dates.get(i), StringToBitmap(images.get(i)), StringToBitmap(simages.get(i))));
            }
        }

        itemPicker = (DiscreteScrollView) findViewById(R.id.item_picker);
        itemPicker.setOrientation(DSVOrientation.HORIZONTAL);
        itemPicker.addOnItemChangedListener(this);
        shopAdapter = new ShopAdapter(data);
        infiniteAdapter = InfiniteScrollAdapter.wrap(shopAdapter);
        itemPicker.setAdapter(infiniteAdapter);
        itemPicker.setSlideOnFling(true);
        itemPicker.setSlideOnFlingThreshold(2800);
        itemPicker.setItemTransitionTimeMillis(100);
        itemPicker.setItemViewCacheSize(20);
        itemPicker.setHasFixedSize(true);
        itemPicker.setItemTransformer(new ScaleTransformer.Builder()
                .setMinScale(0.8f)
                .build());

        onItemChanged(data.get(0));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_delete:
                new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Are you sure?")
                        .setContentText("Won't be able to recover this file!")
                        .setCancelText("Cancel")
                        .setConfirmText("Delete")
                        .showCancelButton(true)
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.cancel();
                            }
                        })
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                titles.remove(infiniteAdapter.getRealCurrentPosition());
                                dates.remove(infiniteAdapter.getRealCurrentPosition());
                                images.remove(infiniteAdapter.getRealCurrentPosition());
                                simages.remove(infiniteAdapter.getRealCurrentPosition());

                                shopAdapter.removeItem(infiniteAdapter.getRealCurrentPosition());

                                setStringArrayPref(ShopActivity.this,"titles",titles);
                                setStringArrayPref(ShopActivity.this,"dates",dates);
                                setStringArrayPref(ShopActivity.this,"images",images);
                                setStringArrayPref(ShopActivity.this,"simages",simages);


                                sweetAlertDialog.dismiss();
                            }
                        })
                        .show();
                break;
            case R.id.download:
                showUnsupportedSnackBar();
                break;
            case R.id.menu:
                Log.d("LOG1", "menu");
                CookieBar.build(ShopActivity.this)
                        .setCustomView(R.layout.custom_cooki)
//                        .setCustomViewInitializer(new CookieBar.CustomViewInitializer() {
//                            @Override
//                            public void initView(View view) {
//
////                                Button btnNew = view.findViewById(R.id.custom_cookie_btn_new);
////                                Button btnOpen = view.findViewById(R.id.custom_cookie_btn_open);
////                                Button btnSave = view.findViewById(R.id.custom_cookie_btn_save);
//
////                                View.OnClickListener btnListener = new View.OnClickListener() {
////
////                                    @Override
////                                    public void onClick(View view) {
////                                        Button button = (Button) view;
////                                        button.setText("R.string.clicked");
////                                    }
////                                };
////
////                                btnNew.setOnClickListener(btnListener);
////                                btnOpen.setOnClickListener(btnListener);
////                                btnSave.setOnClickListener(btnListener);
//                            }
//                        })
                        .setAction("Close", new OnActionClickListener() {
                            @Override
                            public void onClick() {
                                CookieBar.dismiss(ShopActivity.this);
                                CookieBar.dismiss(ShopActivity.this);
                            }
                        })
                        .setEnableAutoDismiss(false) // Cookie will stay on display until manually dismissed
                        .setSwipeToDismiss(false)    // Deny dismiss by swiping off the view
                        .setCookiePosition(CookieBar.TOP)
                        .show();
                break;
            case R.id.btn_transition_time:
                break;
            case R.id.btn_smooth_scroll:
                Log.d("LOG1", "scroll");
                DiscreteScrollViewOptions.smoothScrollToUserSelectedPosition(itemPicker, v);
                break;
            case R.id.add:
                Log.d("LOG1", "add");
                FabOpen = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
                FabClose = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
                FabRClockWise = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_clockwise);
                FabRanticlockwise = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_anticlockwise);
                if (isOpen) {
                    findViewById(R.id.photo).setAnimation(FabClose);
                    findViewById(R.id.album).setAnimation(FabClose);
                    findViewById(R.id.add).setAnimation(FabRanticlockwise);
                    findViewById(R.id.photo).setClickable(false);
                    findViewById(R.id.album).setClickable(false);
                    isOpen = false;
                    findViewById(R.id.photo).setVisibility(View.INVISIBLE);
                    findViewById(R.id.album).setVisibility(View.INVISIBLE);
                    return;
                }
                else {
                    findViewById(R.id.photo).setAnimation(FabOpen);
                    findViewById(R.id.album).setAnimation(FabOpen);
                    findViewById(R.id.add).setAnimation(FabRClockWise);
                    findViewById(R.id.photo).setClickable(true);
                    findViewById(R.id.album).setClickable(true);
                    isOpen = true;
                    findViewById(R.id.photo).setVisibility(View.VISIBLE);
                    findViewById(R.id.album).setVisibility(View.VISIBLE);
                }
                break;
            case R.id.photo:
                Intent intentphoto = new Intent(ShopActivity.this, MakeLine.class);
                intentphoto.putExtra("type",2);;
                startActivity(intentphoto);
                break;
            case R.id.album:
                Intent intentalbum = new Intent(ShopActivity.this, MakeLine.class);
                intentalbum.putExtra("type",1);
                startActivity(intentalbum);
                break;
            case R.id.painting:
                Intent intentpainting= new Intent(ShopActivity.this, Painting.class);
                intentpainting.putExtra("type",1);
                startActivity(intentpainting);
                break;
            default:
//                showUnsupportedSnackBar();
                break;
        }
    }

    private void startShareDialog(Uri uri)
    {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");

        intent.putExtra(Intent.EXTRA_SUBJECT, "");
        intent.putExtra(Intent.EXTRA_TEXT, "");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(intent, "Share Image"));
    }


    private void onItemChanged(Item item) {
        currentItemName.setText(item.getTitle());
        currentItemPrice.setText(item.getDate());
    }

    public static int getCurrentIndex(){
        return infiniteAdapter.getRealCurrentPosition();
    }

    @Override
    public void onCurrentItemChanged(@Nullable RecyclerView.ViewHolder viewHolder, int position) {
        int positionInDataSet = infiniteAdapter.getRealPosition(position);
        onItemChanged(data.get(positionInDataSet));
    }

    private void showUnsupportedSnackBar() {
        Snackbar.make(itemPicker, "Save Completed", Snackbar.LENGTH_SHORT).show();
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= 23){ // 마시멜로(안드로이드 6.0) 이상 권한 체크
            TedPermission.with(context)
                    .setPermissionListener(permissionlistener)
                    .setRationaleMessage("이미지를 다루기 위해서는 접근 권한이 필요합니다")
                    .setDeniedMessage("앱에서 요구하는 권한설정이 필요합니다...\n [설정] > [권한] 에서 사용으로 활성화해주세요.")
                    .setPermissions(new String[] { Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA})
                    .check();

        } else {
//            initView(); // 권한 승인이 필요없을 때 실행할 함수
        }
    }

//    private void initView() {
//        imageView = (ImageView) findViewById(R.id.croppedImageView);
//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
//    }

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
        bitmap.compress(Bitmap.CompressFormat.PNG, 70, byteArrayOutputStream);
        return Base64.encodeToString(byteArrayOutputStream.toByteArray(), 0);
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
