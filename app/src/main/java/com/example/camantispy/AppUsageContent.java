package com.example.camantispy;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashSet;

public class AppUsageContent extends RecyclerView.ViewHolder {

    private ImageView appIcon;
    private TextView appName;
    private TextView lastTimeUsed;

    public AppUsageContent(View itemView) {
        super(itemView);

        appIcon = (ImageView) itemView.findViewById(R.id.icon);
        appName = (TextView) itemView.findViewById(R.id.title);
        lastTimeUsed = (TextView) itemView.findViewById(R.id.last_used);

    }

    public void bindTo(TrackerInterface trackerInterface) {
        appIcon.setImageDrawable(trackerInterface.getAppIcon());
        appName.setText(trackerInterface.getAppName());
        String[] permissions = trackerInterface.getPermission().toString().split("\\n");

        Log.d("VH_app_name", trackerInterface.getAppName());
        if (trackerInterface.getUsageStats() == null){
            //check the apps is it used before
            Log.d("VH_never_use", String.valueOf(trackerInterface.getAppName()));
            lastTimeUsed.setText(R.string.last_time_used_never);
        }else if (trackerInterface.getUsageStats().getLastTimeUsed() == 0L){
            lastTimeUsed.setText(R.string.last_time_used_never);
        } else{
            Log.d("VH_time", DateFormatTransfer.format(trackerInterface));
            lastTimeUsed.setText(App.getApp().getString(R.string.last_time_used,
                    DateFormatTransfer.format(trackerInterface)));
        }
    }
}
