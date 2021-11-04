package com.example.climbinglog.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.climbinglog.database.ClimbingRoute;

import java.util.List;

@Dao
public interface RouteDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(ClimbingRoute route);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateRoute(ClimbingRoute route);

    @Query("SELECT * FROM routes")
    LiveData<List<ClimbingRoute>> getRoutes();

    @Query("SELECT * FROM routes WHERE type_of_route = :typeOfRoute")
    LiveData<List<ClimbingRoute>> getTypeOfRoutes(int typeOfRoute);

    @Query("SELECT * FROM routes WHERE favorite = 1")
    LiveData<List<ClimbingRoute>> getFavoriteRoutes();

    @Query("DELETE FROM routes")
    void deleteAll();

    @Query("DELETE FROM routes WHERE id = :id")
    void deleteRoute(int id);

    @Query("DELETE FROM route_pictures WHERE id = :id")
    void deleteRoutePicture(int id);

    @Query("DELETE FROM route_videos WHERE id = :id")
    void deleteRouteVideo(int id);

    @Query("DELETE FROM route_pictures WHERE route_id = :routeId")
    void deleteRoutePictures(int routeId);

    @Query("DELETE FROM route_videos WHERE route_id = :routeId")
    void deleteRouteVideos(int routeId);

    @Query("SELECT * FROM routes WHERE id = :id")
    LiveData<ClimbingRoute> getSpecificRoute(int id);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(RoutePicture picture);

    @Query("SELECT * FROM route_pictures WHERE route_id = :routeId")
    LiveData<List<RoutePicture>> getPicturesByRouteId(int routeId);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(RouteVideo video);

    @Query("SELECT * FROM route_videos WHERE route_id = :routeId")
    LiveData<List<RouteVideo>> getVideosByRouteId(int routeId);
}
