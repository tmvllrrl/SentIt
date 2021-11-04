package com.example.climbinglog.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "route_pictures")
public class RoutePicture {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "route_id")
    private int routeId;

    @ColumnInfo(name = "photo_path")
    private String photoPath;

    public RoutePicture(int routeId,
                        String photoPath) {
        this.routeId = routeId;
        this.photoPath = photoPath;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getRouteId() {
        return routeId;
    }

    public String getPhotoPath() {
        return photoPath;
    }
}
