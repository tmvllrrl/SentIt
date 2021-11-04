package com.example.climbinglog.EditorActivity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.climbinglog.R;
import com.example.climbinglog.RouteViewModel;
import com.example.climbinglog.database.RouteVideo;

import java.util.List;

public class ViewVideoFragment extends Fragment implements ViewVideoAdapter.ItemLongClickListener {

    private RouteViewModel mViewModel;
    private RecyclerView recyclerView;
    private ViewVideoAdapter adapter;

    private int routeId = -1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_view_video, container, false);

        Bundle bundle = getArguments();
        routeId = bundle.getInt("route_id", EditorActivity.DEFAULT_TASK_ID);

        mViewModel = new ViewModelProvider(requireActivity()).get(RouteViewModel.class);

        recyclerView = rootView.findViewById(R.id.view_video_rv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ViewVideoAdapter(getContext(), this);
        recyclerView.setAdapter(adapter);

        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);

        if (routeId != -1) {
            mViewModel.getVideosByRouteId(routeId).observe(getViewLifecycleOwner(), new Observer<List<RouteVideo>>() {
                @Override
                public void onChanged(List<RouteVideo> routeVideos) {
                    adapter.setVideos(routeVideos);
                }
            });
        }
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_view_video_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_take_video:
                if (routeId != -1) {
                    takeVideoIntent();
                } else {
                    Toast.makeText(getContext(), "Must save route before taking video",
                            Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_delete_videos:
                if (routeId != -1) {
                    AlertDialog.Builder mAlert = new AlertDialog.Builder(getContext());
                    mAlert.setTitle("Are you sure?");
                    mAlert.setMessage("Are you sure that you want to delete all videos for this route?");

                    mAlert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mViewModel.deleteRouteVideos(routeId);
                        }
                    });

                    mAlert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

                    mAlert.show();
                }
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EditorFragment.REQUEST_VIDEO_CAPTURE && resultCode == Activity.RESULT_OK) {
            Uri videoUri = data.getData();
            String videoPath = videoUri.toString();

            if (videoPath != null) {
                mViewModel.insert(new RouteVideo(routeId, videoPath));
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getContext(), "Hello", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(getContext(), "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();

                    return;
                }
        }
    }

    private void takeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, EditorFragment.REQUEST_VIDEO_CAPTURE);
        }
    }

    @Override
    public void onItemLongClickListener(int itemId) {
        AlertDialog.Builder mAlert = new AlertDialog.Builder(getContext());
        mAlert.setTitle("Are you sure?");
        mAlert.setMessage("Are you sure that you want to delete this video?");

        mAlert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mViewModel.deleteRouteVideo(itemId);
            }
        });

        mAlert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        mAlert.show();
    }
}
