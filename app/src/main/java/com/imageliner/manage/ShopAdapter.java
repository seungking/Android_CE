package com.imageliner.manage;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.imageliner.models.Item;
import com.imageliner.R;
import com.imageliner.activities.ShopActivity;
import com.imageliner.activities.ViewImage;

import java.util.List;

public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ViewHolder> {

    private List<Item> data;
    int p = 0;

    public ShopAdapter(List<Item> data) {
        this.data = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shop_card, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.image.setImageBitmap(data.get(position).getSimage());
    }

    public void removeItem(int i) {
        this.data.remove(i);
        notifyItemRemoved(i);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView image;


        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
            this.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent viewimage = new Intent(v.getContext(), ViewImage.class);
                    viewimage.putExtra("position", ShopActivity.getCurrentIndex());
                    v.getContext().startActivity(viewimage);
                }
            });
        }
    }

}
