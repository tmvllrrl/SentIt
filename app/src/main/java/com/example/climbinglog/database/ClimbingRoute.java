package com.example.climbinglog.database;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity (tableName = "routes")
public class ClimbingRoute {

    @PrimaryKey (autoGenerate = true)
    @ColumnInfo (name = "id")
    private int id;

    @ColumnInfo (name = "title")
    private String title; //Title of the route

    @ColumnInfo (name = "location")
    private String location; //Location of the route

    @ColumnInfo (name = "description")
    private String description; //A short description/notes about the route

    @ColumnInfo (name = "type_of_route")
    private int typeOfRoute; //Coded as 0, 1, or 2; 1 - boulder, 2 - top-rope, 3 - lead

    @ColumnInfo (name = "rating")
    private int rating; //See journal for details about rating

    @ColumnInfo (name = "date_climbed")
    private String date; //Date that the route was climbed

    @ColumnInfo (name = "photo_path")
    private String photoPath; //The path to the photo that is stored in the external memory

    @ColumnInfo (name = "favorite")
    private int favorite; //Coded as 0 for not a favorite, 1 for favorite

    @ColumnInfo (name = "who_climbed")
    private String whoClimbed;

    @ColumnInfo (name = "route_color")
    private String routeColor;

    public ClimbingRoute(String title,
                         int rating,
                         int typeOfRoute,
                         String location,
                         String description,
                         String date,
                         String photoPath,
                         int favorite,
                         String whoClimbed,
                         String routeColor) {
        this.title = title;
        this.rating = rating;
        this.typeOfRoute = typeOfRoute;
        this.location = location;
        this.description = description;
        this.date = date;
        this.photoPath = photoPath;
        this.favorite = favorite;
        this.whoClimbed = whoClimbed;
        this.routeColor = routeColor;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public int getRating() {
        return rating;
    }

    public int getTypeOfRoute() {
        return typeOfRoute;
    }

    public String getLocation() {
        return location;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public int getFavorite() {
        return favorite;
    }

    public String getWhoClimbed() {
        return whoClimbed;
    }

    public String getRouteColor() {
        return routeColor;
    }

}
