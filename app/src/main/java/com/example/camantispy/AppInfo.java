package com.example.camantispy;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import java.io.Serializable;

public class AppInfo implements Serializable {
    private String AppName;
    private StringBuilder Permission;
    private String PackageName;
    private long FirstTime;
    private long LastTime;
    private long LastTimeUsed;
    private int DescContents;
    private byte[] AppIcon;
    private boolean IsSuspicious;

    public AppInfo(String AppName, StringBuilder Permission, String PackageName, int DescContents, long FirstTime, long LastTime, long LastTimeUsed, byte[] AppIcon){
        this.AppName = AppName;
        this.Permission = Permission;
        this.PackageName = PackageName;
        this.FirstTime = FirstTime;
        this.LastTime = LastTime;
        this.LastTimeUsed = LastTimeUsed;
        this.DescContents = DescContents;
        this.AppIcon = AppIcon;

    }

    public byte[] getAppIcon() {
        return AppIcon;
    }

    public String getAppName() {
        return AppName;
    }

    public StringBuilder getPermission() {
        return Permission;
    }

    public long getFirstTime() {
        return FirstTime;
    }

    public long getLastTime() {
        return LastTime;
    }

    public long getLastTimeUsed() {
        return LastTimeUsed;
    }

    public String getPackageName() {
        return PackageName;
    }

    public int getDescContents() {
        return DescContents;
    }

    public boolean isSuspicious() {
        return IsSuspicious;
    }

    public void setSuspicious(boolean suspicious) {
        IsSuspicious = suspicious;
    }

}
