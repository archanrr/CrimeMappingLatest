package com.hackathon.cyberblue.crimemapping;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
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

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.ContentValues.TAG;
import static android.content.Context.LOCATION_SERVICE;

public class BlankFragment extends Fragment implements OnMapReadyCallback,GoogleMap.OnMyLocationButtonClickListener,LocationListener {
    View v;
    GoogleMap mMap;
    public DatabaseReference databaseReference;
    FloatingActionButton fab, fab1;
    DatabaseReference ref;
    public String s, t;
    String userLocation;
    int cnt = 1;
    LocationManager locationManager;
    public List<String> a1 = new ArrayList();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_blank, container, false);
        fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab1 = (FloatingActionButton) v.findViewById(R.id.sos);
        SupportMapFragment smp = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        smp.getMapAsync(this);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        databaseReference = FirebaseDatabase.getInstance().getReference("Crime");

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), Main2Activity.class);
                startActivity(intent);
            }
        });
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               // getLocation();
                try
                {
                    random = new Random(); //put this function in onCreate
                    startRecording();

                }
                catch (Exception e)
                {
                    Toast.makeText(getContext(),e.toString(), Toast.LENGTH_SHORT).show();
                }
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
        } else

        {
            // Toast.makeText(getContext(),"bye",Toast.LENGTH_SHORT).show();
        }
        mMap.setMyLocationEnabled(true);
        LatLng l = new LatLng(22.79851, 75.85638);
        mMap.addMarker(new MarkerOptions().position(l).title("Current Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(l, 8));

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
                String lat = dataSnapshot.child("location").child("latitude").getValue().toString();
                String lon = dataSnapshot.child("location").child("longitude").getValue().toString();
                double latitude = Double.valueOf(lat);
                double longitude = Double.valueOf(lon);
                LatLng latLng = new LatLng(latitude, longitude);
                mMap.addMarker(new MarkerOptions().position(latLng).title("TITLE"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 8));


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                String lat = dataSnapshot.child("location").child("latitude").getValue().toString();
                String lon = dataSnapshot.child("location").child("longitude").getValue().toString();
                double latitude = Double.valueOf(lat);
                double longitude = Double.valueOf(lon);
                LatLng latLng = new LatLng(latitude, longitude);
                mMap.addMarker(new MarkerOptions().position(latLng).title("TITLE"));

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                String lat = dataSnapshot.child("location").child("latitude").getValue().toString();
                String lon = dataSnapshot.child("location").child("longitude").getValue().toString();
                double latitude = Double.valueOf(lat);
                double longitude = Double.valueOf(lon);
                LatLng latLng = new LatLng(latitude, longitude);
                mMap.addMarker(new MarkerOptions().position(latLng).title("TITLE"));

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                String lat = dataSnapshot.child("location").child("latitude").getValue().toString();
                String lon = dataSnapshot.child("location").child("longitude").getValue().toString();
                double latitude = Double.valueOf(lat);
                double longitude = Double.valueOf(lon);
                LatLng latLng = new LatLng(latitude, longitude);
                mMap.addMarker(new MarkerOptions().position(latLng).title("TITLE"));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }



  /*  @Override
    public void onLocationChanged(Location location)
    {
        if( mListener != null )
        {
            mListener.onLocationChanged( location );

            //Move the camera to the user's location and zoom in!
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 12.0f));
        }
    }*/


    void getLocation() {
        try {
            locationManager = (LocationManager) getActivity().getSystemService(getContext().LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 5, (LocationListener) this);

        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        //tv1.setText("Latitude: " + location.getLatitude() + "\n Longitude: " + location.getLongitude());

        s = Double.toString(location.getLatitude());
        t = Double.toString(location.getLongitude());
        cnt = 2;

        //tv1.setText(Double.toString(location.getLatitude()));
        //tv2.setText(Double.toString(location.getLongitude()));
        try {
            Toast.makeText(getContext(), "HEllo location changed", Toast.LENGTH_SHORT).show();
            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            userLocation = addresses.get(0).getAddressLine(0);
            //textView.setText("Your current address is: " +"\n"+addresses.get(0).getAddressLine(0));
            sendMessage();
        } catch (Exception e) {

        }

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(getContext(), "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    public void sendMessage() {
// ...
        ref = FirebaseDatabase.getInstance().getReference("userDetails");
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {

                String phNo = dataSnapshot.child("phoneno").getValue().toString();

                SmsManager smsManager = SmsManager.getDefault();

                smsManager.sendTextMessage(phNo, null, "Hello, contact  me soon. My current location is :\n Latitude:" + s + "\nLongitude:" + t + "\n" + userLocation, null, null);
                Toast.makeText(getContext(), "SMS sent." + phNo, Toast.LENGTH_LONG).show();

                // A new comment has been added, add it to the displayed list


                // ...
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                //Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so displayed the changed comment.
                //Comment newComment = dataSnapshot.getValue(Comment.class);
                String commentKey = dataSnapshot.getKey();

                // ...
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                //Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so remove it.
                String commentKey = dataSnapshot.getKey();

                // ...
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                //Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

                // A comment has changed position, use the key to determine if we are
                // displaying this comment and if so move it.
                //Comment movedComment = dataSnapshot.getValue(Comment.class);
                String commentKey = dataSnapshot.getKey();

                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Log.w(TAG, "postComments:onCancelled", databaseError.toException());
                //Toast.makeText(mContext, "Failed to load comments.",
                //      Toast.LENGTH_SHORT).show();
            }
        };
        ref.addChildEventListener(childEventListener);

    }
    String AudioSavePathInDevice = null;
    MediaRecorder mediaRecorder ;
    Random random ;
    String RandomAudioFileName = "ABCDEFGHIJKLMNOP";
    public static final int RequestPermissionCode = 1;

    public void startRecording(){

        if (checkPermission()) {

            AudioSavePathInDevice =
                    Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                            "AudioRecording.3gp";

            MediaRecorderReady();

            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
                new CountDownTimer(15000, 1000) {

                    public void onTick(long millisUntilFinished) {
                        // mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
                    }


                    public void onFinish() {
                        // mTextField.setText("done!");
                        stopRecording();
                        saveInFirebase();
                    }
                }.start();
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            //buttonStart.setEnabled(false);
            Toast.makeText(getContext(), "Recording started",
                    Toast.LENGTH_LONG).show();
        } else {
            requestPermission();
        }
    }
    public void MediaRecorderReady(){
        mediaRecorder=new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(AudioSavePathInDevice);
    }

    public void stopRecording(){
        mediaRecorder.stop();
        // buttonStart.setEnabled(true);

        Toast.makeText(getActivity(), "Recording Completed",
                Toast.LENGTH_LONG).show();
    }

    public void saveInFirebase() {
        // buttonStart.setEnabled(false);

        try {
            File file = new File(AudioSavePathInDevice);
            InputStream targetInputStream = new FileInputStream(file);
            byte[] bytes = IOUtils.toByteArray(targetInputStream);

            String encoded = Base64.encodeToString(bytes, 0);
            Toast.makeText(getContext(), encoded, Toast.LENGTH_LONG).show();
            Log.i("~~~~~~~~ Encoded: ", encoded);
            // text.setText(encoded);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String CreateRandomAudioFileName(int string){
        StringBuilder stringBuilder = new StringBuilder( string );
        int i = 0 ;
        while(i < string ) {
            stringBuilder.append(RandomAudioFileName.
                    charAt(random.nextInt(RandomAudioFileName.length())));

            i++ ;
        }
        return stringBuilder.toString();
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(getActivity(), new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length> 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(getContext(), "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getContext(),"Permission Denied",Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getContext(),
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }
}