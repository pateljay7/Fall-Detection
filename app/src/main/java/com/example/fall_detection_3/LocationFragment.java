package com.example.fall_detection_3;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.github.mikephil.charting.utils.Utils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import static android.content.Context.LOCATION_SERVICE;

public class LocationFragment extends Fragment implements OnMapReadyCallback, LocationListener {
    private MapView mapView;
    private GoogleMap gmap;
    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
    private LocationManager locationManager;
    private String provider;
    private float latituteField, lae, loe;
    private float longitudeField;
    private boolean attache_status = false;
    Button go;
    ImageView image_attache;
    DatabaseReference datasaveinfo, datasaveinfo2;
    String id, lid = "123";
    DatabaseReference rootRef, demoRef1, demoRef2;
    TextView locationid, Tla, Tlo;
    Double la, ln;
    String la1, lo2;
    MarkerOptions markerOptions = new MarkerOptions();
    String userid;

    @SuppressLint("ServiceCast")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_location, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Location");
        ProfileFragment p = new ProfileFragment();
        userid = p.getUpdateId(getContext());

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }
        final FrameLayout attache_layout = (FrameLayout) v.findViewById(R.id.attache_layout);
        attache_layout.setBackgroundResource(R.drawable.notattache_pin);
        go = (Button) v.findViewById(R.id.go);
        image_attache = (ImageView) v.findViewById(R.id.image_attache);
        locationid = (TextView) v.findViewById(R.id.LocationId);
        Tla = (TextView) v.findViewById(R.id.Tla);
        Tlo = (TextView) v.findViewById(R.id.Tlo);
        mapView = v.findViewById(R.id.mapView);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);
        datasaveinfo = FirebaseDatabase.getInstance().getReference("info");
        datasaveinfo2 = FirebaseDatabase.getInstance().getReference("live location");
        id = datasaveinfo.push().getKey();

        image_attache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (attache_status == false) {
                    attache_status = true;
                    attache_layout.setBackgroundResource(R.drawable.attache_pin);
                } else {
                    attache_status = false;
                    attache_layout.setBackgroundResource(R.drawable.notattache_pin);

                }
            }
        });
        go.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        final String locationid2 = locationid.getText().toString();

                                        if (!locationid2.isEmpty()) {
                                            datasaveinfo2.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.hasChild(locationid2)) {
                                                        demoRef1 = datasaveinfo2.child(locationid2).child("la");
                                                        demoRef2 = datasaveinfo2.child(locationid2).child("lo");

                                                        if (demoRef1 != null) {
                                                            demoRef1.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                    //  Toast.makeText(getContext(),"getting data",Toast.LENGTH_LONG).show();
                                                                    String value = dataSnapshot.getValue().toString();
                                                                    Tla.setText(value);
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                    Toast.makeText(getActivity(), "Error fetching data", Toast.LENGTH_LONG).show();
                                                                }
                                                            });
                                                        }
                                if (demoRef2 != null) {
                                    demoRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            String value = dataSnapshot.getValue().toString();
                                            Tlo.setText(value);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            Toast.makeText(getActivity(), "Error fetching data", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                                //=================================================================================================================
                                // ==================================================================================================================
                                mapView.getMapAsync(new OnMapReadyCallback() {
                                    @Override
                                    public void onMapReady(GoogleMap googleMap) {
                                        la1 = Tla.getText().toString();
                                        lo2 = Tlo.getText().toString();

                                        if (!la1.isEmpty()) {
                                            la = Double.parseDouble(la1);
                                            ln = Double.parseDouble(lo2);

                                            LatLng ny1 = new LatLng(la, ln);
                                            MarkerOptions markerOptions1 = new MarkerOptions();
                                            markerOptions1.position(ny1);
                                            BitmapDescriptor i = getBitmapDescriptor(R.drawable.position);
                                            markerOptions1.icon(i);
                                            gmap.addMarker(markerOptions1);
                                            //  gmap.addMarker(new MarkerOptions().position(ny1).title("person"));
                                        }
                                    }
                                });

                            } else {
                                Toast.makeText(getContext(), "user doesn't exist", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                } else {
                    locationid.setError("can't be empty..");
                }
            }
        });

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //return;
        }
        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getContext(), "not located", Toast.LENGTH_LONG);
            // return ;
        }
        Location location = locationManager.getLastKnownLocation(provider);
        //  Location location = getLastKnownLocation();
        if (location != null) {
            System.out.println("Provider " + provider + " has been selected.");
            onLocationChanged(location);
        }
        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }
        mapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;
        gmap.setIndoorEnabled(true);
        UiSettings uiSettings = gmap.getUiSettings();
        uiSettings.setIndoorLevelPickerEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);
        uiSettings.setMapToolbarEnabled(true);
        uiSettings.setCompassEnabled(true);
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setTiltGesturesEnabled(true);
        LatLng ny, ny1;

        if (markerOptions != null) {
            gmap.clear();
        }
        ny = new LatLng(latituteField, longitudeField);
        markerOptions.position(ny);
        BitmapDescriptor i2 = getBitmapDescriptor(R.drawable.pin3);
        markerOptions.icon(i2);
        gmap.addMarker(markerOptions);
        if (attache_status == true)
            gmap.moveCamera(CameraUpdateFactory.newLatLng(ny));

        la1 = Tla.getText().toString();
        lo2 = Tlo.getText().toString();

        if (!la1.isEmpty()) {
            la = Double.parseDouble(la1);
            ln = Double.parseDouble(lo2);

            ny1 = new LatLng(la, ln);
            MarkerOptions markerOptions1 = new MarkerOptions();
            markerOptions1.position(ny1);
            BitmapDescriptor i = getBitmapDescriptor(R.drawable.position);
            markerOptions1.icon(i);
            gmap.addMarker(markerOptions1);
        }

    }

    private BitmapDescriptor getBitmapDescriptor(int id) {

        Drawable vectorDrawable = getContext().getDrawable(id);
        int h = ((int) Utils.convertDpToPixel(30));
        int w = ((int) Utils.convertDpToPixel(25));
        vectorDrawable.setBounds(0, 0, w, h);
        Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bm);
    }

    @Override
    public void onLocationChanged(Location location) {

        float lat = (float) (location.getLatitude());
        float lng = (float) (location.getLongitude());
        latituteField = lat;
        longitudeField = lng;
        database d = new database(latituteField, longitudeField);
        if (userid != null) {
            datasaveinfo.child(userid).child("location").setValue(d);
            if (demoRef1 != null) {
                demoRef1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null) {
                            String value = dataSnapshot.getValue().toString();
                            Tla.setText(value);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getActivity(), "Error fetching data", Toast.LENGTH_LONG).show();
                    }
                });
            }
            if (demoRef2 != null) {
                demoRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null) {
                            String value = dataSnapshot.getValue().toString();
                            Tlo.setText(value);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getActivity(), "Error fetching data", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
        mapView.getMapAsync(this);

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {
        Toast.makeText(getContext(), "Enabled new provider " + provider,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String s) {
        Toast.makeText(getContext(), "disabled new provider " + provider,
                Toast.LENGTH_SHORT).show();

    }

    private Location getLastKnownLocation() {
        LocationManager mLocationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            }
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (l != null)
                onLocationChanged(l);
            if (bestLocation != null)
                onLocationChanged(bestLocation);
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
            }
        }
        return bestLocation;
    }
}
