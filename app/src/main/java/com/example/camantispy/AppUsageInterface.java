package com.example.camantispy;

import java.util.List;

public interface AppUsageInterface {
    interface View{
        void onUsageStatsRetrieved(List<TrackerInterface> list);
        void onUserHasNoPM();
    }

    interface Presenter{
        void retrieveUsageStats();
    }
}

