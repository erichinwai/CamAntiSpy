package com.example.camantispy;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ScanAppResult extends ListActivity {
    private TextView textview_result;
    ArrayList<String> ArraylistItems = new ArrayList<String>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Toast.makeText(this, "Clicked " +adapter.getItem(position), Toast.LENGTH_SHORT).show();
        //delete installed apps
        Uri package_del = Uri.parse("package:"+adapter.getItem(position));
        Intent intent = new Intent(Intent.ACTION_DELETE, package_del);
        startActivity(intent);
        Toast.makeText(getApplicationContext(), "Uninstall", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_app_result);
        textview_result = (TextView) findViewById(R.id.scanDone);
        adapter=new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                ArraylistItems);
        setListAdapter(adapter);
        Intent in = getIntent();
        Bundle bun = in.getExtras();

        if(bun.getInt("malsize") != 0){
            String detected = "Suspicious  Malware detected:";
            textview_result.setText(detected);
            String [] posList =  bun.getStringArray("mallist");
            for(int i = 0; i < posList.length; i++) {
                if (posList[i] != null) {
                    Log.d("Test:", i + "    " + posList[i]);
                    //add items into the listView
                    adapter.add(posList[i]);
                }
            }

        }else{
            Log.d("error", "onCreate: ");
        }
    }
}
