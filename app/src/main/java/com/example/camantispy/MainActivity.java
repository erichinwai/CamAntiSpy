package com.example.camantispy;

import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.preference.CheckBoxPreference;
import android.provider.SyncStateContract;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import java.util.List;
import java.util.Vector;


public class MainActivity extends AppCompatActivity {
    private Button BCamera;
    private Button BAppmon;
    private Button StartService, StopService;
    private Button BScan;
    private Button BScan2;

    private Vector<String> malware_app;
    private Vector<Drawable> malware_icon;
    //private CheckBoxPreference disableCameraCheckbox;
    private ComponentName adminComponent;
    private DevicePolicyManager dpm;
    private Switch switchCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        BAppmon = (Button) findViewById(R.id.bappmon);
        BAppmon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAppMon();
            }
        });

        StartService = (Button)findViewById(R.id.startservice);
        StopService = (Button)findViewById(R.id.stopservice);
        StartService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent serviceIntent = new Intent(MainActivity.this, CamNotification.class);
                    //startService(serviceIntent);
                    ContextCompat.startForegroundService(MainActivity.this, serviceIntent);
            }
        });
        StopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent serviceIntent = new Intent(MainActivity.this, CamNotification.class);
                stopService(serviceIntent);

            }
        });

        dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        adminComponent = new ComponentName(getPackageName(),getPackageName() + ".DisableCamera");
        Log.d("switch", "deviceAdminSample: "+adminComponent.getPackageName()+ ".DisableCamera");

        // Request device admin activation if not enabled.
        if (!dpm.isAdminActive(adminComponent)) {

            Intent activateDeviceAdmin = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            activateDeviceAdmin.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent);
            startActivityForResult(activateDeviceAdmin, 100);

        }

        switchCamera = (Switch)this.findViewById(R.id.CameraSwitch);
        switchCamera.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isEnabled) {
                try {

                    Log.d("checkCamSt", "onCheckedChanged: "+isEnabled+" "+getIntent().getStringExtra("switch2"));
                    String result = getIntent().getStringExtra("switch2");
                    if (isEnabled && result=="false" &&result!=null){
                        isEnabled = false;
                        dpm.setCameraDisabled(adminComponent, true); // Disable camera.
                        Log.d("checkCam1", "onCheckedChanged: "+isEnabled);
                    }else if (isEnabled && result==null) {
                        dpm.setCameraDisabled(adminComponent, false); // Enable camera.
                        Log.d("checkCam2", "onCheckedChanged: "+isEnabled+" "+getIntent().getStringExtra("switch2"));

                    } else{
                        isEnabled = false;
                        dpm.setCameraDisabled(adminComponent, true);
                        Log.d("checkCam3", "onCheckedChanged: "+isEnabled);

                    }
                    Log.d("checkCamStSum", "onCheckedChanged: "+isEnabled);
                } catch (SecurityException securityException) {
                    Log.i("DeviceAdm", "Error occurred while disabling/enabling camera - " + securityException.getMessage());
                }
            }
        });
        Log.d("Ttt", "onCreate: "+dpm.getCameraDisabled(adminComponent));
        if (dpm.getCameraDisabled(adminComponent)==true) {
            switchCamera.setChecked(false);
            Log.d("checkCamitem1", "onCreate: ");

        }else{
            switchCamera.setChecked(true);
            Log.d("checkCamitem3", "onCreate: "+getIntent().getStringExtra("switch"));
            //finish();
            //startActivity(getIntent());
        }

        //permission based malware detection
        BScan= (Button) findViewById(R.id.scanMal);
        BScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                malware_app = new Vector<String>();
                malware_icon = new Vector<Drawable>();
                //get all the installed apps
                final Intent resolveIntent  = new Intent(Intent.ACTION_MAIN, null);
                resolveIntent .addCategory(Intent.CATEGORY_LAUNCHER);
                final List installedAppsList = getPackageManager().queryIntentActivities(resolveIntent , 0);

                for (Object app : installedAppsList) {
                    ResolveInfo resolInfo = (ResolveInfo) app;
                    PackageInfo packInfo = null;

                    float score = 0;
                    float weight = 0;
                    float no_neutral = 0;
                    int num_of_pm=0;
                    try {
                        //get apps permission
                        packInfo = getPackageManager().getPackageInfo(resolInfo.activityInfo.packageName, PackageManager.GET_PERMISSIONS);
                        String[] permission_check = packInfo.requestedPermissions;
                        Log.d("packinfo", "onClick: "+packInfo.packageName+permission_check);
                        if (permission_check != null) {
                            //check apps with sensitive permissions
                            for (int i = 0; i < permission_check.length; i++) {
                                if (permission_check[i].equals("android.permission.MOUNT_UNMOUNT_FILESYSTEMS")||
                                        permission_check[i].equals("android.permission.ACCESS_NETWORK_STATE") ||
                                        permission_check[i].equals("android.permission.CHANGE_WIFI_STATE")||
                                        permission_check[i].equals("android.permission.INTERNET")||
                                        permission_check[i].equals("android.permission.CALL_PHONE")||
                                        permission_check[i].equals("android.permission.WIFI_MULTICAST_STATE")
                                ) {
                                    num_of_pm+=1;
                                    weight += 2;

                                } else if (
                                        permission_check[i].equals("android.permission.BLUETOOTH")||
                                                permission_check[i].equals("android.permission.BLUETOOTH_ADMIN")||
                                        permission_check[i].equals("android.permission.ACCOUNT_MANAGER") ||
                                        permission_check[i].equals("android.permission.WRITE_HISTORY_BOOKMARKS") ||
                                        permission_check[i].equals("android.permission.READ_PHONE_STATE") ||
                                        permission_check[i].equals("android.permission.GET_ACCOUNTS") ||
                                        permission_check[i].equals("android.permission.RECORD_AUDIO") ||
                                                permission_check[i].equals("android.permission.RECORDER_TASKS")||
                                                permission_check[i].equals("android.permission.WRITE_SOCIAL_STREAM")||
                                                permission_check[i].equals("android.permission.WRITE_SYNC_SETTINGS")||
                                        permission_check[i].equals("android.permission.SYSTEM_ALERT_WINDOW")||
                                                permission_check[i].equals("android.permission.CLEAR_APP_CACHE")
                                ) {
                                    num_of_pm+=1;
                                    weight += 3;

                                } else if (permission_check[i].equals("android.permission.READ_HISTORY_BOOKMARKS") ||
                                        permission_check[i].equals("android.permission.READ_CALENDAR")||
                                        permission_check[i].equals("android.permission.CAMERA") ||
                                        permission_check[i].equals("android.permission.WRITE_CALENDAR")||
                                        permission_check[i].equals("android.permission.RECEIVE_BOOT_COMPLETED")||
                                        permission_check[i].equals("android.permission.VIBRATE")||
                                        permission_check[i].equals("android.permission.WAKE_LOCK")||
                                        permission_check[i].equals("android.permission.WRITE_CONTACTS")||
                                        permission_check[i].equals("android.permission.WRITE_APN_SETTINGS")||
                                        permission_check[i].equals("android.permission.KILL_BACKGROUND_PROCESSES")||
                                        permission_check[i].equals("android.permission.AUTHENTICATE_ACCOUNTS")
                                ){
                                    num_of_pm+=1;
                                    weight += 4;
                                } else if (permission_check[i].equals("android.permission.SEND_SMS") ||
                                        permission_check[i].equals("android.permission.RECEIVE_SMS") ||
                                        permission_check[i].equals("android.permission.WRITE_SMS")||
                                        permission_check[i].equals("android.permission.RECEIVE_MMS") ||
                                        permission_check[i].equals("android.permission.READ_EXTERNAL_STORAGE")||
                                        permission_check[i].equals("android.permission.READ_CONTACTS")

                                ) {
                                    num_of_pm+=1;
                                    weight += 5;
                                } else if (permission_check[i].equals("android.permission.WRITE_EXTERNAL_STORAGE")||
                                        permission_check[i].equals("android.permission.ACCESS_BACKGROUND_LOCATION") ||
                                        permission_check[i].equals("android.permission.PROCESS_OUTGOING_CALLS") ||
                                        permission_check[i].equals("android.permission.READ_CALL_LOG") ||
                                        permission_check[i].equals("android.permission.READ_SMS") ||
                                        permission_check[i].equals("android.permission.RECEIVE_WAP_PUSH")||
                                        permission_check[i].equals("android.permission.WRITE_CALL_LOGS") ||
                                        permission_check[i].equals("android.permission.READ_LOGS") ||//read the low-level system log files.
                                        permission_check[i].equals("android.permission.CHANGE_CONFIGURATION") ||//Allows an application to modify the current configuration
                                        permission_check[i].equals("android.permission.ACCESS_FINE_LOCATION") ||
                                        permission_check[i].equals("android.permission.ACCESS_COARSE_LOCATION")||
                                        permission_check[i].equals("android.permission.INSTALL_PACKAGES")

                                ) {
                                    num_of_pm+=1;
                                    weight += 6;
                                } else {
                                    no_neutral++;
                                }
                            }
                            score = weight / weight + no_neutral;
                        }
                        Log.d("APPSCORE", "SCORE:"+resolInfo.activityInfo.packageName + " = " + score);

                        //assume apps with more than 0.5 score are malware
                        if (score >= 0.5) {
                            if(resolInfo.activityInfo.packageName.equals("com.example.camantispy")  || resolInfo.activityInfo.packageName.contains("com.android.") || resolInfo.activityInfo.packageName.contains("com.vphone.")|| resolInfo.activityInfo.packageName.contains("com.google.")){
                                Log.d("malResult", resolInfo.activityInfo.packageName+ " = " + score);
                            }else{
                                malware_app.add(resolInfo.activityInfo.packageName);
                                Log.d("malwarescanResult", "score:  "+resolInfo.activityInfo.packageName+ " = " + score);
                                Drawable icon = getPackageManager().getApplicationIcon(resolInfo.activityInfo.packageName);
                                malware_icon.add(icon);
                            }

                        }
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
                //if malware are detected
                if(malware_app.size() >= 1){
                    Bundle b=new Bundle();
                    String[] malware = malware_app.toArray(new String[malware_app.size()]);
                    Intent intent_malwarelist = new Intent(MainActivity.this, ScanAppResult.class);
                    intent_malwarelist.putExtra("malsize", malware_app.size());
                    b.putStringArray("mallist", malware);
                    intent_malwarelist.putExtras(b);
                    finish();
                    startActivity(intent_malwarelist);
                }else{
                    //if no malware detected
                    Toast.makeText(getApplicationContext(), "Complete! No Malware.", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    public void openAppMon(){
        Intent intent = new Intent(this, AppUsage.class);
        startActivity(intent);
    }
}