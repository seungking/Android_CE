package com.imageliner;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Base64;
import android.view.Display;
import android.view.View;
import android.view.ViewManager;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.Constants;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.snackbar.Snackbar;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.imageliner.models.AppStorage;
import com.imageliner.manage.DiscreteScrollViewOptions;
import com.imageliner.models.Item;
import com.imageliner.adapter.ShopAdapter;
import com.imageliner.dialog.SweetAlertDialog;
import com.vansuita.materialabout.builder.AboutBuilder;
import com.vansuita.materialabout.views.AboutView;
import com.yarolegovich.discretescrollview.DSVOrientation;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.InfiniteScrollAdapter;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements DiscreteScrollView.OnItemChangedListener,
        View.OnClickListener, BillingProcessor.IBillingHandler {

    private ArrayList<Item> data = new ArrayList<Item>();
    ArrayList<String> check = new ArrayList<String>();
    ArrayList<String> titles = new ArrayList<String>();
    ArrayList<String> dates = new ArrayList<String>();
    ArrayList<String> images = new ArrayList<String>();
    ArrayList<String> simages = new ArrayList<String>();

    LinearLayout linearLayout;
    AboutView view;
    private InterstitialAd mInterstitialAd;

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
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            Toast.makeText(MainActivity.this, "권한 허용을 하지 않으면 서비스를 이용할 수 없습니다.", Toast.LENGTH_SHORT).show();
        }
    };

    private AdView mAdView;
    private BillingProcessor bp;
    private AppStorage storage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        context = this.getBaseContext();

        bp = new BillingProcessor(this, "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlzZvyf8fo8yzDEVD8xa3sfRBWgpfT9OuuXjhR+6efl/qDKKphqYFCOLOjYrkhG2bWGMBMLtN47szIEafw9tzJy9h57KZRkoRo8ZPDUfG1zG7UEstWh0MsVWTrf8Gd0H4ob5cmNqc/pGTSGeGjU+gtTUiAC2+8W9v5QroBp6W5UEMfp7m7GvTc+LocFCIQV3t43bResun/+p/SwuIOkYzF28uBicwxoLcltAHTY8Cn82kT7KMAex+JPgcESMw/hNUCTSaNLIMiWeIH2WSX1k2jmAE7uaM6Ygys+UrcmXxbaMUC5egnAkt7NOnsDtsqj9hgqj9KPyG6dBdLjpVKLIkUwIDAQAB", this);
//        bp.initialize();
//        storage = new AppStorage(this);

        ArrayList<String> noad  = getStringArrayPref(this,"noad");
        if (noad.size()==0){
            MobileAds.initialize(this, "ca-app-pub-1992325656759505~6979611558");
            mAdView = findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
            mInterstitialAd = new InterstitialAd(this);
            mInterstitialAd.setAdUnitId("ca-app-pub-1992325656759505/1090443323");
            mInterstitialAd.loadAd(new AdRequest.Builder().build());
        }


        currentItemName = (TextView) findViewById(R.id.item_name);
        currentItemPrice = (TextView) findViewById(R.id.item_price);
        rateItemButton = (ImageView) findViewById(R.id.painting);
        itemdelete = (ImageView)findViewById(R.id.item_delete);

        findViewById(R.id.item_name).setOnClickListener(this);
        findViewById(R.id.photo).setOnClickListener(this);
        findViewById(R.id.album).setOnClickListener(this);
        findViewById(R.id.add).setOnClickListener(this);

        findViewById(R.id.painting).setOnClickListener(this);
        findViewById(R.id.download).setOnClickListener(this);
        findViewById(R.id.item_delete).setOnClickListener(this);

        findViewById(R.id.menu).setOnClickListener(this);
        findViewById(R.id.btn_smooth_scroll).setOnClickListener(this);
        //Log.d("LOG1", "AFTER DECLARE AD1");
        view = AboutBuilder.with(this)
                .setCover(R.mipmap.ic_launcher_round)
                .setAppIcon(R.mipmap.ic_launcher_foreground)
                .setAppName(R.string.app_name)
                .addFiveStarsAction()
                .addFeedbackAction("ahnseungkl@gmail.com")
                .setWrapScrollView(true)
                .setLinksAnimated(true)
                .addDonateAction(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bp.purchase(MainActivity.this, "ce_donate");
                    }
                })
