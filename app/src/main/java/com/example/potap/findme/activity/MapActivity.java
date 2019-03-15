package com.example.potap.findme.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.potap.findme.NewEventDialog;
import com.example.potap.findme.R;
import com.example.potap.findme.adapters.PlaceAutocompleteAdapter;
import com.example.potap.findme.firebase.DataManager;
import com.example.potap.findme.model.Event;
import com.example.potap.findme.model.PlaceInfo;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.potap.findme.util.Constants.DEFAULT_ZOOM;
import static com.example.potap.findme.util.Constants.LOCATION_PERMISSION_REQUEST_CODE;

public class MapActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "MapActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(new LatLng(-40, -168), new LatLng(71, 136));

    private boolean mLocationPermissionGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    private GeoDataClient mGeoDataClient;
    private GoogleApiClient mGoogleApiClient;
    private PlaceInfo mPlace;
    private Geocoder geocoder;
    private ImageButton buttonDeleteEvent;
    private TextView eventTitle;
    private TextView eventAddress;
    private TextView eventDescription;
    private TextView eventUsersCount;
    private AutoCompleteTextView editTextSearch;
    private ImageButton buttonSearch;
    private ImageView mGps;
    private LinearLayout bottomSheet;
    private BottomSheetBehavior bottomSheetBehavior;
    private LinearLayout infoEvent;
    private BottomSheetBehavior infoEventBehavior;
    private TextView nameInfoTextView;
    private TextView addressInfoTextView;
    private TextView phoneInfoTextView;
    private TextView websiteInfoTextView;
    private FloatingActionButton openBottomFloatingButton;
    private DrawerLayout drawerLayout;
    private ImageButton buttonMenuLeft;

    public static ArrayList<Event> events = new ArrayList<>();
    private ArrayList<Marker> markers;

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        markers = new ArrayList<>();

        editTextSearch = findViewById(R.id.edit_text_search);
        buttonSearch = findViewById(R.id.ic_search_bt);
        mGps = findViewById(R.id.ic_gps);

        //Bottom Sheets
/*
        bottomSheet = findViewById(R.id.info_bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
*/
        infoEvent = findViewById(R.id.info_event);
        infoEventBehavior = BottomSheetBehavior.from(infoEvent);
        //

        buttonDeleteEvent = findViewById(R.id.bt_delete_event);
        eventUsersCount = findViewById(R.id.event_users_count);
        eventTitle = findViewById(R.id.event_title);
        eventAddress = findViewById(R.id.event_address);
        eventDescription = findViewById(R.id.event_description);
        nameInfoTextView = findViewById(R.id.info_name_text_view);
        addressInfoTextView = findViewById(R.id.info_address_text_view);
        phoneInfoTextView = findViewById(R.id.info_phone_text_view);
        websiteInfoTextView = findViewById(R.id.info_website_text_view);
        openBottomFloatingButton = findViewById(R.id.fl_button_open_bottom);
        buttonMenuLeft = findViewById(R.id.bt_menu_left);
        drawerLayout = findViewById(R.id.drawer_layout);

        geocoder = new Geocoder(MapActivity.this);

        openBottomFloatingButton.hide();

/*
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
*/
        infoEventBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        getLocationPermission();

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                geoLocate();

//                hideSoftKeyBoard();
            }
        });

        openBottomFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
/*
                showBottomSheet(bottomSheetBehavior);
*/
                showBottomSheet(infoEventBehavior);
            }
        });

