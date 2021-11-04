package com.example.climbinglog.routefragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.climbinglog.EditorActivity.EditorActivity;
import com.example.climbinglog.R;
import com.example.climbinglog.RouteAdapter;
import com.example.climbinglog.RouteViewModel;
import com.example.climbinglog.database.ClimbingRoute;

import java.util.List;

public class AllTypesFragment extends Fragment implements RouteAdapter.ItemClickListener {

    private RouteViewModel mViewModel;
    private RouteAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_route_type, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.climbing_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        mAdapter = new RouteAdapter(getActivity(), this);
        recyclerView.setAdapter(mAdapter);

        mViewModel = new ViewModelProvider(requireActivity()).get(RouteViewModel.class);
        mViewModel.getRoutes().observe(getActivity(), new Observer<List<ClimbingRoute>>() {
            @Override
            public void onChanged(List<ClimbingRoute> climbingRoutes) {
                if (mAdapter != null) {
                    mAdapter.setRoutes(climbingRoutes);
                }
            }
        });

        return rootView;
    }

    @Override
    public void onItemClickListener(int itemId) {
        Intent intent = new Intent(getActivity(), EditorActivity.class);
        intent.putExtra(EditorActivity.UPDATE_ROUTE_ID, itemId);
        startActivity(intent);
    }

}
