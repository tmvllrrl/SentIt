package com.example.climbinglog;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.climbinglog.database.ClimbingRoute;
import com.example.climbinglog.database.ClimbingRouteRepository;
import com.example.climbinglog.database.RoutePicture;
import com.example.climbinglog.database.RouteVideo;

import java.util.List;

public class RouteViewModel extends AndroidViewModel {

    private ClimbingRouteRepository mRepository;
    private LiveData<List<ClimbingRoute>> mRoutes;

    public RouteViewModel(Application application) {
        super(application);
        mRepository = new ClimbingRouteRepository(application);
        mRoutes = mRepository.getRoutes();
    }

    public void insert(ClimbingRoute route) {
        mRepository.insert(route);
    }

    public void insert(RoutePicture picture) {
        mRepository.insert(picture);
    }

    public void insert(RouteVideo video) {
        mRepository.insert(video);
    }

    public void updateRoute(ClimbingRoute route) {
        mRepository.updateRoute(route);
    }

    public void deleteRoute(int id) {
        mRepository.deleteRoute(id);
    }

    public void deleteRoutePicture(int id) {
        mRepository.deleteRoutePicture(id);
    }

    public void deleteRouteVideo(int id) {
        mRepository.deleteRouteVideo(id);
    }

    public void deleteAll() {
        mRepository.deleteAll();
    }

    public void deleteRoutePictures(int routeId) {
        mRepository.deleteRoutePictures(routeId);
    }

    public void deleteRouteVideos(int routeId) {
        mRepository.deleteRouteVideos(routeId);
    }

    public LiveData<List<ClimbingRoute>> getRoutes() {
        return mRoutes;
    }

    public LiveData<List<ClimbingRoute>> getTypeOfRoutes(int typeOfRoute) {
        LiveData<List<ClimbingRoute>> specificRoutes = mRepository.getTypeOfRoutes(typeOfRoute);
        return specificRoutes;
    }

    public LiveData<List<ClimbingRoute>> getFavoriteRoutes() {
        LiveData<List<ClimbingRoute>> favoriteRoutes = mRepository.getFavoriteRoutes();
        return favoriteRoutes;
    }

    public LiveData<ClimbingRoute> getSpecificRoute(int id) {
        return mRepository.getSpecificRoute(id);
    }

    public LiveData<List<RoutePicture>> getPicturesByRouteId(int routeId) {
        return mRepository.getPicturesByRouteId(routeId);
    }

    public LiveData<List<RouteVideo>> getVideosByRouteId(int routeId) {
        return mRepository.getVideosByRouteId(routeId);
    }
}
