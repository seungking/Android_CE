package com.imageliner;

import android.graphics.Bitmap;

public class Item {

//    private final int id;
    private final String title;
    private final String date;
    private final Bitmap image;
    private final Bitmap simage;

    public Item(String name, String price, Bitmap image, Bitmap simage) {
//        this.id = id;
        this.title = name;
        this.date = price;
        this.image = image;
        this.simage = simage;
    }


    public String getTitle() { return title; }

    public String getDate() {
        return date;
    }

    public Bitmap getImage() {
        return image;
    }

    public Bitmap getSimage() {
        return simage;
    }
}
