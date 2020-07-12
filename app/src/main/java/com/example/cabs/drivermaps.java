package com.example.cabs;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Map;

public class drivermaps extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;
    GoogleApiClient googleapiclient;
    Location lastlocation;
    LocationRequest locrequest;
    private Button driverlogout;
    FirebaseAuth mAuth;
    FirebaseUser currentuser;
    String clientid;
    private Button driversetting;
    private boolean driverlogoutstatus=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drivermaps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        driverlogout=(Button)findViewById(R.id.drlogout);
        mAuth=FirebaseAuth.getInstance();
         currentuser=mAuth.getCurrentUser();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

            getclient();

       driverlogout.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               driverlogoutstatus=true;
               disconnectdriver();
               mAuth.signOut();

               logoutact();
           }
       });



    }

    private void getclient()
    {
        String driverid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        System.out.println("misssing");
        DatabaseReference clientref=FirebaseDatabase.getInstance().getReference().child("Users").child("drivers").child("driver_id").child(driverid);
        clientref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    System.out.println("snap here");
                    Map<String,Object> map=(Map<String,Object>)dataSnapshot.getValue();
                    if(map.get("clrideid")!=null)
                    {
                        clientid=map.get("clrideid").toString();
                        System.out.println(clientid);
                    getclientpickloc();
                    }

                }
                else
                {
                    clientid="";
                    if(pickupmarker!=null)
                    {
                        pickupmarker.remove();
                    }
                    if(clientpickloclist!=null) {
                        clientpickloc.removeEventListener(clientpickloclist);
                    }
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    Marker pickupmarker;
    private DatabaseReference clientpickloc;
    private ValueEventListener clientpickloclist;
    private void getclientpickloc() {

        clientpickloc=FirebaseDatabase.getInstance().getReference().child("Client_calls").child(clientid).child("l");
        clientpickloclist=clientpickloc.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && !clientid.equals(""))
                { List<Object> map=(List<Object>)dataSnapshot.getValue();
                    double lat=0;
                    double lon=0;

                    if(map.get(0)!=null)
                    {
                        lat=Double.parseDouble(map.get(0).toString());

                    }
                    if(map.get(1)!=null)
                    {
                        lon=Double.parseDouble(map.get(1).toString());

                    }
                    LatLng driverloc=new LatLng(lat,lon);

                    pickupmarker=mMap.addMarker(new MarkerOptions().position(driverloc).title("Pickup location"));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void logoutact() {
        Intent in=new Intent(drivermaps.this,splashscreen.class);

        startActivity(in);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        buildgoogleapiclient();
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(true);

        }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locrequest = new LocationRequest();
        locrequest.setInterval(3000);
        locrequest.setFastestInterval(3000);
        locrequest.setPriority(locrequest.PRIORITY_HIGH_ACCURACY);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleapiclient, locrequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
              lastlocation=location;
              LatLng latlng = new LatLng(location.getLatitude(),location.getLongitude());
              mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
              mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

              String userid= FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference driveravailablity= FirebaseDatabase.getInstance().getReference().child("drivers available");
        GeoFire geoFire = new GeoFire(driveravailablity);
        geoFire.setLocation(userid,new GeoLocation(location.getLatitude(),location.getLongitude()));
        DatabaseReference dravailref=FirebaseDatabase.getInstance().getReference("drivers available");
        DatabaseReference drworkingref=FirebaseDatabase.getInstance().getReference("working drivers");
        GeoFire dravailfire=new GeoFire(dravailref);
        GeoFire drworkfire=new GeoFire(drworkingref);






                dravailfire.setLocation(userid,new GeoLocation(location.getLatitude(),location.getLongitude()));


                    drworkfire.setLocation(userid,new GeoLocation(location.getLatitude(),location.getLongitude()));



    }
    protected  synchronized  void buildgoogleapiclient()
    {
        googleapiclient=new GoogleApiClient.Builder(this).addConnectionCallbacks(this).
                addOnConnectionFailedListener(this).addApi(LocationServices.API).build();

        googleapiclient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(!driverlogoutstatus)
        {
            disconnectdriver();
        }

    }
    private void disconnectdriver() {
        String userid= FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference driveravailablity= FirebaseDatabase.getInstance().getReference().child("drivers available");

        GeoFire geoFire = new GeoFire(driveravailablity);

        geoFire.removeLocation(userid);
        LocationServices.FusedLocationApi.removeLocationUpdates(googleapiclient,this);
    }

}

