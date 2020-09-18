package com.imageliner.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.Constants;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.Task;
import com.imageliner.MainActivity;
import com.imageliner.MainActivity_home;
import com.imageliner.OnBoardActivity;
import com.imageliner.OnBoardActivity2;
import com.imageliner.R;
import com.imageliner.models.AppStorage;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class SettingFragment extends Fragment implements BillingProcessor.IBillingHandler {

    private View view;
    private BillingProcessor bp;

    ReviewManager manager;
    com.google.android.play.core.tasks.Task<ReviewInfo> request;
    ReviewInfo reviewInfo;

    public SettingFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_setting,container,false);
        init();
        return view;
    }

    private void init(){

        bp = new BillingProcessor(getContext(), "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlzZvyf8fo8yzDEVD8xa3sfRBWgpfT9OuuXjhR+6efl/qDKKphqYFCOLOjYrkhG2bWGMBMLtN47szIEafw9tzJy9h57KZRkoRo8ZPDUfG1zG7UEstWh0MsVWTrf8Gd0H4ob5cmNqc/pGTSGeGjU+gtTUiAC2+8W9v5QroBp6W5UEMfp7m7GvTc+LocFCIQV3t43bResun/+p/SwuIOkYzF28uBicwxoLcltAHTY8Cn82kT7KMAex+JPgcESMw/hNUCTSaNLIMiWeIH2WSX1k2jmAE7uaM6Ygys+UrcmXxbaMUC5egnAkt7NOnsDtsqj9hgqj9KPyG6dBdLjpVKLIkUwIDAQAB", this);

        view.findViewById(R.id.cat_review).setOnClickListener(v->{
//
//            manager = ReviewManagerFactory.create(getContext());
//            request = manager.requestReviewFlow();
//            request.addOnCompleteListener(task -> {
//                if (task.isSuccessful()) {
//                    // We can get the ReviewInfo object
//                    reviewInfo = task.getResult();
//                } else {
//                    Toasty.info(getContext(), "Review Error", Toast.LENGTH_SHORT, true).show();
//                }
//            });
//
//            Task<Void> flow = manager.launchReviewFlow(getActivity(), reviewInfo);
//            flow.addOnCompleteListener(task -> {
//                Toasty.info(getContext(), "Thank you!", Toast.LENGTH_SHORT, true).show();
//            });

        });

        view.findViewById(R.id.cat_donation).setOnClickListener(v->{
            bp.purchase((Activity) getContext(), "ce_donate");
        });

        view.findViewById(R.id.cat_howto).setOnClickListener(v->{
            startActivity(new Intent(getContext(), OnBoardActivity2.class));
        });

        view.findViewById(R.id.cat_feedback).setOnClickListener(v->{
            Intent email = new Intent(Intent.ACTION_SEND);
            email.setType("plain/text");
            String[] address = {"ahnseungkl@gmail.com"};
            email.putExtra(Intent.EXTRA_EMAIL, address);
            email.putExtra(Intent.EXTRA_SUBJECT, "[FROM Contour Extractor]");
            startActivity(email);
        });
    }


    @Override
    public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
        if (productId.equals("ce_donate")) {
            // TODO: 구매 해 주셔서 감사합니다! 메세지 보내기
            bp.isPurchased("ce_donate");
            Toasty.success(getContext(), "Thank you!", Toast.LENGTH_SHORT, true).show();
            bp.consumePurchase("ce_donate");
        }
    }

    @Override
    public void onPurchaseHistoryRestored() {
    }

    @Override
    public void onBillingError(int errorCode, @Nullable Throwable error) {
        if (errorCode != Constants.BILLING_RESPONSE_RESULT_USER_CANCELED) {
            Toasty.info(getContext(), "Billing Error.", Toast.LENGTH_SHORT, true).show();
        }
    }

    @Override
    public void onBillingInitialized() {
    }
}