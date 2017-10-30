package com.example.administrator.youtube.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
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

import static android.R.interpolator.linear;

/**
 * Created by Administrator on 10/10/2017.
 */

public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.MyFavouriteViewHolder> {

    ArrayList<Video> favouriteList;
    OnItemClickListener onItemClickListener;
    public FavouriteAdapter(ArrayList<Video> favourites) {
        favouriteList = favourites;
    }

    @Override
    public MyFavouriteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_upnext_play,parent,false);

        return new MyFavouriteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyFavouriteViewHolder holder, final int position) {
        final Video video = favouriteList.get(position);
        holder.title.setText(video.getTitle());
        holder.author.setText(video.getTitleChannel());
        holder.viewCount.setText(video.getViewCount());
        Picasso.with(holder.item.getContext()).load(video.getThumbnails()).into(holder.thumbnail);
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener!=null) onItemClickListener.onItemCLickListener(position,favouriteList);
            }
        });
        holder.popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener!=null) onItemClickListener.onPopupClickListener(video,holder.itemView);
            }
        });
    }

    @Override
    public int getItemCount() {
        return favouriteList.size();
    }

    public class MyFavouriteViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView author;
        TextView viewCount;
        ImageView thumbnail;
        RelativeLayout popup;
        LinearLayout linearLayout;
        View item;


        public MyFavouriteViewHolder(View itemView) {
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
