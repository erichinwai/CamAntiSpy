package com.example.camantispy;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
public class AppUsageAdapter extends RecyclerView.Adapter<AppUsageContent>  {

    private List<TrackerInterface> list;
    List<AppInfo> Applist;
    private Bitmap bitmap;

    public AppUsageAdapter(){
        list = new ArrayList<>();
    }

    @Override
    public AppUsageContent onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.usage_status_item, parent, false);
        return new AppUsageContent(view);
    }

    @Override
    public void onBindViewHolder(AppUsageContent holder, int position) {

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "onclick", Toast.LENGTH_LONG).show();
                /*Log.d("USA_appinfo", "onclick: "+list.get(position).getAppName()
                        +"PM:"+list.get(position).getPermission()
                        +"PackageName: "+list.get(position).getUsageStats().getPackageName()
                        +"Content: "+list.get(position).getUsageStats().describeContents()+
                        "First: "+ DateFormatTransfer.formatDateTime(v.getContext(), list.get(position).getUsageStats().getFirstTimeStamp(), DateFormatTransfer.FORMAT_SHOW_TIME | DateFormatTransfer.FORMAT_SHOW_DATE)+
                        "Last: "+DateFormatTransfer.formatDateTime(v.getContext(),list.get(position).getUsageStats().getLastTimeStamp(), DateFormatTransfer.FORMAT_SHOW_TIME | DateFormatTransfer.FORMAT_SHOW_DATE)+
                        "Used: "+list.get(position).getUsageStats().getLastTimeUsed()
                        +"IMG: "+list.get(position).getAppIcon());*/
                try {
                    Drawable appIcon = list.get(position).getAppIcon();
                    drawableToBitamp(appIcon);
                    byte[] bytes = bitmap2Bytes(bitmap);
                    Applist = new ArrayList<AppInfo>();
                    Applist.add(new AppInfo(list.get(position).getAppName(),
                            list.get(position).getPermission(),
                            list.get(position).getUsageStats().getPackageName(),
                            list.get(position).getUsageStats().describeContents(),
                            list.get(position).getUsageStats().getFirstTimeStamp(),
                            list.get(position).getUsageStats().getLastTimeStamp(),
                            list.get(position).getUsageStats().getLastTimeUsed(),
                            bytes
                    ));
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("APP_INFO", (Serializable) Applist);
                    Intent intent = new Intent(v.getContext(), AppDetail.class);
                    intent.putExtras(bundle);
                    v.getContext().startActivity(intent);
                }catch (Exception e){
                    Toast.makeText(v.getContext(), "onclick", Toast.LENGTH_LONG).show();
                }

                //holder.itemView.getContext().startActivity(intent);
            }
        });
        holder.bindTo(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setList(List<TrackerInterface> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    private void drawableToBitamp(Drawable drawable) {
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        System.out.println("Drawable2Bitmap");
        Bitmap.Config config =
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565;
        bitmap = Bitmap.createBitmap(w, h, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
    }
    private byte[] bitmap2Bytes(Bitmap bm){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }


}

