package com.example.climbinglog.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "route_videos")
public class RouteVideo {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "route_id")
    private int routeId;

    @ColumnInfo(name = "video_path")
    private String videoPath;

    public RouteVideo(int routeId,
                      String videoPath) {
        this.routeId = routeId;
        this.videoPath = videoPath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRouteId() {
        return routeId;
    }

    public String getVideoPath() {
        return videoPath;
    }
}
