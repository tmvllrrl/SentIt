package com.example.climbinglog.database;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ClimbingRouteRepository {

    private RouteDao mRouteDao;
    private LiveData<List<ClimbingRoute>> mRoutes;

    public ClimbingRouteRepository(Application application) {
        ClimbingRouteDatabase db = ClimbingRouteDatabase.getDatabase(application);
        mRouteDao = db.routeDao();
        mRoutes = mRouteDao.getRoutes();
    }

    public void insert(ClimbingRoute route) {
        ClimbingRouteDatabase.databaseWriteExecutor.execute(() -> {
            mRouteDao.insert(route);
        });
    }

    public void insert(RoutePicture picture) {
        ClimbingRouteDatabase.databaseWriteExecutor.execute(() -> {
            mRouteDao.insert(picture);
        });
    }

    public void insert(RouteVideo video) {
        ClimbingRouteDatabase.databaseWriteExecutor.execute(() -> {
            mRouteDao.insert(video);
        });
    }

    public void updateRoute(ClimbingRoute route) {
        ClimbingRouteDatabase.databaseWriteExecutor.execute(() -> {
            mRouteDao.updateRoute(route);
        });
    }

    public void deleteRoute(int id) {
        ClimbingRouteDatabase.databaseWriteExecutor.execute(() -> {
            mRouteDao.deleteRoute(id);
            mRouteDao.deleteRoutePictures(id);
            mRouteDao.deleteRouteVideos(id);
        });
    }

    public void deleteRoutePicture(int id) {
        ClimbingRouteDatabase.databaseWriteExecutor.execute(() -> {
            mRouteDao.deleteRoutePicture(id);
        });
    }

    public void deleteRouteVideo(int id) {
        ClimbingRouteDatabase.databaseWriteExecutor.execute(() -> {
            mRouteDao.deleteRouteVideo(id);
        });
    }

    public void deleteRoutePictures(int routeId) {
        ClimbingRouteDatabase.databaseWriteExecutor.execute(() -> {
            mRouteDao.deleteRoutePictures(routeId);
        });
    }

    public void deleteRouteVideos(int routeId) {
        ClimbingRouteDatabase.databaseWriteExecutor.execute(() -> {
            mRouteDao.deleteRouteVideos(routeId);
        });
    }

    public void deleteAll() {
        ClimbingRouteDatabase.databaseWriteExecutor.execute(() -> {
            mRouteDao.deleteAll();
        });
    }



    public LiveData<List<ClimbingRoute>> getRoutes() {
        return this.mRoutes;
    }

    public LiveData<ClimbingRoute> getSpecificRoute(int id) {
        LiveData<ClimbingRoute> route = mRouteDao.getSpecificRoute(id);
        return route;
    }

    public LiveData<List<ClimbingRoute>> getTypeOfRoutes(int typeOfRoute) {
        LiveData<List<ClimbingRoute>> specificTypeOfRoutes = mRouteDao.getTypeOfRoutes(typeOfRoute);
        return specificTypeOfRoutes;
    }

    public LiveData<List<ClimbingRoute>> getFavoriteRoutes() {
        LiveData<List<ClimbingRoute>> favoriteRoutes = mRouteDao.getFavoriteRoutes();
        return favoriteRoutes;
    }

    public LiveData<List<RoutePicture>> getPicturesByRouteId(int routeId) {
        return mRouteDao.getPicturesByRouteId(routeId);
    }

    public LiveData<List<RouteVideo>> getVideosByRouteId(int routeId) {
        return mRouteDao.getVideosByRouteId(routeId);
    }
}
