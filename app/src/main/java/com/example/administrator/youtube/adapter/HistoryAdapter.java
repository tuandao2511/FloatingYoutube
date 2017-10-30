package com.example.administrator.youtube.adapter;

import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.administrator.youtube.R;
import com.example.administrator.youtube.model.Video;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 10/6/2017.
 */

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyHistoryViewHolder> {
    public static final String LOG_TAG = HistoryAdapter.class.getSimpleName();
    private ArrayList<Video> listHistory;

    OnItemClickListener onItemClickListener;

    public HistoryAdapter(ArrayList<Video> listHistory) {
        this.listHistory = listHistory;
    }

    @Override
    public MyHistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_upnext_play,parent,false);

        return new MyHistoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyHistoryViewHolder holder, final int position) {
        final Video video = listHistory.get(position);
        Log.v(LOG_TAG,"video o day la gi "+video.getTitle() + " id =" +video.getID());
        holder.title.setText(video.getTitle());
        holder.author.setText(video.getTitleChannel());
        holder.viewCount.setText(video.getViewCount());
        Picasso.with(holder.item.getContext()).load(video.getThumbnails()).into(holder.thumbnail);
        holder.popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener!=null)
                    onItemClickListener.onPopupClickListener(video,holder.itemView);
            }
        });
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener!=null) onItemClickListener.onItemCLickListener(position,listHistory);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listHistory.size();
    }


    public class MyHistoryViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        TextView author;
        TextView viewCount;
        ImageView thumbnail;
        RelativeLayout popup;
        LinearLayout linearLayout;
        View item;

        public MyHistoryViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title_video);
            author = (TextView) itemView.findViewById(R.id.author);
            viewCount = (TextView) itemView.findViewById(R.id.viewVideo);
            thumbnail = (ImageView) itemView.findViewById(R.id.thumb_video);
            popup = (RelativeLayout) itemView.findViewById(R.id.popup);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.root_up_next);
            item = itemView;
        }
    }
    public interface OnItemClickListener{
        public void onItemCLickListener(int position,ArrayList<Video> videos);
        public void onPopupClickListener(Video video, View view);
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener ) {
        this.onItemClickListener = onItemClickListener;
    }
}




