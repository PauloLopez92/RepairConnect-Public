package com.singlesoft.repaircon.models;

import android.graphics.Bitmap;

public class BitmapStringItem {
    private Bitmap bitmap;
    private String name;

    private String serviceId;

    public BitmapStringItem(Bitmap bitmap, String name,String serviceId) {
        this.bitmap = bitmap;
        this.name = name;
        this.serviceId = serviceId;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }
}
