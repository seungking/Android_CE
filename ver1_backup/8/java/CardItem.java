package com.imageliner;

import android.graphics.Bitmap;

public class CardItem {
    private Bitmap image;
    private String title;

    public CardItem(String str, Bitmap bitmap) {
        this.title = str;
        this.image = bitmap;
    }

    public Bitmap getImage() {
        return this.image;
    }

    public void setImage(Bitmap bitmap) {
        this.image = bitmap;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String str) {
        this.title = str;
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer("CardItem{");
        stringBuffer.append("title='");
        stringBuffer.append(this.title);
        stringBuffer.append('\'');
        stringBuffer.append('}');
        return stringBuffer.toString();
    }
}
