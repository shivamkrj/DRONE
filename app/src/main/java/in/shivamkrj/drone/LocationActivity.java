package in.shivamkrj.drone;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class LocationActivity extends AppCompatActivity {

    String emailid, username;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference userReference;
    DatabaseReference adminNotify;
    TextView latitude;
    TextView longitude;
    TextView altitude;
    Button findLocation;
    Button sendLocation;

    boolean isDonating = false;
    Button chat;
    FusedLocationProviderClient client;
    LocationCallback callback;
    String lat, lon, alt, accuracy;
    ProgressDialog progressDialog;
    boolean flag,foundFlag;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        FirebaseApp.initializeApp(this);
//        emailid = getIntent().getStringExtra("EMAIL");
        emailid = Contact.username;
//        username = emailid.replaceAll(".","k");
//        emailid = "cse.shivamkr@gmail.com";
        findUserName();
        firebaseDatabase = FirebaseReference.getDatabaseInstance();
        userReference = firebaseDatabase.getReference(username);
        adminNotify = firebaseDatabase.getReference("USERS");

        isDonating = !getIntent().getBooleanExtra("isNeed",false);


        latitude = findViewById(R.id.latitude);
        longitude = findViewById(R.id.longitude);
        altitude = findViewById(R.id.altitude);
        findLocation = findViewById(R.id.findButton);
        sendLocation = findViewById(R.id.send);
        chat = findViewById(R.id.Chat);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching Location ...");
        progressDialog.setCanceledOnTouchOutside(false);

        flag = false;
        findLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fetchLocation()) {
                    latitude.setText(lat);
                    longitude.setText(lon);
//                    altitude.setText(alt);
                    Log.d("zzzAccuracy", accuracy + "");
                }
//                progressDialog.dismiss();
            }
        });
        sendLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag) {
                    uploadLocation();
                } else
                    Toast.makeText(LocationActivity.this, "Fetch Location", Toast.LENGTH_SHORT).show();
//                chat.setVisibility(View.VISIBLE);
            }
        });
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startChat();
            }
        });
    }

    private void startChat() {
//        chat.setVisibility(View.GONE);
        Intent intent = new Intent(this,ChatActivity.class);
        intent.putExtra("USERNAME",username);
        startActivity(intent);
    }

    private void findUserName() {
        username = "";
        for (int i = 0; i < emailid.length(); i++) {
            char c = emailid.charAt(i);
            if (c == '.' || c == '@') {
                username += 'k';
            } else
                username += c;
        }
    }

    private void uploadLocation() {
        Log.d("zzzusername:",username);
        lat = latitude.getText().toString();
        lon = longitude.getText().toString();
        alt = altitude.getText().toString();
        EditText nameEditText = findViewById(R.id.editText3);
        String name = nameEditText.getText().toString();
        final UserData userData = new UserData();
        userData.name = name;
        nameEditText = findViewById(R.id.editText4);
        name = nameEditText.getText().toString();
        userData.address = name;
        userData.items = alt;
        userData.latitude = lat;
        userData.longitude = lon;
        userData.username = emailid;
        userData.isDonating = isDonating;

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        String t = formatter.format(date);
        userData.time = t;
//        System.out.println(formatter.format(date));
        if(username==null||username.length()<2){
            Log.d("zzzusername",username+" -null");
            return;
        }
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Uploading your request...");
        progressDialog.show();
        adminNotify.child(username).setValue(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.cancel();
                Toast.makeText(LocationActivity.this,"Your request has been successfully submitted",Toast.LENGTH_LONG).show();
                onBackPressed();
            }
        });
    }

    private boolean fetchLocation() {

        foundFlag = false;
        if(!checkLocationPermission()) return false;
        final LocationRequest request = new LocationRequest();
        request.setInterval(5000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setFastestInterval(3000);
        Log.d("zzzlocation", "130");
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(request);
        builder.setAlwaysShow(true);
        SettingsClient client2 = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client2.checkLocationSettings(builder.build());
        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...
                foundFlag = false;
                client = LocationServices.getFusedLocationProviderClient(LocationActivity.this);
                callback = new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        if (locationResult != null) {

                            List<Location> locations = locationResult.getLocations();
                            for (Location location : locations) {
                                lat = location.getLatitude() + "";
                                lon = location.getLongitude() + "";
                                alt = location.getAltitude() + "";
                                if(!foundFlag){
                                    latitude.setText(lat);
                                    longitude.setText(lon);
//                                    altitude.setText(alt);
                                    flag = true;
                                    foundFlag=true;
                                }

                                Log.d("zzz", lat + "  " + lon);
                                progressDialog.dismiss();
                            }
                        }
                        super.onLocationResult(locationResult);
                    }
                };
                if (ActivityCompat.checkSelfPermission(LocationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(LocationActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(LocationActivity.this,"Requires Permission Request1",Toast.LENGTH_SHORT).show();
                    return;
                }
                client.requestLocationUpdates(request, callback, null);
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LocationActivity.this,"Requires Permission Request Failure",Toast.LENGTH_SHORT).show();

                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(LocationActivity.this,
                                1);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });


//        progressDialog.show();
//        client = LocationServices.getFusedLocationProviderClient(this);
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            Toast.makeText(this,"Requires Permission",Toast.LENGTH_SHORT).show();
//            progressDialog.dismiss();
//            return false;
//        }
//        client.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
//            @Override
//            public void onSuccess(Location location) {
//                if (location != null) {
//                    lat = location.getLatitude() + "";
//                    lon = location.getLongitude() + "";
//                    alt = location.getAltitude()+"";
//                    accuracy = location.getAccuracy()+"";
//                    latitude.setText(lat);
//                    longitude.setText(lon);
//                    altitude.setText(alt);
//                }
//                progressDialog.dismiss();
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(LocationActivity.this,"Failure",Toast.LENGTH_SHORT).show();
//                progressDialog.dismiss();
//            }
//        });
        return true;
    }

    private boolean checkLocationPermission() {
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                    Manifest.permission.ACCESS_FINE_LOCATION)) {
            {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(LocationActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                                if (fetchLocation()) {
                                    latitude.setText(lat);
                                    longitude.setText(lon);
                                    altitude.setText(alt);
                                    Log.d("zzzAccuracy", accuracy + "");
                                }
                            }
                        })
                        .create()
                        .show();


            }
            return false;
        }
        else
            return true;
    }
}
