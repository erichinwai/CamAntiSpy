package com.example.camantispy;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class AppDetail extends Activity {

    private TextView App_name;
    private ImageView App_icon;
    private TextView Package_name;
    private TextView Last_ac;
    private TextView First_ac;
    private ListView Permission;
    List<AppInfo> appInfoList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_detail);
        appInfoList =  (List<AppInfo>) getIntent().getSerializableExtra("APP_INFO");

        Log.d("Test4", String.valueOf(appInfoList));

        String apName = appInfoList.get(0).getAppName();
        String pgName = appInfoList.get(0).getPackageName();
        long firstTime = appInfoList.get(0).getFirstTime();
        long lastTime = appInfoList.get(0).getLastTimeUsed();
        byte[] appIcons = appInfoList.get(0).getAppIcon();
        Bitmap bitmap= BitmapFactory.decodeByteArray(appIcons,0,appIcons.length);
        StringBuilder pm = appInfoList.get(0).getPermission();
        App_name = (TextView)findViewById(R.id.app_name);
        App_name.setText(apName);
        App_icon = (ImageView)findViewById(R.id.app_img);
        App_icon.setImageBitmap(bitmap);
        Package_name = (TextView)findViewById(R.id.package_name);
        Package_name.setText(getString(R.string.package_name,pgName));
        DateFormat format = new SimpleDateFormat("HH:mm:ss , yyyy-MM-dd", Locale.getDefault());
        Last_ac = (TextView)findViewById(R.id.last_access_time);
        Last_ac.setText(getString(R.string.last_time_used, format.format(lastTime)));
        First_ac = (TextView)findViewById(R.id.first_install_time);
        First_ac.setText(getString(R.string.first_time_used, format.format(firstTime)));
        String[] pmlist = pm.toString().split("\\n");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(AppDetail.this, android.R.layout.simple_list_item_activated_1, pmlist);
        Permission = (ListView)findViewById(R.id.permission_list);
        Permission.setAdapter(adapter);

    }
}
