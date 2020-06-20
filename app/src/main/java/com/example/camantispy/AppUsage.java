package com.example.camantispy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class AppUsage extends AppCompatActivity implements AppUsageInterface.View{

    //private ProgressBar progressBar;
    private TextView PMmsg;

    private AppUsageInterface.Presenter presenter;
    private AppUsageAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_usage);

        //show all camera related app
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        //progressbar in case too many apps
        //progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        PMmsg = (TextView) findViewById(R.id.grant_permission_message);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AppUsageAdapter();
        recyclerView.setAdapter(adapter);

        PMmsg.setOnClickListener(v -> openSettings());
        presenter = new AppUsagePresenter(this, this);

    }

    private void openSettings() {
        startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
    }

    @Override
    protected void onResume() {
        super.onResume();
        //showProgressBar(true);
        presenter.retrieveUsageStats();
    }

    @Override
    public void onUsageStatsRetrieved(List<TrackerInterface> list) {
        //showProgressBar(false);
        PMmsg.setVisibility(GONE);
        adapter.setList(list);
    }

    @Override
    public void onUserHasNoPM() {
        //showProgressBar(false);
        PMmsg.setVisibility(VISIBLE);
    }

    /*private void showProgressBar(boolean show) {
        if (show) {
            progressBar.setVisibility(VISIBLE);
        } else {
            progressBar.setVisibility(GONE);
        }
    }*/
}
