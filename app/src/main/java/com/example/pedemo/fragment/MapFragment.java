package com.example.pedemo.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.pedemo.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private View mapView;
    private float lastX, lastY;
    private boolean isRightClickDragging = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        // Initialize map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
            mapView = mapFragment.getView();
        }

        return view;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Enable basic UI settings
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

        // Add initial marker (Vietnam)
        LatLng vietnam = new LatLng(14.0583, 108.2772);
        mMap.addMarker(new MarkerOptions()
                .position(vietnam)
                .title("Vietnam"));

        // Set up mouse event handling
        if (mapView != null) {
            mapView.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Check if it's a right click (BUTTON_SECONDARY)
                        if (event.getButtonState() == MotionEvent.BUTTON_SECONDARY) {
                            lastX = event.getX();
                            lastY = event.getY();
                            isRightClickDragging = true;
                            return true;
                        }
                        break;

                    case MotionEvent.ACTION_MOVE:
                        if (isRightClickDragging) {
                            float dx = event.getX() - lastX;
                            float dy = event.getY() - lastY;

                            // Move the map based on the drag distance
                            mMap.animateCamera(com.google.android.gms.maps.CameraUpdateFactory.scrollBy(-dx, -dy));

                            lastX = event.getX();
                            lastY = event.getY();
                            return true;
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        isRightClickDragging = false;
                        break;
                }

                // Let the map handle other touch events
                return false;
            });
        }

        try {
            mMap.setMyLocationEnabled(true);
        } catch (SecurityException e) {
            Toast.makeText(getContext(), "Location permission not granted", Toast.LENGTH_SHORT).show();
        }
    }
}