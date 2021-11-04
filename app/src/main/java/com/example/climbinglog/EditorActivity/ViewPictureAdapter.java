package com.example.climbinglog.EditorActivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.climbinglog.R;
import com.example.climbinglog.database.RoutePicture;

import java.util.List;

public class ViewPictureAdapter extends RecyclerView.Adapter<ViewPictureAdapter.ViewPictureViewHolder> {

    private List<RoutePicture> mPictures;
    private Context mContext;

    public ItemLongClickListener longClickListener;

    public ViewPictureAdapter(Context context, ItemLongClickListener longClickListener) {
        this.mContext = context;
        this.longClickListener = longClickListener;
    }

    @NonNull
    @Override
    public ViewPictureAdapter.ViewPictureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_picture_rv_list_item,
                parent, false);
        return new ViewPictureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewPictureAdapter.ViewPictureViewHolder holder, int position) {
        String photoPath = mPictures.get(position).getPhotoPath();
        if (photoPath != null) Glide.with(mContext).load(photoPath).into(holder.picture);
    }

    @Override
    public int getItemCount() {
        if (mPictures == null) return 0;
        return mPictures.size();
    }

    public void setPictures(List<RoutePicture> pictures) {
        mPictures = pictures;
        notifyDataSetChanged();
    }

    public int getPositionId(int position) {
        int positionId = -1;
        positionId = mPictures.get(position).getId();
        return positionId;
    }

    public interface ItemLongClickListener {
        void onItemLongClickListener(int itemId);
    }

    class ViewPictureViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        private ImageView picture;

        public ViewPictureViewHolder(@NonNull View itemView) {
            super(itemView);

            picture = itemView.findViewById(R.id.view_picture_iv);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            int itemId = mPictures.get(getAdapterPosition()).getId();
            longClickListener.onItemLongClickListener(itemId);
            return true;
        }
    }
}
