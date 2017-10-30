package com.example.administrator.youtube.method;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.example.administrator.youtube.model.Video;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Administrator on 14-Oct-17.
 */

public final class FetchVideoId {


    public static ArrayList<String> loadJsonFromAsset(Context myContext,String fileName) {
        String json = null;
        try {
            InputStream is = myContext.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer,"UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        ArrayList<String> videos = new ArrayList<>();
        try {
            JSONObject jsonRootObject = new JSONObject(json);
            JSONArray jsonArrayItems = jsonRootObject.getJSONArray("items");
            for (int i=0; i< jsonArrayItems.length(); i++) {
                JSONObject jsonObject = jsonArrayItems.getJSONObject(i);
                JSONObject jsonObjectId = jsonObject.getJSONObject("id");
                String id = jsonObjectId.getString("videoId");
                Log.v("id la gi ", id);
                videos.add(id);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        videos.addAll(loadJsonFromAsset(myContext,"dataWithPage.json"));

        return videos;
    }


}
