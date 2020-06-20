package com.example.camantispy;

import android.app.usage.UsageStats;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.Log;

public final class TrackerInterface implements Comparable<TrackerInterface> {

    private final UsageStats usageStats;
    private final Drawable appIcon;
    private final String appName;
    private final StringBuilder permission ;

    public TrackerInterface(UsageStats usageStats, Drawable appIcon, String appName, StringBuilder permission) {
        this.usageStats = usageStats;
        this.appIcon = appIcon;
        this.appName = appName;
        this.permission = permission;
    }

    public UsageStats getUsageStats() {
        return usageStats;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public String getAppName() {
        return appName;
    }

    public StringBuilder getPermission() {
        return permission;
    }

    @Override
    public int compareTo(@NonNull TrackerInterface trackerInterface) {
        Log.d("compare", "compareTo: "+trackerInterface);
        if (usageStats == null && trackerInterface.getUsageStats() != null) {
            return 1;
        } else if (trackerInterface.getUsageStats() == null && usageStats != null) {
            return -1;
        } else if (trackerInterface.getUsageStats() == null && usageStats == null) {
            return 0;
        } else {
            return Long.compare(trackerInterface.getUsageStats().getLastTimeUsed(),
                    usageStats.getLastTimeUsed());
        }
    }
}
