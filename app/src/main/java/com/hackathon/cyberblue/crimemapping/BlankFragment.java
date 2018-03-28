package com.hackathon.cyberblue.crimemapping;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.content.ContentValues.TAG;

public class BlankFragment extends Fragment implements OnMapReadyCallback,GoogleMap.OnMyLocationButtonClickListener{
    View v;
    GoogleMap mMap;
    public DatabaseReference databaseReference;
    FloatingActionButton fab;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_blank,container,false);
        fab = (FloatingActionButton)v.findViewById(R.id.fab);
        SupportMapFragment smp = (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map);
        smp.getMapAsync(this);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        databaseReference= FirebaseDatabase.getInstance().getReference("Crime");
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),Main2Activity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            //Toast.makeText(getContext(),"hi",Toast.LENGTH_SHORT).show();
            return;
        }
        else

        {
           // Toast.makeText(getContext(),"bye",Toast.LENGTH_SHORT).show();
        }
        mMap.setMyLocationEnabled(true);
        // mMap.setOnMyLocationButtonClickListener((GoogleMap.OnMyLocationButtonClickListener) this);
        // mMap.setOnMyLocationClickListener((OnMyLocationClickListener) this);
        /*mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMap.clear();

                //Do what you want on obtained latLng
                Toast.makeText(getContext(),String.valueOf(latLng), Toast.LENGTH_SHORT).show();
                mMap.addMarker(new MarkerOptions().position(latLng).title("TITLE"));

            }
        });*/
    }

    @Override
    public void onStart() {
        super.onStart();
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.hasChild("location")) {
                    for (DataSnapshot poly : dataSnapshot.child("location").getChildren()) {
                        String id=String.valueOf(poly.child("latitude").getValue());
                        String name=String.valueOf(poly.child("longitude").getValue());
                        double lat=Double.parseDouble(id);
                        double lon=Double.parseDouble(name);
                        LatLng latLng=new LatLng(lat,lon);
                        Toast.makeText(getContext(),id+name, Toast.LENGTH_SHORT).show();
                        mMap.addMarker(new MarkerOptions().position(latLng).title("TITLE"));



                    }
                }
                else
                {
                    Toast.makeText(getContext(), "hiiiiiiiiiiiiiiii", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.hasChild("location")) {
                    for (DataSnapshot poly : dataSnapshot.child("location").getChildren()) {
                        String id=String.valueOf(poly.child("latitude").getValue());
                        String name=String.valueOf(poly.child("longitude").getValue());
                        double lat=Double.parseDouble(id);
                        double lon=Double.parseDouble(name);
                        LatLng latLng=new LatLng(lat,lon);
                        Toast.makeText(getContext(),id+name, Toast.LENGTH_SHORT).show();
                        mMap.addMarker(new MarkerOptions().position(latLng).title("TITLE"));



                    }
                }
                else
                {
                    Toast.makeText(getContext(), "hiiiiiiiiiiiiiiii", Toast.LENGTH_SHORT).show();
                }
}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}