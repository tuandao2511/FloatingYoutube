package com.example.administrator.youtube.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.youtube.R;
import com.example.administrator.youtube.model.Video;
import com.example.administrator.youtube.model.Video;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 9/18/2017.
 */

public class VideoAdapter extends ArrayAdapter<Video> {

    private static final String LOG_TAG = VideoAdapter.class.getSimpleName();

    public VideoAdapter(@NonNull Context context, @NonNull ArrayList<Video> video) {
        super(context,0,video);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;

        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item2, parent, false);
        }

        Video video = getItem(position);

        TextView title_view = (TextView) listItemView.findViewById(R.id.textTitleView);
        title_view.setText(video.getmTitle());

        TextView title_channel_view = (TextView) listItemView.findViewById(R.id.textTitleChannelView);
        title_channel_view.setText(video.getmTitleChannel());

        TextView count_view_text = (TextView) listItemView.findViewById(R.id.text_count_view);
        count_view_text.setText(""+video.getmViewCount() +" views");

        TextView elapsed_time_view = (TextView) listItemView.findViewById(R.id.textTimeView);
        elapsed_time_view.setText(video.getmEslapedTime());

        ImageView imageVideo = (ImageView) listItemView.findViewById(R.id.imageView);
        Picasso.with(getContext()).load(String.valueOf(video.getmThumbnails())).into(imageVideo);




        return listItemView;
    }


}
