package com.example.climbinglog.EditorActivity;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.climbinglog.R;
import com.example.climbinglog.database.RouteVideo;

import java.io.File;
import java.util.List;

public class ViewVideoAdapter extends RecyclerView.Adapter<ViewVideoAdapter.ViewVideoHolder> {

    private Context context;
    private List<RouteVideo> mVideos;

    private ItemLongClickListener longClickListener;

    public ViewVideoAdapter(Context context, ItemLongClickListener itemLongClickListener) {
        this.context = context;
        this.longClickListener = itemLongClickListener;
    }

    @NonNull
    @Override
    public ViewVideoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_video_rv_list_item, parent,
                false);
        return new ViewVideoHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewVideoHolder holder, int position) {

        MediaController controller = new MediaController(context);
        controller.setPadding(0,0,0, 124);
        controller.setAnchorView(holder.videoView);

        holder.videoView.setMediaController(controller);

        String videoPath = mVideos.get(position).getVideoPath();
        Uri videoUri = Uri.parse(videoPath);
        holder.videoView.setVideoURI(videoUri);
    }

    @Override
    public int getItemCount() {
        if (mVideos == null) return 0;
        return mVideos.size();
    }

    public void setVideos(List<RouteVideo> videos) {
        mVideos = videos;
        notifyDataSetChanged();
    }

    public interface ItemLongClickListener {
        void onItemLongClickListener(int itemId);
    }

    class ViewVideoHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener{

        private VideoView videoView;

        public ViewVideoHolder(@NonNull View itemView) {
            super(itemView);

            videoView = itemView.findViewById(R.id.view_video_rv_videoview);
            videoView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            int itemId = mVideos.get(getAdapterPosition()).getId();
            longClickListener.onItemLongClickListener(itemId);
            return true;
        }
    }
}
