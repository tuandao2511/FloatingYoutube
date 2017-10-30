package com.example.administrator.youtube.activity;

import android.app.ActionBar;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.provider.BaseColumns;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;


import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;

//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.administrator.youtube.R;
//import com.example.administrator.youtube.application.AppController;
import com.example.administrator.youtube.application.AppController;
import com.example.administrator.youtube.fragment.HomeFragment;
import com.example.administrator.youtube.fragment.LibraryFragment;
import com.example.administrator.youtube.fragment.TrendingFragment;
import com.example.administrator.youtube.model.Video;
import com.example.administrator.youtube.youtubeplayer.YoutubeConnector;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;

/**
 * Created by linh on 9/26/2017.
 */

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener, View.OnClickListener {
    private SearchView searchView;
    private ImageView settings;
    private SimpleCursorAdapter mAdapter;
    private HomeFragment homeFragment = new HomeFragment();
    private TrendingFragment trendingFragment = new TrendingFragment();
    private LibraryFragment libraryFragment = new LibraryFragment();
    private BottomNavigationView bottom_control;
    private static  final int  REQUEST_OVERLAY_PERMISSION = 1;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.ic_ho:
                    gotoFragment(homeFragment);
                    return true;
                case R.id.action_schedules:
                    gotoFragment(trendingFragment);
                    return true;
                case R.id.action_music:
                    gotoFragment(libraryFragment);
                    return true;
            }
            return false;
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        Toolbar.LayoutParams lp1 = new Toolbar.LayoutParams(Toolbar.LayoutParams.MATCH_PARENT, android.support.v7.app.ActionBar.LayoutParams.MATCH_PARENT);
        View customToolBar = LayoutInflater.from(this).inflate(R.layout.custom_toolbar, null, false);
        searchView = (SearchView) customToolBar.findViewById(R.id.search);
        settings = (ImageView) customToolBar.findViewById(R.id.settings);
        settings.setOnClickListener(this);
        searchView.setOnQueryTextListener(this);
        toolbar.addView(customToolBar, lp1);
        setSupportActionBar(toolbar);

        onDisplayPopupPermission();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if  (!Settings.canDrawOverlays(this)){
                // Show alert dialog to the user saying a separate permission is needed
                // Launch the settings activity if the user prefers
                Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                startActivityForResult(myIntent,REQUEST_OVERLAY_PERMISSION);
            }
        }

        bottom_control = (BottomNavigationView) findViewById(R.id.bottom_control);
        bottom_control.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_layout, homeFragment).commit();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
        }
        return false;
    }

    private void onDisplayPopupPermission() {
        if (isMIUI()) {
            try {
                // MIUI 8
                Intent localIntent = new Intent("miui.intent.action.APP_PERM_EDITOR");
                localIntent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
                localIntent.putExtra("extra_pkgname", getPackageName());
                startActivity(localIntent);
            } catch (Exception e) {
                try {
                    // MIUI 5/6/7
                    Intent localIntent = new Intent("miui.intent.action.APP_PERM_EDITOR");
                    localIntent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
                    localIntent.putExtra("extra_pkgname", getPackageName());
                    startActivity(localIntent);
                } catch (Exception e1) {
                    // Otherwise jump to application details
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                }
            }
        }
    }


    private static boolean isMIUI() {
        String device = Build.MANUFACTURER;
        if (device.equals("Xiaomi")) {
            try {
                Properties prop = new Properties();
                prop.load(new FileInputStream(new File(Environment.getRootDirectory(), "build.prop")));
                return prop.getProperty("ro.miui.ui.version.code", null) != null
                        || prop.getProperty("ro.miui.ui.version.name", null) != null
                        || prop.getProperty("ro.miui.internal.storage", null) != null;
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return false;
    }

    private void gotoFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_layout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText.length() > 0) {
            newText = newText.replace(" ", "+");
            String url = "http://suggestqueries.google.com/complete/search?client=youtube&ds=yt&client=firefox&hl=vi&q="
                    + newText;
            final String finalNewText = newText;
            JsonArrayRequest request = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {
                        JSONArray jsonArraySuggestion = (JSONArray) response.get(1);
                        final String[] suggestions = new String[10];
                        for (int i = 0; i < 10; i++) {
                            if (!jsonArraySuggestion.isNull(i)) {
                                suggestions[i] = jsonArraySuggestion.get(i).toString();
                            }
                        }
                        Log.d("Suggestions", Arrays.toString(suggestions));
                        String[] columnNames = {"_id", "suggestion"};
                        MatrixCursor cursor = new MatrixCursor(columnNames);
                        String[] temp = new String[2];
                        int id = 0;
                        for (String item : suggestions) {
                            if (item != null) {
                                temp[0] = Integer.toString(id++);
                                temp[1] = item;
                                cursor.addRow(temp);
                            }
                        }
                        CursorAdapter cursorAdapter = new CursorAdapter(MainActivity.this,cursor,false) {
                            @Override
                            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                                return LayoutInflater.from(context).inflate(R.layout.search_suggestion_list_item, parent, false);
                            }



                            @Override
                            public void bindView(View view, Context context, Cursor cursor) {
                                final TextView suggest = (TextView) view.findViewById(R.id.suggest);
                                ImageView putInSearchBox = (ImageView) view.findViewById(R.id.put_in_search_box);
                                String body = cursor.getString(cursor.getColumnIndexOrThrow("suggestion"));
                                Log.e("cx",body);
                                suggest.setText(body);
                                suggest.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(final View v) {
                                        searchView.setQuery(suggest.getText(), true);
                                        Log.e("search",suggest.getText().toString()+" ");
                                        Intent intent = new Intent(MainActivity.this,YoutubeSearchActivity.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putString("key",suggest.getText().toString());
                                        intent.putExtras(bundle);
                                        startActivity(intent);
                                        //  homeFragment.updateList(suggest.getText().toString());

//                                        new Thread(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                try {
//                                                    Log.e("search",suggest.getText().toString()+" ");
//                                                    youtubeConnector.search(suggest.getText().toString(), videos);
//                                                } catch (IOException e) {
//                                                    Log.e("search",e.toString()+" ");
//                                                    e.printStackTrace();
//                                                }
//                                            }
//                                        });


                                        searchView.clearFocus();

                                    }
                                });
                                putInSearchBox.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        searchView.setQuery(suggest.getText(), false);
                                    }
                                });

                            }
                        };
                        searchView.setSuggestionsAdapter(cursorAdapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }

            });
            AppController.getInstance().addToRequestQueue(request);
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(MainActivity.this, SettingActivity.class);
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_OVERLAY_PERMISSION){
           if  (!Settings.canDrawOverlays(this)) {
                Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                startActivityForResult(myIntent,REQUEST_OVERLAY_PERMISSION);
            }
        }
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//
//        // ATTENTION: This was auto-generated to implement the App Indexing API.
//        // See https://g.co/AppIndexing/AndroidStudio for more information.
//
//    }
//    public Action getIndexApiAction() {
//        Thing object = new Thing.Builder()
//                .setName("YoutubeSearch Page")
//                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
//                .build();
//        return new Action.Builder(Action.TYPE_VIEW)
//                .setObject(object)
//                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
//                .build();
//    }
//    @Override
//    public void onStop() {
//        super.onStop();
//
//        // ATTENTION: This was auto-generated to implement the App Indexing API.
//        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        AppIndex.AppIndexApi.end(client, getIndexApiAction());
//        client.disconnect();
//    }

    @Override
    public void onBackPressed() {
     finish();
    }
}
