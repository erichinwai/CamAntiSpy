package com.example.camantispy;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import static com.example.camantispy.MyService.CHANNEL_ID;
public class CamNotification extends Service {

    static final String TAG = "CCam";
    CameraManager mCameraManager;
    String[] mCameraIDsList;
    CameraDevice mCameraDevice;
    CameraManager.AvailabilityCallback cameraAvailableCB;
    private String CamStat = "start";
    private AlertDialog alertDialog;
    public int notifyid=1;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.mCameraManager = (CameraManager) this.getSystemService(Context.CAMERA_SERVICE);
            try {
                mCameraIDsList = this.mCameraManager.getCameraIdList();
                for (String id : mCameraIDsList) {
                    Log.d(TAG, "Camera ID: " + id);
                }
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notification.Builder builder = new Notification.Builder(this.getApplicationContext());
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder1=new NotificationCompat.Builder(this);
        cameraAvailableCB = new CameraManager.AvailabilityCallback() {
            @Override
            public void onCameraAvailable(String cameraId) {
                super.onCameraAvailable(cameraId);
                CamStat = "available";
                Log.d("CACB_A", "Camera is available"+flags+"ID:"+startId);
                Toast.makeText(getApplicationContext(), "Camera Available", Toast.LENGTH_SHORT).show();
                builder.setContentTitle("Camera Status")
                        .setSmallIcon(R.drawable.notification)
                        .setContentText(CamStat)
                        .setDefaults(Notification.DEFAULT_ALL)
                        ;
                Notification notification = builder.build();
                notification.defaults = Notification.DEFAULT_SOUND;
                startForeground(1, notification);
            }

            @Override
            public void onCameraUnavailable(String cameraId) {
                super.onCameraUnavailable(cameraId);
                CamStat = "not available";
                Log.d("CACB_U", "Camera is not available"+flags+"ID:"
                        +startId+"Context:"+getApplicationContext().getPackageName()
                );

                Toast.makeText(getApplicationContext(), "Camera is running", Toast.LENGTH_SHORT).show();
                builder.setContentTitle("Camera Status")
                        .setSmallIcon(R.drawable.notification)
                        .setContentText(CamStat)
                        .setDefaults(Notification.DEFAULT_ALL)
                        ;

                Notification notification = builder.build();
                notification.defaults = Notification.DEFAULT_SOUND;
                startForeground(1, notification);


                /*ActivityManager activityManager=(ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                //String runningActivity=activityManager.getRunningTasks(1).get(0).topActivity.getClassName();
                List<ActivityManager.RunningTaskInfo> taskInfo = activityManager.getRunningTasks(1);
                for (int i = 0;i<taskInfo.size();i++) {
                    Log.d("appTest", "apptest: " + taskInfo.get(i).topActivity.getClassName());
                }
*/
                String currentApp = "NULL";
                if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    UsageStatsManager usm = (UsageStatsManager)getSystemService("usagestats");
                    long time = System.currentTimeMillis();
                    List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,  time - 1000*1000, time);
                    if (appList != null && appList.size() > 0) {
                        SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                        for (UsageStats usageStats : appList) {
                            mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                        }
                        if (mySortedMap != null && !mySortedMap.isEmpty()) {
                            currentApp = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                        }
                    }
                } else {
                    ActivityManager am = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
                    List<ActivityManager.RunningAppProcessInfo> tasks = am.getRunningAppProcesses();
                    currentApp = tasks.get(0).processName;
                }

                Log.d("currentTASK", "onCameraUnavailable: "+currentApp);
                //Log.d("kkk", taskInfo.get(1).topActivity.getClassName().toString());
                builder1.setContentTitle("Details")
                        .setSmallIcon(R.drawable.notification)
                        .setDefaults(Notification.DEFAULT_ALL).setAutoCancel(false)
                        //.setContentText(taskInfo.get(0).topActivity.getClassName().toString())
                        .setContentText(currentApp)
                        .setGroup("detail")
                        .setWhen(System.currentTimeMillis());

                Notification notification1 = builder1.build();
                notification1.defaults = Notification.DEFAULT_SOUND;
                notificationManager.notify(notifyid++, notification1);

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                dialog.dismiss();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                Intent back = new Intent(getApplicationContext(), MainActivity.class);
                                back.putExtra("switch",  "false");
                                back.putExtra("switch2", "false");
                                back.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(back);
                                dialog.dismiss();

                                break;
                        }
                    }
                };

                //build alertDialog to warn user
                /*alertDialog = new AlertDialog.Builder(getApplicationContext(), R.style.Theme_AppCompat_DayNight_Dialog_Alert)
                        .setTitle("Warning")
                        .setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener)
                        .create();
                alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                alertDialog.show();
*/
                // Vibrate 500 milliseconds
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    v.vibrate(500);
                }


                }
        };

        this.mCameraManager.registerAvailabilityCallback(cameraAvailableCB, new Handler());

        return START_NOT_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //this.mCameraManager.registerAvailabilityCallback(cameraAvailableCB, new Handler());
        Log.d("Dead", "onDestroy");
        try {
            if (mCameraDevice != null) {
                //alertDialog.cancel();
                this.mCameraManager.unregisterAvailabilityCallback(cameraAvailableCB);
                stopForeground(true);
                stopSelf();
                mCameraDevice.close();
                mCameraDevice = null;


            }
        } catch (final IllegalStateException e2) {
            e2.printStackTrace();
        }

    }




}
