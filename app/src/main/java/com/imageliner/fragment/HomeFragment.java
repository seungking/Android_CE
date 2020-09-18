package com.imageliner.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.codingending.popuplayout.PopupLayout;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.snackbar.Snackbar;
import com.imageliner.MainActivity;
import com.imageliner.MainActivity_home;
import com.imageliner.MakeLine;
import com.imageliner.Painting;
import com.imageliner.R;
import com.imageliner.SampleActivity;
import com.imageliner.adapter.ShopAdapter;
import com.imageliner.dialog.SweetAlertDialog;
import com.imageliner.manage.ManagePref;
import com.imageliner.manage.functions;
import com.imageliner.models.Item;
import com.vansuita.materialabout.builder.AboutBuilder;
import com.vansuita.materialabout.views.AboutView;
import com.yarolegovich.discretescrollview.DSVOrientation;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.InfiniteScrollAdapter;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class HomeFragment extends Fragment implements DiscreteScrollView.OnItemChangedListener,
        View.OnClickListener {

    private View view;

    private ArrayList<Item> data = new ArrayList<Item>();
    ArrayList<String> check = new ArrayList<String>();
    ArrayList<String> titles = new ArrayList<String>();
    ArrayList<String> dates = new ArrayList<String>();
    ArrayList<String> images = new ArrayList<String>();
    ArrayList<String> simages = new ArrayList<String>();

    private InterstitialAd mInterstitialAd;

    TextView currentItemName;
    TextView currentItemPrice;
    DiscreteScrollView itemPicker;
    static InfiniteScrollAdapter infiniteAdapter;
    ShopAdapter shopAdapter;

    public HomeFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        v = inflater.inflate(R.layout.layout_home,container,false);

        view = inflater.inflate(R.layout.layout_home,container,false);

        mInterstitialAd = new InterstitialAd(getContext());
        mInterstitialAd.setAdUnitId("ca-app-pub-1992325656759505/1090443323");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        //리스트
        itemPicker = (DiscreteScrollView) view.findViewById(R.id.item_picker);
        itemPicker.setOrientation(DSVOrientation.HORIZONTAL);
        itemPicker.addOnItemChangedListener(this);

        //리스트 정보
        currentItemName = (TextView) view.findViewById(R.id.item_name);
        currentItemPrice = (TextView) view.findViewById(R.id.item_price);

        //추가
        view.findViewById(R.id.add).setOnClickListener(v->{
            View parent=View.inflate(getContext(),R.layout.layout_bottom_menu,null);
            final PopupLayout popupLayout=PopupLayout.init(getContext(),parent);
            final View.OnClickListener clickListener=new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Toast.makeText(getContext(),((Button)v).getText(),Toast.LENGTH_SHORT).show();
                    popupLayout.dismiss();
                }
            };
            parent.findViewById(R.id.menu_1).setOnClickListener(view->{
                Intent intentphoto = new Intent(getContext(), MakeLine.class);
                intentphoto.putExtra("type", 2);
                popupLayout.dismiss();
                startActivity(intentphoto);
            });
            parent.findViewById(R.id.menu_2).setOnClickListener(view->{
                Intent intentalbum = new Intent(getContext(), SampleActivity.class);
                intentalbum.putExtra("type",1);
                popupLayout.dismiss();
                startActivity(intentalbum);
            });
            popupLayout.show(PopupLayout.POSITION_BOTTOM);

        });

        //그림
        view.findViewById(R.id.painting).setOnClickListener(v->{
            if(data.size()>0)
                Painting.startWithBitmap(getContext(), new functions().StringToBitmap(images.get(infiniteAdapter.getRealCurrentPosition())));
        });

        //다운로드
        view.findViewById(R.id.download).setOnClickListener(v->{
            if(data.size()>0) {
                SaveImage(new functions().StringToBitmap(images.get(infiniteAdapter.getRealCurrentPosition())));
                ArrayList<String> noad = new ManagePref().getStringArrayPref(getContext(), "noad");
                if (noad.size() == 0) {
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                        mInterstitialAd.loadAd(new AdRequest.Builder().build());
                    } else {
                        mInterstitialAd.loadAd(new AdRequest.Builder().build());
                    }
                }
            }
        });

        //삭제
        view.findViewById(R.id.item_delete).setOnClickListener(v->{
            if(data.size()>0) {
                new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
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

                                infiniteAdapter.notifyItemRemoved(infiniteAdapter.getRealCurrentPosition());
                                infiniteAdapter.notifyDataSetChanged();
                                sweetAlertDialog.dismiss();

                                titles.remove(infiniteAdapter.getRealCurrentPosition());
                                dates.remove(infiniteAdapter.getRealCurrentPosition());
                                images.remove(infiniteAdapter.getRealCurrentPosition());
                                simages.remove(infiniteAdapter.getRealCurrentPosition());
                                data.remove(infiniteAdapter.getRealCurrentPosition());

                                new ManagePref().setStringArrayPref(getContext(), "titles", titles);
                                new ManagePref().setStringArrayPref(getContext(), "dates", dates);
                                new ManagePref().setStringArrayPref(getContext(), "images", images);
                                new ManagePref().setStringArrayPref(getContext(), "simages", simages);

                                if(titles.size()==0){
                                    currentItemName.setVisibility(View.INVISIBLE);
                                    currentItemPrice.setVisibility(View.INVISIBLE);
                                    view.findViewById(R.id.painting).setVisibility(View.INVISIBLE);
                                    view.findViewById(R.id.download).setVisibility(View.INVISIBLE);
                                    view.findViewById(R.id.item_delete).setVisibility(View.INVISIBLE);
                                    view.findViewById(R.id.add_new).setVisibility(View.VISIBLE);
                                }

                            }
                        })
                        .show();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        initDataset();
    }

    public void initDataset() {

        ManagePref managePref = new ManagePref();

        data = new ArrayList<Item>();
        check = managePref.getStringArrayPref(getContext(),"check");
        titles = managePref.getStringArrayPref(getContext(),"titles");
        dates = managePref.getStringArrayPref(getContext(),"dates");
        images = managePref.getStringArrayPref(getContext(),"images");
        simages = managePref.getStringArrayPref(getContext(),"simages");
        if(check.size()==0){

            check.add("started");
            titles.add("Welcome");
            dates.add("Make Your Image!");
            images.add(new functions().BitmapToString(BitmapFactory.decodeResource(getResources(), R.drawable.logo_ce)));
            simages.add(new functions().BitmapToString(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.logo_ce), (int) BitmapFactory.decodeResource(getResources(), R.drawable.logo_ce).getWidth()/2, (int) BitmapFactory.decodeResource(getResources(), R.drawable.logo_ce).getHeight()/2, true)));

            managePref.setStringArrayPref(getContext(),"check",check);
            managePref.setStringArrayPref(getContext(),"titles",titles);
            managePref.setStringArrayPref(getContext(),"dates",dates);
            managePref.setStringArrayPref(getContext(),"images",images);
            managePref.setStringArrayPref(getContext(),"simages",simages);

            data.add(new Item( "Welcome", "Make Your Image!", BitmapFactory.decodeResource(getResources(), R.drawable.logo_ce), Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.logo_ce), (int) BitmapFactory.decodeResource(getResources(), R.drawable.logo_ce).getWidth()/2, (int) BitmapFactory.decodeResource(getResources(), R.drawable.logo_ce).getHeight()/2, true)));
        }
        else{
            for(int i=0; i<titles.size(); i++) {
                data.add(new Item(titles.get(i), dates.get(i), new functions().StringToBitmap(images.get(i)), new functions().StringToBitmap(simages.get(i))));
            }
        }

        if(titles.size()==0){
            currentItemName.setVisibility(View.INVISIBLE);
            currentItemPrice.setVisibility(View.INVISIBLE);
            view.findViewById(R.id.painting).setVisibility(View.INVISIBLE);
            view.findViewById(R.id.download).setVisibility(View.INVISIBLE);
            view.findViewById(R.id.item_delete).setVisibility(View.INVISIBLE);
            view.findViewById(R.id.add_new).setVisibility(View.VISIBLE);
        }
        else{
            currentItemName.setVisibility(View.VISIBLE);
            currentItemPrice.setVisibility(View.VISIBLE);
            view.findViewById(R.id.painting).setVisibility(View.VISIBLE);
            view.findViewById(R.id.download).setVisibility(View.VISIBLE);
            view.findViewById(R.id.item_delete).setVisibility(View.VISIBLE);
            view.findViewById(R.id.add_new).setVisibility(View.INVISIBLE);
        }

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

    }

    @Override
    public void onClick(View v) {
        
    }

    private void onItemChanged(Item item) {
        currentItemName.setText(item.getTitle());
        currentItemPrice.setText(item.getDate());
    }


    @Override
    public void onCurrentItemChanged(@Nullable RecyclerView.ViewHolder viewHolder, int i) {
        if(data.size()>0) {
            int positionInDataSet = infiniteAdapter.getRealPosition(i);
            onItemChanged(data.get(positionInDataSet));
        }
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
        getContext().sendBroadcast(new Intent(str2, Uri.parse(stringBuilder3.toString())));
        getContext().getApplicationContext().sendBroadcast(new Intent(str2, Uri.fromFile(file2)));
    }


}
