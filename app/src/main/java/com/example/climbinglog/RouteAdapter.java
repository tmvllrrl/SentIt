package com.example.climbinglog;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.climbinglog.database.ClimbingRoute;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class RouteAdapter extends RecyclerView.Adapter<RouteAdapter.RouteViewHolder> {

    // Constant for date format
    private static final String DATE_FORMAT = "MM/dd/yyyy";
    // Date formatter
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

    // Member variable to handle item clicks
    final private ItemClickListener mItemClickListener;
    private List<ClimbingRoute> mRoutes;
    // Class variables for the List that holds task data and the Context
    private Context mContext;

    /**
     * Constructor for the TaskAdapter that initializes the Context.
     *
     * @param context  the current Context
     * @param listener the ItemClickListener
     */
    public RouteAdapter(Context context, ItemClickListener listener) {
        mContext = context;
        mItemClickListener = listener;
    }

    /**
     * Called when ViewHolders are created to fill a RecyclerView.
     *
     * @return A new TaskViewHolder that holds the view for each task
     */
    @Override
    public RouteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the task_layout to a view
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.recyclerview_list_item, parent, false);

        return new RouteViewHolder(view);
    }

    /**
     * Called by the RecyclerView to display data at a specified position in the Cursor.
     *
     * @param holder   The ViewHolder to bind Cursor data to
     * @param position The position of the data in the Cursor
     */
    @Override
    public void onBindViewHolder(RouteViewHolder holder, int position) {
        ClimbingRoute route = mRoutes.get(position);

        if (route.getPhotoPath() != null) {
//            String photoPath = route.getPhotoPath();
//            Bitmap bitmap = BitmapFactory.decodeFile(photoPath);
//            Bitmap rotatedBitmap = AddRouteActivity.rotateBitmap(bitmap, 90);
//            holder.routePicture.setImageBitmap(rotatedBitmap);
            Glide.with(mContext).load(route.getPhotoPath()).into(holder.routePicture);
        } else {
            Glide.with(mContext).load(R.drawable.sable_icon).into(holder.routePicture);
        }

        String typeOfRouteStr = convertTypeOfRoute(route.getTypeOfRoute());
        String convertedRating = convertRating(route.getTypeOfRoute(), route.getRating());
        String ratingAndColor;
        if (route.getRouteColor() != null) {
            ratingAndColor = route.getRouteColor() + " " + convertedRating;
        } else {
            ratingAndColor = convertedRating;
        }

        holder.routeTitle.setText(route.getTitle());
        holder.routeRatingAndColor.setText(ratingAndColor);
        holder.routeType.setText(typeOfRouteStr);
        holder.routeLocation.setText(route.getLocation());
        holder.routeDescription.setText(route.getDescription());
    }

    /**
     * Returns the number of items to display.
     */
    @Override
    public int getItemCount() {
        if (mRoutes == null) {
            return 0;
        }
        return mRoutes.size();
    }

    /**
     * When data changes, this method updates the list of taskEntries
     * and notifies the adapter to use the new values on it
     */
    public void setRoutes(List<ClimbingRoute> climbingRoutes) {
        mRoutes = climbingRoutes;
        notifyDataSetChanged();
    }

    public interface ItemClickListener {
        void onItemClickListener(int itemId);
    }

    // Inner class for creating ViewHolders
    class RouteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView routeTitle;
        TextView routeRatingAndColor;
        TextView routeType;
        TextView routeLocation;
        TextView routeDescription;
        ImageView routePicture;

        /**
         * Constructor for the TaskViewHolders.
         *
         * @param itemView The view inflated in onCreateViewHolder
         */
        public RouteViewHolder(View itemView) {
            super(itemView);

            routeTitle = itemView.findViewById(R.id.tv_listitem_title);
            routeRatingAndColor = itemView.findViewById(R.id.tv_listitem_rating_and_color);
            routeType = itemView.findViewById(R.id.tv_listitem_type_of_route);
            routeLocation = itemView.findViewById(R.id.tv_listitem_location);
            routeDescription = itemView.findViewById(R.id.tv_listitem_description);
            routePicture = itemView.findViewById(R.id.iv_listitem_pic_of_route);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int elementId = mRoutes.get(getAdapterPosition()).getId();
            mItemClickListener.onItemClickListener(elementId);
        }
    }

    ///////////////////////////////// HELPER METHODS ///////////////////////////

    private String convertTypeOfRoute(int typeOfRoute) {
        String typeOfRouteStr = "";

        switch (typeOfRoute) {
            case 0:
                typeOfRouteStr = "Boulder";
                break;
            case 1:
                typeOfRouteStr = "Top-Rope";
                break;
            case 2:
                typeOfRouteStr = "Lead";
                break;
        }
        return typeOfRouteStr;
    }

    private String convertRating(int typeOfRoute, int rating) {
        String ratingStr = "";

        if (typeOfRoute == 0) {
            ratingStr = "V" + rating;

        } else if (typeOfRoute == 1 || typeOfRoute == 2) { //Meaning that its a top-rope/lead route
            double ratingDouble = rating;
            ratingDouble /= 100;
            if (ratingDouble == 5.1) {
                ratingStr = ratingDouble + "0";
            } else {
                ratingStr = "" + ratingDouble;
            }

        }
        return ratingStr;
    }
}