/*
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                if (i == BottomSheetBehavior.STATE_HIDDEN)
                    openBottomFloatingButton.show();
                else
                    openBottomFloatingButton.hide();
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });
*/

        buttonMenuLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.START);
            }
        });

        DataManager.getInstance().getEventsReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (events.isEmpty()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Map record = (Map) ds.getValue();
                        addEvent(record, Integer.parseInt(ds.getKey()));
                    }
                }
                updateEvents(events, dataSnapshot);
                fillMap(events);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        buttonDeleteEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < events.size(); i++) {
                    if (events.get(i).getDescription().equals(eventDescription.getText()) &&
                            events.get(i).getTitle().equals(eventTitle.getText())) {
                        DataManager.getInstance().getEventsReference().child(String.valueOf(events.get(i).getId())).removeValue();
//                        events.remove(i);
                    }
                }
            }
        });

    }

    private void showBottomSheet(BottomSheetBehavior behavior) {
        if (behavior.getState() == BottomSheetBehavior.STATE_HIDDEN)
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        else
            behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    private void addEvent(Map record, int id) {
        events.add(new Event(
                id,
                record.get("title").toString(),
                record.get("description").toString(),
                (Map) record.get("position"),
                record.get("address").toString(),
                Integer.parseInt(record.get("usersCount").toString()),
                record.get("userId").toString()));
    }

    private void updateEvents(ArrayList<Event> events, DataSnapshot dataSnapshot) {
        long dsCount = dataSnapshot.getChildrenCount();
        long difference = dsCount - events.size();
        if (difference > 0) {
/*
            for (int i = events.size(); i < dsCount; i++) {
*/
            Map fgp = (Map) dataSnapshot.child(String.valueOf(dsCount)).getValue();
            if (fgp != null)
                addEvent(fgp, (int) dsCount);
        } else if (difference < 0) {
            for (int i = (int)dsCount; i < events.size(); i++) {////////
                events.remove(i);                       //Удаление событий из списка
                mMap.clear();
                showBottomSheet(infoEventBehavior);///////
            }
        }
    }

    private void fillMap(ArrayList<Event> events) {
        if (!events.isEmpty())
            for (Event event : events)
                addOptionsAndMarker(event.getPosition(), event.getTitle());
    }

    private void init() {
        Log.d(TAG, "init: initializing");

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        mGeoDataClient = Places.getGeoDataClient(this, null);

        mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(this, mGeoDataClient, LAT_LNG_BOUNDS, null);


        editTextSearch.setOnItemClickListener(mAutocompleteClickListener);
        editTextSearch.setAdapter(mPlaceAutocompleteAdapter);
        editTextSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN
                        || event.getAction() == KeyEvent.KEYCODE_ENTER) {

                    //execute method
                    geoLocate();
                }
                return false;
            }
        });

        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked gps icon");
                getDeviceLocation();
            }
        });

        hideSoftKeyBoard();
    }

    private void geoLocate() {                     //Searching
        Log.d(TAG, "geoLocate: geoLocating");

        openBottomFloatingButton.show();

        String searchStr = editTextSearch.getText().toString();

        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchStr, 1);
        } catch (IOException e) {
            Log.e(TAG, "geoLocate: IOException: " + e.getMessage());
        }

        if (list.size() > 0) {
            Address address = list.get(0);

//            Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();

            Log.d(TAG, "geoLocate: found a location: " + address.toString());

            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM, address.getAddressLine(0));
        }
    }

    private void setPlaceInfo(PlaceInfo placeInfo) {
        if (mPlace != null && infoEvent.getVisibility() == View.INVISIBLE)
            infoEvent.setVisibility(View.VISIBLE);

        if (mPlace != null) {
            nameInfoTextView.setText(placeInfo.getName());
            addressInfoTextView.setText(placeInfo.getAddress());
        }
    }

    private void setEventInfo(Event event) {
        if (!eventTitle.getText().equals(event.getTitle()) && !eventDescription.getText().equals(event.getDescription())) {
            eventTitle.setText(event.getTitle());
            eventAddress.setText(event.getAddress());
            eventDescription.setText(event.getDescription());
            eventUsersCount.setText("Users count: " + Integer.toString(event.getUsersCount()));
            String key = DataManager.getInstance().getFirebaseAuth().getUid();
            String Ekey = event.getUserId();
            if (key != null && key.equals(Ekey))
                buttonDeleteEvent.setVisibility(View.VISIBLE);
        }
    }

    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (mLocationPermissionGranted) {
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: found location!");

                            Location currentLocation = (Location) task.getResult();
                            if (currentLocation != null) {
                                moveCamera(new LatLng(currentLocation.getLatitude(),
                                                currentLocation.getLongitude()),
                                        DEFAULT_ZOOM, "My location");
                            }
                            else {
                                currentLocation = new Location("");//provider name is unnecessary   ///Костыль  Костыль Костыль   Костыль
                                currentLocation.setLatitude(54.7845032);//your coords of course                 ///Костыль  Костыль Костыль   Костыль
                                currentLocation.setLongitude(32.0452469);                                   ///Костыль  Костыль Костыль   Костыль
                                moveCamera(new LatLng(currentLocation.getLatitude(),                       ///Костыль  Костыль Костыль   Костыль
                                                currentLocation.getLongitude()),                        ///Костыль  Костыль Костыль   Костыль
                                        DEFAULT_ZOOM, "My location");
                            }
                        } else {
                            Log.d(TAG, "onComplete: found location!");
                            Toast.makeText(MapActivity.this, "unable to get current", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    private void addOptionsAndMarker(LatLng latLng, String title) {
        markers.add(mMap.addMarker(new MarkerOptions().position(latLng).title(title)));
    }

    private void moveCamera(LatLng latLng, float zoom, String title) {
        Log.d(TAG, "move the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        if (!title.equals("My location")) {
            addOptionsAndMarker(latLng, title);
        }

        setPlaceInfo(mPlace);

        hideSoftKeyBoard();
    }

    private void initMap() {
        Log.d(TAG, "initMap: initializing map");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    Toast.makeText(MapActivity.this, "Map is ready", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onMapReady: map is ready");
                    mMap = googleMap;

                    if (mLocationPermissionGranted) {
                        getDeviceLocation();

                        if (ActivityCompat.checkSelfPermission(MapActivity.this,
                                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                ActivityCompat.checkSelfPermission(MapActivity.this,
                                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        mMap.setMyLocationEnabled(true);

                        init();
                    }

                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            for (int i = 0; i < events.size(); i++) {
                                if (events.get(i).getTitle().equals(marker.getTitle())) {
                                    setEventInfo(events.get(i));
                                    break;
                                }
                            }
                            showBottomSheet(infoEventBehavior); //////
                            infoEvent.setVisibility(View.VISIBLE);
                            return true;
                        }
                    });

                    infoEventBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                        @Override
                        public void onStateChanged(@NonNull View view, int i) {
                            if (i == infoEventBehavior.STATE_HIDDEN)
                                openBottomFloatingButton.show();
                            else
                                openBottomFloatingButton.hide();
                        }

                        @Override
                        public void onSlide(@NonNull View view, float v) {

                        }
                    });

                    addNewEvent();
                }
            });

        }
    }

    private void addNewEvent() {
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                NewEventDialog dialog = new NewEventDialog(mMap, latLng, geocoder);
                dialog.show(getSupportFragmentManager(), "NewEventDialog");
            }
        });
    }

    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permission");
        String[] permissions = {FINE_LOCATION,
                COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called");
        mLocationPermissionGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionGranted = true;
                    //init our map
                    initMap();
                }
            }
        }
    }

    private void hideSoftKeyBoard() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }


    private AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            hideSoftKeyBoard();

            openBottomFloatingButton.show();

            final AutocompletePrediction item = mPlaceAutocompleteAdapter.getItem(position);
            final String placeId = item.getPlaceId();

            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);

        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.d(TAG, "onResult: Place query did not complete successfully: " + places.getStatus().toString());

                places.release();
                return;
            }
            final Place place = places.get(0);

            try {
                mPlace = new PlaceInfo(place);

                Log.d(TAG, "onResult: place: " + mPlace.toString());
            } catch (NullPointerException e) {
                Log.e(TAG, "onResult: NullPointerException: " + e.getMessage());
            }

            moveCamera(new LatLng(place.getViewport().getCenter().latitude, place.getViewport().getCenter().longitude)
                    , DEFAULT_ZOOM, mPlace.getName());

            places.release();
        }
    };
}
