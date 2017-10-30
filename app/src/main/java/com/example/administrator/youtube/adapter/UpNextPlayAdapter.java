package com.example.administrator.youtube.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.youtube.R;
import com.example.administrator.youtube.model.Video;
import com.example.administrator.youtube.youtubeplayer.FullScreenManager;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by linh on 10/3/2017.
 */

public class UpNextPlayAdapter extends RecyclerView.Adapter<UpNextPlayAdapter.ViewHolder> {
    private View view;
    private Context context;
    private ArrayList<Video> videos = new ArrayList<>();
    private onItemClickListeners onItemClickListeners;

    public UpNextPlayAdapter(Context context, ArrayList<Video> videos) {
        this.context = context;
        this.videos = videos;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.item_upnext_play, null, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Video video = videos.get(position);
        holder.title.setText(video.getTitle());
        holder.author.setText(video.getTitleChannel());
        holder.viewCount.setText(String.valueOf(video.getViewCount()));
        Picasso.with(context).load(video.getThumbnails())
                .into(holder.thumb);

        holder.root_up_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListeners!=null) onItemClickListeners.onItemClickListener(video,position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout root_up_next;
        private ImageView thumb;
        private TextView title, author, viewCount;

        public ViewHolder(View itemView) {
            super(itemView);
            root_up_next = (LinearLayout) itemView.findViewById(R.id.root_up_next);
            thumb = (ImageView) itemView.findViewById(R.id.thumb_video);
            title = (TextView) itemView.findViewById(R.id.title_video);
            author = (TextView) itemView.findViewById(R.id.author);
            viewCount = (TextView) itemView.findViewById(R.id.viewVideo);

        }
    }
    public void setOnItemClicklisteners(onItemClickListeners onItemClicklisteners){
        this.onItemClickListeners = onItemClicklisteners;
    }
    public interface onItemClickListeners{
        void onItemClickListener(Video video,int position);
    }
    public void playVideoFromUpNextList(Video video,Video oldVideo){
        videos.remove(video);
        videos.add(videos.size(),oldVideo);
        notifyDataSetChanged();
    }
    public void refeshItem(ArrayList<Video> newList){
        videos.clear();
        videos  = new ArrayList<>();
        videos.addAll(newList);
        Log.e("SIZE",newList.size()+ " " +videos.size());
        notifyDataSetChanged();
    }
    public void playVideoPrevFromUpNextList(Video prev,Video cur){

        videos.remove(prev);
        videos.add(0,cur);
        notifyDataSetChanged();
    }
    public ArrayList<Video> getVideos(){
        return videos;
    }
}
