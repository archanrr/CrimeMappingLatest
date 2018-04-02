package com.hackathon.cyberblue.crimemapping;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.AnnotateImageResponse;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.ColorInfo;
import com.google.api.services.vision.v1.model.DominantColorsAnnotation;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.api.services.vision.v1.model.ImageProperties;
import com.google.api.services.vision.v1.model.SafeSearchAnnotation;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import butterknife.ButterKnife;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_OK;

public class BlankFragment extends Fragment implements OnMapReadyCallback,GoogleMap.OnMyLocationButtonClickListener,LocationListener {
    View v;
    GoogleMap mMap;

    private static final String TAG = "MainActivity";



    public DatabaseReference databaseReference;
    FloatingActionButton fab, fab1;
    DatabaseReference ref;
    public String s, t;
    String userLocation;
    int cnt = 1;
    LocationManager locationManager;

    private static final int RECORD_REQUEST_CODE = 101;
    private static final int CAMERA_REQUEST_CODE = 102;
    private static final String CLOUD_VISION_API_KEY = "AIzaSyAV7sY_PPBc6AGnYhDlXJLtygLQ5tYWbGY";
    final Map<String,String> m1= new HashMap<String,String>();
    private int score = 0;
    private int avgscore = 0;
    Feature feature;
    Bitmap bitmap;
    String[] visionAPI = new String[]{"LANDMARK_DETECTION", "LOGO_DETECTION", "SAFE_SEARCH_DETECTION", "IMAGE_PROPERTIES", "LABEL_DETECTION"};
    String api = visionAPI[0];

    public List<String> a1 = new ArrayList();
    final DatabaseReference d1 =
            FirebaseDatabase.getInstance().
                    getReference("SOS");
    String pid = d1.push().getKey();
    byte[] imageBytes;



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

                try
                {
                    getLocation();
                }
                catch (Exception e)
                {
                    Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                }

