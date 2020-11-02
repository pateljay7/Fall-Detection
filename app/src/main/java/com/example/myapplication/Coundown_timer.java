package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.telephony.SmsManager;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


//import com.example.fall_detection_3.EmergencyContact;
import com.example.fall_detection_3.EmergencyContact;
import com.example.fall_detection_3.ExampleService;
import com.example.fall_detection_3.HomeFragment;
import com.example.fall_detection_3.MainActivity;
import com.example.fall_detection_3.ProfileFragment;
import com.example.fall_detection_3.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.graphics.Color.parseColor;
//import static com.example.fall_detection_3.Samp.getContext;

public class Coundown_timer extends AppCompatActivity implements LocationListener {
    public int counter = 30;
    public int bar_count = 30000;
    public static final String Tid = "id";
    String id;
    private String latituteField;
    private String longitudeField;
    private LocationManager locationManager;
    public String provider;
     CountDownTimer cd;
     String userid;
    DatabaseReference LocationData, datasaveinfo,Dlocation,Dcontact,Demail,datauser,Deme,Deme2;
    float lat = 0;
    float lng = 0;
    String Lid;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coundown_timer);
        ((AppCompatActivity)Coundown_timer.this).getSupportActionBar().setTitle("Alert mode Activate");
        //((AppCompatActivity)Coundown_timer.this).getSupportActionBar().setBackgroundDrawable();

        ProfileFragment p = new ProfileFragment();
        Context c =p.getcontext();
         userid = p.getUpdateId(getApplicationContext());
        datasaveinfo= FirebaseDatabase.getInstance().getReference("info");
        Dlocation=datasaveinfo.child(userid);
       // LocationData.child(userid).child("Locatiobid");

        Dcontact = datasaveinfo.child(userid);
        final TextView timer = (TextView) findViewById(R.id.timer);
        Button ok_state = (Button) findViewById(R.id.ok_state);
        Button confirm_state = (Button) findViewById(R.id.confirm_state);
        final ProgressBar bar = (ProgressBar) findViewById(R.id.progressBar2);

loadData();
        final ObjectAnimator progressamin = ObjectAnimator.ofInt(bar, "progress", 100, 0);

        progressamin.start();

         cd = new CountDownTimer(30000, 1000) {
            public void onTick(long millisUnitlFinished) {
                timer.setText(String.valueOf(counter));
                progressamin.setDuration(bar_count);
                progressamin.setInterpolator(new DecelerateInterpolator());
                counter--;
            }

            @Override
            public void onFinish() {
                timer.setText("0");
                //Intent i = new Intent(Coundown_timer.this, MainActivity.class);
                //startActivity(i);
                Confirm_fallDown();
            }
        }.start();
        ok_state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cd.cancel();
                Intent i = new Intent(Coundown_timer.this, MainActivity.class);
                startActivity(i);

            }
        });
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            //return TODO;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);


        confirm_state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Confirm_fallDown();
            }
        });

    }
    public void Confirm_fallDown()
    {
        // datasaveinfo= FirebaseDatabase.getInstance().getReference("info");
        cd.cancel();
        id = datasaveinfo.push().getKey();
        datasaveinfo.child(id);
        HomeFragment h =new HomeFragment();
        h.share_state=true;
        h.savesharestate(Coundown_timer.this);
        Toast.makeText(Coundown_timer.this,id,Toast.LENGTH_SHORT).show();
        saveData();
        //  loadData();
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, true);
        if (checkSelfPermission(ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(Coundown_timer.this, "not located", Toast.LENGTH_LONG);
            //return;
        }
        Location location = locationManager.getLastKnownLocation(provider);
        if (location == null) {
            Toast.makeText(Coundown_timer.this, "null location", Toast.LENGTH_LONG);
            location = locationManager.getLastKnownLocation(provider);
        }

        // Initialize the location fields
        if (location != null) {
            System.out.println("Provider " + provider + " has been selected.");
            onLocationChanged(location);
        }

        if (location != null) {
            lat = (float) (location.getLatitude());
            lng = (float) (location.getLongitude());

        }

        datauser = datasaveinfo.child(userid);
        datauser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("Contact")) {
                    Dcontact = datasaveinfo.child(userid).child("Contact");
                    Dcontact.addListenerForSingleValueEvent(new ValueEventListener() {
                        int j = 5;
                        int i;

                        public void onDataChange(DataSnapshot dataSnapshot) {

                            for (i = 1; i <=j; i++) {
                                if (dataSnapshot.hasChild(String.valueOf(i))) {
                                    Deme = datasaveinfo.child(userid).child("Contact").child(String.valueOf(i));
                                    Deme.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                            EmergencyContact e = dataSnapshot.getValue(EmergencyContact.class);
                                            String contact =e.getEcontact();
                                            //String name =e.getEname();
                                            //  String message = "Alert From Jay Patel" + "\n" + "Fall Detected..!" + "\n" + "https://maps.google.com/?q=" + lat + "," + lng + "\n\nFrom fall detection application.. ";
                                            String message = "Alert..!!" +"\n" + "Fall Detected..!" + "\n" +"Location id : "+ id+ "\n\nFrom fall detection application.. ";

                                            SmsManager.getDefault().sendTextMessage(contact, null, message, null, null);
                                            Toast.makeText(Coundown_timer.this ,"message send to your emergency contact successfully..!! to "+contact,Toast.LENGTH_LONG).show();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                        }
                                    });

                                }

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(Coundown_timer.this, "Error fetching data", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        startService();

        Intent i = new Intent(Coundown_timer.this, MainActivity.class);
        startActivity(i);
    }
    public String getLocationId(Context c) {
//        Toast.makeText(Coundown_timer.this,"getLocation",Toast.LENGTH_SHORT).show();
        final String SHARED_PREFS = "sharedPrefs";
        SharedPreferences sharedPreferences =c.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        id = sharedPreferences.getString(Tid, "");
        return id;
    }
    public void saveData() {
        final String SHARED_PREFS = "sharedPrefs";
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Tid, id);
        editor.apply();

    }

    public void loadData() {
        final String SHARED_PREFS = "sharedPrefs";
        SharedPreferences sharedPreferences =getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        id = sharedPreferences.getString(Tid, "");
    }

    public void updatView() {
      //  editName.setText(id);

    }
    public void startService() {
       // Toast.makeText(Coundown_timer.this,"started",Toast.LENGTH_SHORT).show();
        Intent serviceIntent = new Intent(this, ExampleService.class);
        ContextCompat.startForegroundService(this, serviceIntent);
    }
    public void stopService(View v) {
        Intent serviceIntent = new Intent(this, ExampleService.class);
        stopService(serviceIntent);
    }

    @Override
    public void onLocationChanged(Location location) {
        float lat = (float) (location.getLatitude());
        float lng = (float) (location.getLongitude());
        latituteField = String.valueOf(lat);
        longitudeField = String.valueOf(lng);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {
        Toast.makeText(this, "Enabled new provider " + provider,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String s) {
        Toast.makeText(this, "Disabled provider " + provider,
                Toast.LENGTH_SHORT).show();
    }
}