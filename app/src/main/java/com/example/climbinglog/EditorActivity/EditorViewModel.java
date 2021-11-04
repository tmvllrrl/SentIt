package com.example.climbinglog.EditorActivity;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class EditorViewModel extends ViewModel {
    private MutableLiveData<String> tentativePhotoPath = new MutableLiveData<>();

    public EditorViewModel() {

    }

    public void setTentativePhotoPath(String tentativePhotoPath) {
        this.tentativePhotoPath.setValue(tentativePhotoPath);
    }

    public MutableLiveData<String> getTentativePhotoPath() {
        return this.tentativePhotoPath;
    }
}