                try
                {
                    random = new Random(); //put this function in onCreate
                    startRecording();

                }
                catch (Exception e)
                {
                    Toast.makeText(getContext(),e.toString(), Toast.LENGTH_SHORT).show();
                }
                try
                {
                    VisionApi();
                }
                catch (Exception e)
                {
                    Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
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
                try {
                    String lat = dataSnapshot.child("location").child("latitude").getValue().toString();
                    String lon = dataSnapshot.child("location").child("longitude").getValue().toString();
                    double latitude = Double.valueOf(lat);
                    double longitude = Double.valueOf(lon);
                    LatLng latLng = new LatLng(latitude, longitude);
                    mMap.addMarker(new MarkerOptions().position(latLng).title("TITLE"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 8));
                }
                catch (Exception e){
                    Log.d("Except",e.toString());
                }

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
            location l=new location(location.getLatitude(),location.getLongitude());
            d1.child(pid).child("location").setValue(l);
            Toast.makeText(getContext(),s.toString()+","+t.toString(), Toast.LENGTH_SHORT).show();


        } catch (Exception e) {
            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
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

    int i=0;
    public void sendMessage() {

// ...
        ref = FirebaseDatabase.getInstance().getReference("userDetails");
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {

                String phNo = dataSnapshot.child("phoneno").getValue().toString();

                SmsManager smsManager = SmsManager.getDefault();
               // Toast.makeText(getContext(), "sms", Toast.LENGTH_SHORT).show();

                if(i!=2) {
                    smsManager.sendTextMessage(phNo, null, "Hello, contact  me soon. My current location is :\n Latitude:" + s + "\nLongitude:" + t + "\n" + userLocation, null, null);
                    Toast.makeText(getContext(), "SMS sent." + phNo, Toast.LENGTH_LONG).show();

                    // A new comment has been added, add it to the displayed list
                }

                i++;
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
                        //saveInFirebase();
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
            Log.i("~~~~~~~~ Encoded: ", encoded);
            // text.setText(encoded);

            post p1 = new post(encoded);
            if(d1.child(pid).child("audio").setValue(p1).isSuccessful())
                Toast.makeText(getContext(),"Audio added",Toast.LENGTH_SHORT).show();

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

    /*@Override
    public void onRequestPermissionsResult(int requestCode,@NonNull String permissions[], int[] grantResults) {
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
*/
    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getContext(),
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }
    public  void VisionApi()
    {


        ButterKnife.bind(getActivity());

        feature = new Feature();
        feature.setType(visionAPI[0]);
        feature.setMaxResults(10);

        takePictureFromCamera();

    }
    public void onResume() {
        super.onResume();
        if (checkPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
           // takePicture.setVisibility(View.VISIBLE);
        } else {
           // takePicture.setVisibility(View.INVISIBLE);
            makeRequest(Manifest.permission.CAMERA);
        }
    }

    private int checkPermission(String permission) {
        return ContextCompat.checkSelfPermission(getActivity(), permission);
    }

    private void makeRequest(String permission) {
        ActivityCompat.requestPermissions(getActivity(), new String[]{permission}, RECORD_REQUEST_CODE);
    }

    public void takePictureFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {


            bitmap = (Bitmap) data.getExtras().get("data");
          //  imageView.setImageBitmap(bitmap);

            callCloudVision(bitmap, feature);


            String encodedImage = Base64.encodeToString(imageBytes, Base64.NO_WRAP);


            post p1 = new post(encodedImage);
            if(d1.child(pid).setValue(p1).isSuccessful())
                Toast.makeText(getContext(),"Image added",Toast.LENGTH_SHORT).show();

            d1.child(pid).child("Status").setValue("New");
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == RECORD_REQUEST_CODE) {
            if (grantResults.length == 0 && grantResults[0] == PackageManager.PERMISSION_DENIED
                    && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                     //finish();
            } else {
                //takePicture.setVisibility(View.VISIBLE);
            }
        }
    }

    private void callCloudVision(final Bitmap bitmap, final Feature feature) {
        //imageUploadProgress.setVisibility(View.VISIBLE);
        final List<Feature> featureList = new ArrayList<>();
        featureList.add(feature);

        final List<AnnotateImageRequest> annotateImageRequests = new ArrayList<>();

        AnnotateImageRequest annotateImageReq = new AnnotateImageRequest();
        annotateImageReq.setFeatures(featureList);
        annotateImageReq.setImage(getImageEncodeImage(bitmap));
        annotateImageRequests.add(annotateImageReq);


        new AsyncTask<Object, Void, String>() {
            @Override
            protected String doInBackground(Object... params) {
                try {

                    HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
                    GsonFactory jsonFactory = GsonFactory.getDefaultInstance();

                    VisionRequestInitializer requestInitializer = new VisionRequestInitializer(CLOUD_VISION_API_KEY);

                    Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
                    builder.setVisionRequestInitializer(requestInitializer);

                    Vision vision = builder.build();

                    BatchAnnotateImagesRequest batchAnnotateImagesRequest = new BatchAnnotateImagesRequest();
                    batchAnnotateImagesRequest.setRequests(annotateImageRequests);

                    Vision.Images.Annotate annotateRequest = vision.images().annotate(batchAnnotateImagesRequest);
                    annotateRequest.setDisableGZipContent(true);
                    BatchAnnotateImagesResponse response = annotateRequest.execute();
                    return convertResponseToString(response);
                } catch (GoogleJsonResponseException e) {
                    Log.d(TAG, "failed to make API request because " + e.getContent());
                } catch (IOException e) {
                    Log.d(TAG, "failed to make API request because of other IOException " + e.getMessage());
                }
                return "Cloud Vision API request failed. Check logs for details.";
            }

            protected void onPostExecute(String result) {
                //visionAPIData.setText(result);
                //imageUploadProgress.setVisibility(View.INVISIBLE);

                int i = Arrays.asList(visionAPI).indexOf(api);
                if(i!=4) {
                    api = visionAPI[i + 1];
                    feature.setType(api);
                    feature.setMaxResults(10);
                    callCloudVision(bitmap, feature);
                }
            }
        }.execute();
    }

    @NonNull
    private Image getImageEncodeImage(Bitmap bitmap) {
        Image base64EncodedImage = new Image();
        // Convert the bitmap to a JPEG
        // Just in case it's a format that Android understands but Cloud Vision
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
        imageBytes = byteArrayOutputStream.toByteArray();

        // Base64 encode the JPEG
        base64EncodedImage.encodeContent(imageBytes);
        return base64EncodedImage;
    }

    private String convertResponseToString(BatchAnnotateImagesResponse response) {

        AnnotateImageResponse imageResponses = response.getResponses().get(0);

        List<EntityAnnotation> entityAnnotations;

        String message = "";
        switch (api) {
            case "LANDMARK_DETECTION":
                entityAnnotations = imageResponses.getLandmarkAnnotations();
                message = formatAnnotation(entityAnnotations);
                break;
            case "LOGO_DETECTION":
                entityAnnotations = imageResponses.getLogoAnnotations();
                message = formatAnnotation(entityAnnotations);
                break;
            case "SAFE_SEARCH_DETECTION":
                SafeSearchAnnotation annotation = imageResponses.getSafeSearchAnnotation();
                message = getImageAnnotation(annotation);
                break;
            case "IMAGE_PROPERTIES":
                ImageProperties imageProperties = imageResponses.getImagePropertiesAnnotation();
                message = getImageProperty(imageProperties);
                break;
            case "LABEL_DETECTION":
                entityAnnotations = imageResponses.getLabelAnnotations();
                message = formatAnnotation(entityAnnotations);
                break;

        }

        m1.put(api,message);

        Log.d("msg",m1.get(api));

        String str = m1.get(api);

        if (d1.child(pid).child(api).setValue(m1.get(api)).isSuccessful())
            Toast.makeText(getContext(), "DATA added", Toast.LENGTH_SHORT).show();


        String replaceString = str.replace("\n"," ");



/*
       String regx = "[a-zA-Z]+";
       Pattern pattern = Pattern.compile(regx,Pattern.CASE_INSENSITIVE);
       Matcher matcher = pattern.matcher(replaceString);
        String myString = "";
        myString = replaceString.match("/[A-Z_]+$/")[0];
        if (matcher.find())
        {
            finalString = matcher.group(1);
        }
 */
        replaceString = replaceString.replaceAll("(\\\\n)+"," ")
                .replaceAll("[0-9]\\.?", "");
        Log.d("finalstr",replaceString);

     /*   String text = "\"OR\\n\\nThe Central Site Engineering\\u2019s \\u201cfrontend\\u201d, where developers turn to\"";

        text = text.replaceAll("(\\\\n)+"," ")
                .replaceAll("\\\\u[0-9A-Ha-h]{4}", "");
        Log.d("text",text);*/
        if (d1.child(pid).child(api+"_EDITED").setValue(replaceString).isSuccessful())
            Toast.makeText(getContext(), "DATA added", Toast.LENGTH_SHORT).show();


        return message;
    }

    private String getImageAnnotation(SafeSearchAnnotation annotation) {

        String[] res = {"","","",""};
        res[0] = annotation.getAdult().toString();
        Log.d("adult",res[0]);
        res[1] = annotation.getMedical().toString();
        res[2] = annotation.getSpoof().toString();
        res[3] = annotation.getViolence().toString();

        for(String t : res)
        {
            switch(t)
            {
                case "VERY_UNLIKELY":
                    score+=10;
                    break;
                case "UNLIKELY":
                    score+=20;
                    break;
                case "POSSIBLE":
                    score+=30;
                    break;
                case "LIKELY":
                    score+=40;
                    break;
                case "VERY_LIKELY":
                    score+=50;
                    break;
            }
        }



        if (d1.child(pid).child("Safe_Score").setValue(score).isSuccessful())
            Toast.makeText(getContext(), "DATA added", Toast.LENGTH_SHORT).show();

        avgscore = score/4;
        if(avgscore<=10) {
            if (d1.child(pid).child("Risk").setValue("Low").isSuccessful())
                Toast.makeText(getContext(), "DATA added", Toast.LENGTH_SHORT).show();
        }
        else if(avgscore<=15) {
            if (d1.child(pid).child("Risk").setValue("Moderate").isSuccessful())
                Toast.makeText(getContext(), "DATA added", Toast.LENGTH_SHORT).show();
        }
        else
        {
            if (d1.child(pid).child("Risk").setValue("High").isSuccessful())
                Toast.makeText(getContext(), "DATA added", Toast.LENGTH_SHORT).show();
        }
        saveInFirebase();
        Log.d("score", String.format("%d",score));
        Log.d("avgscore", String.format("%d",avgscore));

        return String.format("adult: %s\nmedical: %s\nspoofed: %s\nviolence: %s\n",
                annotation.getAdult(),
                annotation.getMedical(),
                annotation.getSpoof(),
                annotation.getViolence());
    }

    private String getImageProperty(ImageProperties imageProperties) {
        String message = "";
        DominantColorsAnnotation colors = imageProperties.getDominantColors();
        for (ColorInfo color : colors.getColors()) {
            message = message + "" + color.getPixelFraction() + " - " + color.getColor().getRed() + " - " + color.getColor().getGreen() + " - " + color.getColor().getBlue();
            message = message + "\n";
        }
        return message;
    }

    private String formatAnnotation(List<EntityAnnotation> entityAnnotation) {
        String message = "";

        if (entityAnnotation != null) {
            for (EntityAnnotation entity : entityAnnotation) {
                message = message + "    " + entity.getDescription() + " " + entity.getScore();
                message += "\n";
            }
        } else {
            message = "Nothing Found";
        }
        return message;
    }

}
