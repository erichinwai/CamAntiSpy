package com.example.camantispy;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static android.app.AppOpsManager.MODE_ALLOWED;
import static android.app.AppOpsManager.OPSTR_GET_USAGE_STATS;
import static android.os.Process.myUid;

public class AppUsagePresenter implements AppUsageInterface.Presenter {

    private static final int flags = PackageManager.GET_META_DATA |
            PackageManager.GET_SHARED_LIBRARY_FILES |
            PackageManager.GET_UNINSTALLED_PACKAGES
            ;
    private UsageStatsManager usageStatsManager;
    private PackageManager packageManager;
    private AppUsageInterface.View view;
    private final Context context;

    public AppUsagePresenter(Context context, AppUsageInterface.View view) {
        usageStatsManager = (UsageStatsManager) context.getSystemService("usagestats");
        packageManager = context.getPackageManager();
        this.view = view;
        this.context = context;
    }

    private long getStartTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -1);
        return calendar.getTimeInMillis();
    }

    private boolean checkForPermission(Context context) {
        //Log.d("UP_INFO", String.valueOf(context.checkPermission("android.permission.CAMERA", myPid(), myUid())));
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        //int mode = appOps.checkOpNoThrow(OPSTR_CAMERA, myUid(), context.getPackageName());
        int mode = appOps.checkOpNoThrow(OPSTR_GET_USAGE_STATS, myUid(), context.getPackageName());

        if (mode == AppOpsManager.MODE_ALLOWED){
            Log.d("UP_CHECK", "OK");
        }else if (mode == AppOpsManager.MODE_IGNORED){
            Log.d("UP_CHECK", "BAN");
        }else if (mode == AppOpsManager.MODE_ERRORED){
            Log.d("UP_CHECK", "ERROR");
        }else if (mode == 4){
            Log.d("UP_CHECK", "ASK");
        }
        Log.d("UP_C", String.valueOf(PackageManager.PERMISSION_GRANTED));

        return mode == MODE_ALLOWED;
    }

    private List<String> getInstalledAppList(){
        List<ApplicationInfo> appinfos = packageManager.getInstalledApplications(flags);
        List<String> installed_applist = new ArrayList<>();
        for (ApplicationInfo info : appinfos){
            Log.d("UP_InstalledAppList", info.packageName);
            Log.d("UP_AppPermission", info.permission == null ? "" : info.permission);

            installed_applist.add(info.packageName);
        }
        return installed_applist;
    }

    private List<TrackerInterface> searchAllCamPMApp(List<String> packageNames, List<UsageStats> usageStatses) {
        List<TrackerInterface> list = new ArrayList<>();
        for (String name : packageNames) {
            //Log.d("US_pn_name", name);
            boolean added = false;
            boolean cam = false;
            for (UsageStats stat : usageStatses) {
                if (name.equals(stat.getPackageName())) {
                    added = true;
                    //Log.d("US_stat_getName", "this_app_name"+stat.getPackageName());
                    //Log.d("US_stat", "this_app_stats"+fromUsageStat(stat).getUsageStats().toString());
                    //Log.d("US_name1", fromUsageStat(stat).getAppName());
                    //Log.d("US_permission1", fromUsageStat(stat).getPermission().toString());
                    String[] pmlists = fromUsageStat(stat).getPermission().toString().split("\\n");
                    for (String pmlist: pmlists){
                        //Log.d("Split2 : ", "Name: "+fromUsageStat(stat).getAppName()+"PM: "+pmlist);
                        if (pmlist.equals("android.permission.CAMERA")){
                            cam = true;
                        }
                    }
                    //Log.d("Split1 :", "name:"+fromUsageStat(stat).getAppName()+"pmlists:  "+pmlists);
                    if (cam==true) {
                        list.add(fromUsageStat(stat));
                    }
                    //list.add(fromUsageStat(stat));
                }
            }
            if (!added) {
                //Log.d("US_name2", String.valueOf(fromUsageStat(name).getAppName()));
                //Log.d("US_permission2", fromUsageStat(name).getPermission().toString() == null ? "" : fromUsageStat(name).getPermission().toString());
                String[] pmlists = fromUsageStat(name).getPermission().toString().split("\\n");
                for (String pmlist: pmlists){
                    if (pmlist.equals("android.permission.CAMERA")){
                        cam = true;
                    }
                }
                if (cam==true) {
                    list.add(fromUsageStat(name));
                }
                //list.add(fromUsageStat(name));
            }
        }
        //collect all the application status info and store in a list
        Collections.sort(list);
        return list;
    }

    private TrackerInterface fromUsageStat(String packageName) throws IllegalArgumentException {
        try {
            ApplicationInfo ai = packageManager.getApplicationInfo(packageName, 0);
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
            String[] requestedPermissions = packageInfo.requestedPermissions;
            StringBuilder permissions = new StringBuilder("");
            //Log.d("UP_PMs_PM_name", "AppName:"+packageName);
            if (requestedPermissions!=null){
                for (int i=0; i<requestedPermissions.length; i++){
                    //Log.d("UP_PMs_PM_n1", "name:"+packageName+"\n"+"pm:"+requestedPermissions[i]);
                    permissions.append(requestedPermissions[i]+"\n");
                }

            }

            //Log.d("fk", packageName+String.valueOf(CheckAppSuspicious(requestedPermissions)));
            //Log.d("SUM1", "Appname: "+packageName+"\n"+"PMs:"+permissions);
            //return 4 time
            return new TrackerInterface(null, packageManager.getApplicationIcon(ai), packageManager.getApplicationLabel(ai).toString(), permissions);

        } catch (PackageManager.NameNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private TrackerInterface fromUsageStat(UsageStats usageStats) throws IllegalArgumentException {
        try {
            ApplicationInfo ai = packageManager.getApplicationInfo(usageStats.getPackageName(), 0);
            PackageInfo packageInfo = packageManager.getPackageInfo(usageStats.getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] requestedPermissions = packageInfo.requestedPermissions;
            StringBuilder permissions = new StringBuilder();

            //Log.d("UP_PMs_US_name", "AppName:"+usageStats.getPackageName());
            if (requestedPermissions!=null){
                for (int i=0; i<requestedPermissions.length; i++){
                    permissions.append(requestedPermissions[i]+"\n");
                }
                //Log.d("UP_PMs_US", "Permissions: "+permissions);
            }

            //Log.d("SUM2", "UsageStat: "+usageStats+"\n"+"PMs:"+permissions);
            return new TrackerInterface(usageStats, packageManager.getApplicationIcon(ai), packageManager.getApplicationLabel(ai).toString(), permissions);

        } catch (PackageManager.NameNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public void retrieveUsageStats() {
        if (!checkForPermission(context)) {
            view.onUserHasNoPM();
            return;
        }
        List<String> installedApps = getInstalledAppList();
        //Log.d("retrieveUS", "installedApps:  "+installedApps);
        Map<String, UsageStats> usageStats = usageStatsManager.queryAndAggregateUsageStats(getStartTime(), System.currentTimeMillis());
        List<UsageStats> USstats = new ArrayList<>();
        USstats.addAll(usageStats.values());

        List<TrackerInterface> finalList = searchAllCamPMApp(installedApps, USstats);
        view.onUsageStatsRetrieved(finalList);
    }


}
