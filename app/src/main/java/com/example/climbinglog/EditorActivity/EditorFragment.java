package com.example.climbinglog.EditorActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NavUtils;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.climbinglog.DatePickerFragment;
import com.example.climbinglog.MainActivity;
import com.example.climbinglog.R;
import com.example.climbinglog.RouteViewModel;
import com.example.climbinglog.database.ClimbingRoute;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditorFragment extends Fragment {

    public static int REQUEST_TAKE_PHOTO = 1;
    public static int REQUEST_VIDEO_CAPTURE = 2;

    private EditText titleEditText;
    private Spinner typeOfRouteSpinner;
    private Spinner ratingSpinner;
    private EditText locationEditText;
    private Button selectDateButton;
    private EditText descriptionEditText;
    private ImageButton picOfRouteButton;
    private ImageButton favoriteButton;
    private EditText whoClimbedEditText;
    private EditText routeColor;

    private int typeOfRoute;
    private int rating;
    private int ratingPrev; //Previous rating to correctly set the ratings spinner when editing an activity
    private String photoPath = null;
    private String potentialPhotoPath;
    private String date;
    private String title;
    private String location;
    private String description;
    private int favorite;
    private String whoClimbed;
    private String color;

    private TextView displayDateTextView;
    private RouteViewModel mViewModel;
    private EditorViewModel mEditorViewModel;

    private boolean mRouteHasChanged = false;
    private boolean favorited = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mRouteHasChanged = true;
            return false;
        }
    };

    private int mRouteID = EditorActivity.DEFAULT_TASK_ID;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_editor, container, false);

        initViews(rootView);

        Bundle bundle = getArguments();
        mRouteID = bundle.getInt("route_id", EditorActivity.DEFAULT_TASK_ID);

        mViewModel = new ViewModelProvider(requireActivity()).get(RouteViewModel.class);
        if (mRouteID != EditorActivity.DEFAULT_TASK_ID) {
            mViewModel.getSpecificRoute(mRouteID).observe(getViewLifecycleOwner(), new Observer<ClimbingRoute>() {
                @Override
                public void onChanged(ClimbingRoute climbingRoute) {
                    mViewModel.getSpecificRoute(mRouteID).removeObserver(this);
                    populateUiAndData(climbingRoute);
                }
            });
        }

        mEditorViewModel = new ViewModelProvider(requireActivity()).get(EditorViewModel.class);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_editor_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveRoute();
                return true;
            case R.id.action_delete_route:
                deleteRoute();
                return true;
            case android.R.id.home:
                if (mRouteHasChanged) {
                    saveRoute();
                }

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == getActivity().RESULT_OK) {
            picOfRouteButton.setScaleType(ImageView.ScaleType.CENTER_CROP);
            if (potentialPhotoPath != null) {
                photoPath = potentialPhotoPath;
                Glide.with(getContext()).load(photoPath).into(picOfRouteButton);
                mEditorViewModel.setTentativePhotoPath(potentialPhotoPath);
            }
        } else if (requestCode == EditorActivity.SELECT_DATE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            String selectedDate = data.getStringExtra("selected_date");
            processDatePickerResults(selectedDate);
        } else if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == Activity.RESULT_OK) {

        }
    }



    ///////////////////////////////////// HELPER METHODS ///////////////////////////////////////////////
    /**
     * Method that initializes all of the Views and sets up different listeners on the Views that it
     * is necessary for
     * */
    private void initViews(View rootView) {
        displayDateTextView = rootView.findViewById(R.id.tv_addactivity_display_date);

        typeOfRouteSpinner = rootView.findViewById(R.id.sp_addactivity_type_of_route);
        typeOfRouteSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                typeOfRoute = i;

                if (typeOfRoute == 0) {
                    ArrayAdapter<String> ratingsAdapter = new ArrayAdapter<String>(getContext(),
                            android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.ratings_boulder));
                    ratingsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    ratingSpinner.setAdapter(ratingsAdapter);
                }

                if (typeOfRoute == 1 || typeOfRoute == 2) {
                    ArrayAdapter<String> ratingsAdapter = new ArrayAdapter<String>(getContext(),
                            android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.ratings_toprope));
                    ratingsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    ratingSpinner.setAdapter(ratingsAdapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayAdapter<String> typesOfRoutesAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.types_of_routes));
        typesOfRoutesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeOfRouteSpinner.setAdapter(typesOfRoutesAdapter);

        ratingSpinner = rootView.findViewById(R.id.sp_addactivity_rating);
        ratingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                rating = convertRating(typeOfRoute, position);

                if (ratingPrev != 0) {
                    ratingSpinner.setSelection(convertRatingBack(ratingPrev));
                    rating = convertRating(typeOfRoute, ratingPrev);
                    ratingPrev = 0;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        picOfRouteButton = rootView.findViewById(R.id.ib_addactivity_pic);
        picOfRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePictureIntent();
            }
        });

        titleEditText = rootView.findViewById(R.id.et_addactivity_title);
        locationEditText = rootView.findViewById(R.id.et_addactivity_location);
        descriptionEditText = rootView.findViewById(R.id.et_addactivity_description);
        whoClimbedEditText = rootView.findViewById(R.id.et_addactivity_who_climbed);
        routeColor = rootView.findViewById(R.id.et_addactivity_route_color);

        selectDateButton = rootView.findViewById(R.id.bu_addactivity_select_date);
        selectDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialogFragment = new DatePickerFragment();
                dialogFragment.setTargetFragment(EditorFragment.this, EditorActivity.SELECT_DATE_REQUEST_CODE);
                dialogFragment.show(getActivity().getSupportFragmentManager(), getString(R.string.date_picker));
            }
        });

        favoriteButton = rootView.findViewById(R.id.ib_addactivity_favorite);
        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (favorited) { // Unfavorited it
                    favorite = 0;
                    favoriteButton.setImageResource(R.drawable.ic_baseline_favorite_border_36);
                    favorited = false;
                } else {
                    favorite = 1;
                    favoriteButton.setImageResource(R.drawable.ic_baseline_favorite_36);
                    favorited = true;
                }
            }
        });

        titleEditText.setOnTouchListener(mTouchListener);
        typeOfRouteSpinner.setOnTouchListener(mTouchListener);
        ratingSpinner.setOnTouchListener(mTouchListener);
        locationEditText.setOnTouchListener(mTouchListener);
        selectDateButton.setOnTouchListener(mTouchListener);
        descriptionEditText.setOnTouchListener(mTouchListener);
        favoriteButton.setOnTouchListener(mTouchListener);
        whoClimbedEditText.setOnTouchListener(mTouchListener);
        routeColor.setOnTouchListener(mTouchListener);
    }

    /**
     * Method that populates all of the EditTexts, Spinners, and TextViews when editing a route to
     * update some of its information
     *
     * @param route is the ClimbingRoute that is being updated and the information is being retrieved
     *              from
     * */
    private void populateUiAndData(ClimbingRoute route) {
        if (route != null) {
            title = route.getTitle();
            location = route.getLocation();
            description = route.getDescription();
            date = route.getDate();
            typeOfRoute = route.getTypeOfRoute();
            ratingPrev = route.getRating();
            photoPath = route.getPhotoPath();
            favorite = route.getFavorite();
            whoClimbed = route.getWhoClimbed();
            color = route.getRouteColor();
        }

        mEditorViewModel.setTentativePhotoPath(photoPath);


        typeOfRouteSpinner.setSelection(typeOfRoute);

        if (title != null) titleEditText.setText(title);
        if (location != null) locationEditText.setText(location);
        if (description != null) descriptionEditText.setText(description);
        if (date != null) displayDateTextView.setText(date);
        if (whoClimbed != null) whoClimbedEditText.setText(whoClimbed);
        if (color != null) routeColor.setText(color);

        if (favorite == 0) {
            favorited = false;
            favoriteButton.setImageResource(R.drawable.ic_baseline_favorite_border_36);
        } else {
            favorited = true;
            favoriteButton.setImageResource(R.drawable.ic_baseline_favorite_36);
        }

        if (photoPath != null) Glide.with(getContext()).load(photoPath).into(picOfRouteButton); //Takes care of setting the ImageButton

    }

    /**
     * Method that saves the route if it is a new one or updates the route if its an old one
     * */
    private void saveRoute() {
        title = titleEditText.getText().toString();
        location = locationEditText.getText().toString();
        description = descriptionEditText.getText().toString();
        whoClimbed = whoClimbedEditText.getText().toString();
        color = routeColor.getText().toString();

        ClimbingRoute route = new ClimbingRoute(title, rating, typeOfRoute, location, description,
                date, photoPath, favorite, whoClimbed, color);

        if (mRouteID == EditorActivity.DEFAULT_TASK_ID) {
            mViewModel.insert(route);
        } else {
            route.setId(mRouteID);
            mViewModel.updateRoute(route);
        }
        Intent intent = new Intent(getActivity(), MainActivity.class);
        NavUtils.navigateUpTo(getActivity(), intent);
    }

    /**
     * Deletes a route
     * */
    private void deleteRoute() {
        AlertDialog.Builder mAlert = new AlertDialog.Builder(getContext());
        mAlert.setTitle("Are you sure?");
        mAlert.setMessage("Are you sure that you want to delete this route?");

        mAlert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mViewModel.deleteRoute(mRouteID);
                Intent intent = new Intent(getActivity(), MainActivity.class);
                NavUtils.navigateUpTo(getActivity(), intent);
            }
        });

        mAlert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        mAlert.show();
    }

    /**
     * Formats the data into a String, which is then assigned to the global variable date
     *
     * @param selectedDate is the selected date from the date picker fragment
     * */
    public void processDatePickerResults(String selectedDate) {
        this.date = selectedDate;
        displayDateTextView.setText(date);
    }

    /**
     * Converts the selected position in the spinner into an int rating based on the type of route
     *
     * @param typeOfRoute is the type of route selected in its respective spinner
     * @param position is the position of the rating spinner
     * */
    private static int convertRating(int typeOfRoute, int position) {
        int ratingInt = 0;

        if (typeOfRoute == 0) { //If typeOfRoute is 0, then its a boulder problem
            ratingInt = position;   //We can simply just assign position to ratingInt b/c position
            // corresponds to the correct rating
        } else if (typeOfRoute == 1 || typeOfRoute == 2) { //Meaning that its a top-rope/lead route
            switch (position) {
                case 0:
                    ratingInt = 560; //5.6
                    break;
                case 1:
                    ratingInt = 570; //5.7
                    break;
                case 2:
                    ratingInt = 580; //5.8
                    break;
                case 3:
                    ratingInt = 590; //5.9
                    break;
                case 4:
                    ratingInt = 510; //5.10
                    break;
                case 5:
                    ratingInt = 511; //5.11
                    break;
                case 6:
                    ratingInt = 512; //5.12
                    break;
                case 7:
                    ratingInt = 513; //5.13
                    break;
                case 8:
                    ratingInt = 514; //5.14
                    break;
                case 9:
                    ratingInt = 515; //5.15
                    break;
                case 10:
                    ratingInt = 516; //5.16
                    break;
            }
        }
        return ratingInt;
    }

    /**
     * Converts the rating pack to a position that the ratings spinner can use. Used when populateUiAndData
     * is called
     * */
    private int convertRatingBack(int rating) {
        int ratingConvertedBack = 0;

        if (rating >= 0 && rating <= 10) {
            ratingConvertedBack = rating;
        } else {
            switch (rating) {
                case 560:
                    ratingConvertedBack = 0;
                    break;
                case 570:
                    ratingConvertedBack = 1;
                    break;
                case 580:
                    ratingConvertedBack = 2;
                    break;
                case 590:
                    ratingConvertedBack = 3;
                    break;
                case 510:
                    ratingConvertedBack = 4;
                    break;
                case 511:
                    ratingConvertedBack = 5;
                    break;
                case 512:
                    ratingConvertedBack = 6;
                    break;
                case 513:
                    ratingConvertedBack = 7;
                    break;
                case 514:
                    ratingConvertedBack = 8;
                    break;
                case 515:
                    ratingConvertedBack = 9;
                    break;
                case 516:
                    break;

            }
        }
        return ratingConvertedBack;
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
                Log.d("EditorFragment", "Error occurred while trying to create file");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getContext(),
                        "com.example.climbinglog.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
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
        return image;
    }
}
