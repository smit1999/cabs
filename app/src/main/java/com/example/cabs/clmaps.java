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
import android.provider.Telephony;
import android.view.View;
import android.widget.Button;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
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
import com.google.firebase.auth.FirebaseAuthWebException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

public class clmaps extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;
    GoogleApiClient googleapiclient;
    Location lastlocation;
    LocationRequest locrequest;
    int radius=1;
    LatLng clientpickloc;
    Boolean driverfound=false;
    String drfoundid;
    private Button clientlogout;
    private Button clientcall;
    FirebaseAuth mAuth;
    FirebaseUser currentuser;
    String clientid;
    Boolean reqcancel=false;
    DatabaseReference clbdref,driverref;
    private Marker pickupmarker;
    private boolean cllogoutstatus=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clmaps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        clientlogout=(Button)findViewById(R.id.cllogout);
        clientcall=(Button)findViewById(R.id.clcall);
        mAuth=FirebaseAuth.getInstance();
        currentuser=mAuth.getCurrentUser();


        clientid=FirebaseAuth.getInstance().getCurrentUser().getUid();
         clbdref=FirebaseDatabase.getInstance().getReference().child("Client_calls");
         driverref=FirebaseDatabase.getInstance().getReference().child("drivers available");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        clientcall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               if(reqcancel)
               {
                   reqcancel=false;
                geoQuery.removeAllListeners();
                driverlocationref.removeEventListener(drlocreflistener);
                   if(drfoundid!=null)
                   {
                       DatabaseReference driverride=FirebaseDatabase.getInstance().getReference().child("Users").child("drivers").child("driver_id").child(drfoundid);
                       driverride.setValue(true);
                       drfoundid=null;
                   }
                   driverfound=false;
                   radius=1;

                String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference dbref=FirebaseDatabase.getInstance().getReference("Client_calls");
                GeoFire geofire=new GeoFire(dbref);
                geofire.removeLocation(uid);
                if(pickupmarker!=null)
                {
                    pickupmarker.remove();
                }
                clientcall.setText("Call Cabs");

               }
               else {
                   reqcancel=true;

                   GeoFire geoFire = new GeoFire(clbdref);
                   geoFire.setLocation(clientid, new GeoLocation(lastlocation.getLatitude(), lastlocation.getLongitude()));
                   clientpickloc = new LatLng(lastlocation.getLatitude(), lastlocation.getLongitude());
                  pickupmarker= mMap.addMarker(new MarkerOptions().position(clientpickloc).title("YOUR PICKUP LOCATION"));

                   clientcall.setText("SEARCHING FOR DRIVERS");
                   getdrivers();
               }
            }
        });

        clientlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                logoutact();
            }
        });

    }
GeoQuery geoQuery;
    private void getdrivers() {
        GeoFire geoFire=new GeoFire(driverref);
        GeoQuery geoQuery=geoFire.queryAtLocation(new GeoLocation(clientpickloc.latitude,clientpickloc.longitude),radius);
        geoQuery.removeAllListeners();
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
    if(!driverfound && reqcancel)
    {
        driverfound=true;
        drfoundid=key;
        DatabaseReference driverride=FirebaseDatabase.getInstance().getReference().child("Users").child("drivers").child("driver_id").child(drfoundid);
        String clientid= FirebaseAuth.getInstance().getCurrentUser().getUid();
        HashMap map=new HashMap();
        map.put("clrideid",clientid);
        driverride.updateChildren(map);
        System.out.println("here is the day");
        getdriverlocation();
        clientcall.setText("WAITING FOR DRIVER'S LOCATION");
    }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if(!driverfound)
                {
                    radius = radius + 1;
                    getdrivers();
                }
                }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });

    }

    Marker drmarker;
    private DatabaseReference driverlocationref;
    private ValueEventListener drlocreflistener;

    private void getdriverlocation() {
DatabaseReference driverlocation=FirebaseDatabase.getInstance().getReference().child("working drivers").child(drfoundid).child("l");

driverlocation.addValueEventListener(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        if(dataSnapshot.exists() && reqcancel)
        {
            List<Object> map=(List<Object>)dataSnapshot.getValue();
        double latat=0;
        double loner=0;

        if(map.get(0)!=null)
        {
            latat=Double.parseDouble(map.get(0).toString());


        }
            if(map.get(1)!=null)
            {
                loner=Double.parseDouble(map.get(1).toString());


            }
            LatLng driverloc=new LatLng(latat,loner);

           if(drmarker!=null)
           {
               drmarker.remove();
           }

           Location loc1=new Location("");
           loc1.setLatitude(clientpickloc.latitude);
           loc1.setLongitude(clientpickloc.longitude);

           Location loc2 =new Location("");
           loc2.setLatitude(driverloc.latitude);
           loc2.setLongitude(driverloc.longitude);

           float dist=loc1.distanceTo(loc2);

           drmarker= mMap.addMarker(new MarkerOptions().position(driverloc).title("drivers location"));
clientcall.setText("found : "+String.valueOf(dist));

        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
});
    }


    private void logoutact() {
        Intent in=new Intent(clmaps.this,splashscreen.class);
        in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(in);
        finish();
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
//              mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));


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


    }



}