//                .addRemoveAdsAction(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        //Log.d("LOG1", "NO AD");
//                        ArrayList<String> noad  = getStringArrayPref(v.getContext(),"noad");
//                        if (noad.size()>0) {
//                            Snackbar.make(itemPicker, "You've already purchased it.", Snackbar.LENGTH_SHORT).show();
//                        } else {
//                            bp.purchase(MainActivity.this, "ce_no_ad");
//                        //Log.d("LOG1", "NO AD1");
//                            if(noad.size()>0){
//                                Intent intent = getIntent();
//                                intent.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                                finish();
//                                startActivity(intent);
//                            }
//                        }
//                    }
//                })
                .setVersionNameAsAppSubTitle()
                .build();
        //Log.d("LOG1", "AFTER DECLARE AD2");
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Log.d("LOG1", "onstart");
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

            //Log.d("LOG1","onstart");
            check.add("started");
            titles.add("Welcome");
            dates.add("Make Your Image!");
            images.add(BitmapToString(BitmapFactory.decodeResource(context.getResources(), R.drawable.welcom)));
            simages.add(BitmapToString(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.welcom), (int) BitmapFactory.decodeResource(context.getResources(), R.drawable.welcom).getWidth()/2, (int) BitmapFactory.decodeResource(context.getResources(), R.drawable.welcom).getHeight()/2, true)));

            setStringArrayPref(this,"check",check);
            setStringArrayPref(this,"titles",titles);
            setStringArrayPref(this,"dates",dates);
            setStringArrayPref(this,"images",images);
            setStringArrayPref(this,"simages",simages);

            data.add(new Item( "Welcome", "Make Your Image!", BitmapFactory.decodeResource(context.getResources(), R.drawable.welcom), Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.welcom), (int) BitmapFactory.decodeResource(context.getResources(), R.drawable.welcom).getWidth()/2, (int) BitmapFactory.decodeResource(context.getResources(), R.drawable.welcom).getHeight()/2, true)));
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
            case R.id.item_name :
                if(titles.size()==1&&titles.get(0).equals("Press '+' Button")){
                    Snackbar.make(itemPicker, "ADD NEW!", Snackbar.LENGTH_SHORT).show();
                }
                else {
                    new MaterialDialog.Builder(this)
                            .title("New Title")
                            .content("Please enter a new title.")
                            .positiveColor(Color.BLACK)
                            .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)
                            .input(null, null, new MaterialDialog.InputCallback() {
                                @Override
                                public void onInput(MaterialDialog dialog, CharSequence input) {
                                    titles.set(infiniteAdapter.getRealCurrentPosition(), input.toString());

                                    setStringArrayPref(MainActivity.this, "titles", titles);

                                    Intent intent = getIntent();
                                    intent.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    finish();
                                    startActivity(intent);
                                }
                            }).show();
                }
                break;
            case R.id.item_delete:
                if(titles.size()==1&&titles.get(0).equals("Press '+' Button")){
                    Snackbar.make(itemPicker, "ADD NEW!", Snackbar.LENGTH_SHORT).show();
                }
                else {
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
                                    //Log.d("LOG1", "CURRENT INDEX1 : " + String.valueOf(infiniteAdapter.getRealCurrentPosition()));

                                    if (titles.size()==1) {

                                        titles.add("Press '+' Button");
                                        dates.add("Contour Extractor");
                                        images.add(BitmapToString(BitmapFactory.decodeResource(context.getResources(), R.drawable.addnew)));
                                        simages.add(BitmapToString(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.addnew), (int) BitmapFactory.decodeResource(context.getResources(), R.drawable.addnew).getWidth() / 2, (int) BitmapFactory.decodeResource(context.getResources(), R.drawable.addnew).getHeight() / 2, true)));

                                        Intent intent = getIntent();
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                        finish();
                                        startActivity(intent);
                                    }
                                    else {

                                        infiniteAdapter.notifyItemRemoved(infiniteAdapter.getRealCurrentPosition());
                                        infiniteAdapter.notifyDataSetChanged();
                                        sweetAlertDialog.dismiss();
                                    }

                                    titles.remove(infiniteAdapter.getRealCurrentPosition());
                                    dates.remove(infiniteAdapter.getRealCurrentPosition());
                                    images.remove(infiniteAdapter.getRealCurrentPosition());
                                    simages.remove(infiniteAdapter.getRealCurrentPosition());
                                    data.remove(infiniteAdapter.getRealCurrentPosition());

                                    setStringArrayPref(MainActivity.this, "titles", titles);
                                    setStringArrayPref(MainActivity.this, "dates", dates);
                                    setStringArrayPref(MainActivity.this, "images", images);
                                    setStringArrayPref(MainActivity.this, "simages", simages);

                                }
                            })
                            .show();
                }
                break;
            case R.id.download:

                SaveImage(StringToBitmap(images.get(infiniteAdapter.getRealCurrentPosition())));
                ArrayList<String> noad  = getStringArrayPref(this,"noad");
                if (noad.size()==0) {
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                        mInterstitialAd.loadAd(new AdRequest.Builder().build());
                    } else {
                        mInterstitialAd.loadAd(new AdRequest.Builder().build());
                    }
                }
                showUnsupportedSnackBar();
                break;
            case R.id.menu:
                Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
                addContentView(view, new LinearLayout.LayoutParams(display.getWidth()-10, display.getHeight()*2/5));
                linearLayout.setClickable(true);
                findViewById(R.id.download).setClickable(false);
                findViewById(R.id.item_delete).setClickable(false);
                findViewById(R.id.painting).setClickable(false);
                findViewById(R.id.btn_transition_time).setClickable(false);
                findViewById(R.id.btn_smooth_scroll).setClickable(false);
                break;
            case R.id.btn_smooth_scroll:
                DiscreteScrollViewOptions.smoothScrollToUserSelectedPosition(itemPicker, v);
                break;
            case R.id.add:
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
                Intent intentphoto = new Intent(MainActivity.this, MakeLine.class);
                intentphoto.putExtra("type", 2);
                startActivity(intentphoto);
                break;
            case R.id.album:
                Intent intentalbum = new Intent(MainActivity.this, SampleActivity.class);
                intentalbum.putExtra("type",1);
                startActivity(intentalbum);
                break;
            case R.id.painting:
                if(titles.size()==1&&titles.get(0).equals("Press '+' Button")){
                    Snackbar.make(itemPicker, "ADD NEW!", Snackbar.LENGTH_SHORT).show();
                }
                else{
                    Intent topaint = new Intent(MainActivity.this, Painting.class);

                    Painting.startWithBitmap(MainActivity.this, StringToBitmap(images.get(infiniteAdapter.getRealCurrentPosition())));
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
        // 이 메소드는 구매 '성공'시에만 호출된다.
        if (productId.equals("ce_no_ad")) {
            bp.isPurchased("ce_no_ad");
            // TODO: 구매 해 주셔서 감사합니다! 메세지 보내기
            ArrayList<String> noad = new ArrayList<String>();
            noad.add("ok");
            setStringArrayPref(this,"noad",noad);
            Snackbar.make(itemPicker, "THANK YOU!", Snackbar.LENGTH_SHORT).show();
            // * 광고 제거는 1번 구매하면 영구적으로 사용하는 것이므로 consume하지 않지만,
            // 만약 게임 아이템 100개를 주는 것이라면 아래 메소드를 실행시켜 다음번에도 구매할 수 있도록 소비처리를 해줘야한다.
            // bp.consumePurchase("noad");
            Intent intent = getIntent();
            intent.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION);
            finish();
            startActivity(intent);
        }
        if (productId.equals("ce_donate")) {
            // TODO: 구매 해 주셔서 감사합니다! 메세지 보내기
            bp.isPurchased("ce_donate");
            Snackbar.make(itemPicker, "I LOVE YOU!", Snackbar.LENGTH_SHORT).show();
            // * 광고 제거는 1번 구매하면 영구적으로 사용하는 것이므로 consume하지 않지만,
            // 만약 게임 아이템 100개를 주는 것이라면 아래 메소드를 실행시켜 다음번에도 구매할 수 있도록 소비처리를 해줘야한다.
             bp.consumePurchase("ce_donate");
        }
    }

    @Override
    public void onPurchaseHistoryRestored() {
//        bp.isPurchased("noad");
    }

    @Override
    public void onBillingError(int errorCode, @Nullable Throwable error) {

        //TODO: 이런식으로 구매 오류시 오류가 발생했다고 알려주는 것도 좋다.
        if (errorCode != Constants.BILLING_RESPONSE_RESULT_USER_CANCELED) {
            Snackbar.make(itemPicker, "Billing Error!", Snackbar.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onBillingInitialized() {
        // storage에 구매여부 저장
//        storage.setPurchasedRemoveAds(bp.isPurchased("noad"));

//        ArrayList<String> noad  = getStringArrayPref(this,"noad");
//        if(noad.size()!=0){
//            Intent intent = getIntent();
//            intent.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION);
//            finish();
//            startActivity(intent);
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (bp != null) {
            bp.release();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    public void SaveImage(Bitmap bitmap) {
        String str = Environment.getExternalStorageDirectory().getAbsolutePath().toString();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(str);
        str = "/Contour_Extractor/";
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
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream);
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
        }
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

