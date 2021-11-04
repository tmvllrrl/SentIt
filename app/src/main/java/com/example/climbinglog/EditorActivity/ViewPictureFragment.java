package com.example.climbinglog.EditorActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowMetrics;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.climbinglog.EditorActivity.EditorViewModel;
import com.example.climbinglog.EditorActivity.EditorActivity;
import com.example.climbinglog.MainActivity;
import com.example.climbinglog.R;
import com.example.climbinglog.RouteViewModel;
import com.example.climbinglog.database.ClimbingRoute;
import com.example.climbinglog.database.RoutePicture;
import com.zolad.zoominimageview.ZoomInImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ViewPictureFragment extends Fragment implements ViewPictureAdapter.ItemLongClickListener {

    private RouteViewModel mViewModel;
    private ImageView noPictureImageView;

    private RecyclerView viewPictureRecyclerView;
    private ViewPictureAdapter viewPictureAdapter;

    private EditorViewModel mEditorViewModel;
    private RoutePicture coverPhoto;

    private String potentialPhotoPath;
    private int routeId = -1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_view_picture, container, false);

        Bundle bundle = getArguments();
        routeId = bundle.getInt("route_id", EditorActivity.DEFAULT_TASK_ID);

        viewPictureRecyclerView = rootView.findViewById(R.id.view_picture_rv);
        viewPictureRecyclerView.setHasFixedSize(true);
        viewPictureRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        viewPictureAdapter = new ViewPictureAdapter(getContext(), this);
        viewPictureRecyclerView.setAdapter(viewPictureAdapter);

//        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
//                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
//            @Override
//            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
//                return false;
//            }
//
//            @Override
//            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
//                int position = viewHolder.getAdapterPosition();
//                int positionId = viewPictureAdapter.getPositionId(position);
//                //mViewModel.deleteRoutePicture(positionId);
//                Toast.makeText(getContext(), "position: " + position + " position id: " + positionId,
//                        Toast.LENGTH_SHORT).show();
//            }
//        });
//        helper.attachToRecyclerView(viewPictureRecyclerView);

        mViewModel = new ViewModelProvider(requireActivity()).get(RouteViewModel.class);

        if (routeId != -1) {
            mViewModel.getPicturesByRouteId(routeId).observe(getViewLifecycleOwner(), new Observer<List<RoutePicture>>() {
                @Override
                public void onChanged(List<RoutePicture> routePictures) {
                    //if (coverPhoto != null) routePictures.add(0, coverPhoto);
                    viewPictureAdapter.setPictures(routePictures);
                }
            });
        }

        mEditorViewModel = new ViewModelProvider(requireActivity()).get(EditorViewModel.class);
        mEditorViewModel.getTentativePhotoPath().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String tentativePhotoPath) {
                if (tentativePhotoPath != null) {
                    coverPhoto = new RoutePicture(-1, tentativePhotoPath);
                }
            }
        });

        /**
         * I'm utilizing two view models to make sure that the correct cover photo is being displayed,
         * even if the user has yet to save the route yet.
         * */

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_view_pictures_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_take_picture:
                if (routeId == -1) {
                    Toast.makeText(getContext(), "Save route before taking a picture",
                            Toast.LENGTH_SHORT).show();
                } else {
                    takePictureIntent();
                }
                return true;
            case R.id.action_delete_pictures:
                if (routeId != -1) {
                    AlertDialog.Builder mAlert = new AlertDialog.Builder(getContext());
                    mAlert.setTitle("Are you sure?");
                    mAlert.setMessage("Are you sure that you want to delete all pictures for this route?");

                    mAlert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mViewModel.deleteRoutePictures(routeId);
                        }
                    });

                    mAlert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

                    mAlert.show();
                } else {
                    Toast.makeText(getContext(), "Save route before selecting this option",
                            Toast.LENGTH_SHORT).show();
                }

                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EditorFragment.REQUEST_TAKE_PHOTO && resultCode == getActivity().RESULT_OK) {
            if (potentialPhotoPath != null) {
                mViewModel.insert(new RoutePicture(routeId, potentialPhotoPath));
            }
        }
    }

    /**
     * Makes an intent to take a picture and creates a file to store that image in
     * */
    private void takePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.d("ViewPictureFragment", "Error occurred while trying to create file");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getContext(),
                        "com.example.climbinglog.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, EditorFragment.REQUEST_TAKE_PHOTO);
            }
        }
    }

    /**
     * Creates a unique file to store an image in to. Used when takePictureIntent() is called
     * */
    public File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        potentialPhotoPath = image.getAbsolutePath();
        Log.i("hello", potentialPhotoPath);
        return image;
    }

    @Override
    public void onItemLongClickListener(int itemId) {
        AlertDialog.Builder mAlert = new AlertDialog.Builder(getContext());
        mAlert.setTitle("Are you sure?");
        mAlert.setMessage("Are you sure that you want to delete this picture?");

        mAlert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mViewModel.deleteRoutePicture(itemId);
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
