package com.imageliner;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;

public class MyRecyclerAdapter extends Adapter<ViewHolder> {
    private final List<CardItem> mDataList;
    private MyRecyclerViewClickListener mListener;

    public interface MyRecyclerViewClickListener {
        void onItemClicked(int i);

        void onLearnMoreButtonClicked(int i);

        void onShareButtonClicked(int i);
    }

    public static class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        ImageView image;
        TextView title;

        public ViewHolder(final View view) {
            super(view);
            this.title = (TextView) view.findViewById(R.id.title_text);
            this.image = (ImageView) view.findViewById(R.id.card_image);
            this.image.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    if (ViewHolder.this.getAdapterPosition() != -1) {
                        Bitmap bitmap = ((BitmapDrawable) ViewHolder.this.image.getDrawable()).getBitmap();
                        Intent intent = new Intent(view.getContext(), save_test.class);
                        ArrayList arrayList = new ArrayList();
                        arrayList.add(MyRecyclerAdapter.BitmapToString(bitmap));
                        MyRecyclerAdapter.setStringArrayPref(view.getContext(), "pass_image", arrayList);
                        view.getContext().startActivity(intent);
                    }
                }
            });
        }
    }

    public void onItemClicked(int i) {
    }

    public void removeItem(int i) {
        this.mDataList.remove(i);
        notifyItemRemoved(i);
    }

    public void setOnClickListener(MyRecyclerViewClickListener myRecyclerViewClickListener) {
        this.mListener = myRecyclerViewClickListener;
    }

    public MyRecyclerAdapter(List<CardItem> list) {
        this.mDataList = list;
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_card, viewGroup, false));
    }

    public void onBindViewHolder(ViewHolder viewHolder, final int i) {
        CardItem cardItem = (CardItem) this.mDataList.get(i);
        viewHolder.title.setText(cardItem.getTitle());
        viewHolder.image.setImageBitmap(cardItem.getImage());
        if (this.mListener != null) {
            viewHolder.image.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    MyRecyclerAdapter.this.mListener.onItemClicked(i);
                }
            });
        }
    }

    public int getItemCount() {
        return this.mDataList.size();
    }

    public static String BitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.PNG, 70, byteArrayOutputStream);
        return Base64.encodeToString(byteArrayOutputStream.toByteArray(), 0);
    }

    public static void setStringArrayPref(Context context, String str, ArrayList<String> arrayList) {
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
}
