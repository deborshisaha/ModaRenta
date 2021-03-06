package fashiome.android.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fashiome.android.Manifest;
import fashiome.android.R;
import fashiome.android.adapters.CustomWindowAdapter;
import fashiome.android.adapters.MapviewAdapter;
import fashiome.android.models.Product;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;


@RuntimePermissions
public class MapFullScreenActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        GoogleMap.OnMapLongClickListener{

    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 60000;  /* 60 secs */
    private long FASTEST_INTERVAL = 5000; /* 5 secs */

    private LatLng mLocation;
    private Marker mLocationMarker;

    ImageView mProfileLogo;
    private static final String TAG = MapFullScreenActivity.class.getSimpleName();


    /*
     * Define a request code to send to Google Play services This code is
     * returned in Activity.onActivityResult
     */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    ArrayList<Product> mItems;
    private HashMap<String, Product> mMarkers= new HashMap<String, Product>();
    ImageView pic;
    TextView title;
    TextView desc;
    TextView price;
    RecyclerView rvMap;
    Product mSelectedProduct;
    LinearLayoutManager linearLayoutManager;
    MapviewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_full_screen);

        mItems = getIntent().getParcelableArrayListExtra("products");

        Log.i(TAG, "list of products" + mItems.size());

        rvMap = (RecyclerView) findViewById(R.id.rvMap);

        Log.i(TAG, "oncreateview");

        // Set layout manager to position the items
        linearLayoutManager =
                new LinearLayoutManager(MapFullScreenActivity.this);

        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        rvMap.setLayoutManager(linearLayoutManager);

        // Create adapter passing in the sample user data
        adapter = new MapviewAdapter(MapFullScreenActivity.this);

        rvMap.setAdapter(adapter);


/*
        RelativeLayout footer = (RelativeLayout) findViewById(R.id.rlFooter);
        footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //...
                //show dialog fragment here
                Intent i = new Intent(MapFullScreenActivity.this, ProductDetailsActivity.class);
                i.putExtra("product", mSelectedProduct);
                startActivity(i);
            }
        });
*/

        System.out.println(mItems);
        System.out.print(mItems.size());

       // mItems = Item.loadItems();
        //setToolBar();
        mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap map) {
                    loadMap(map);
                    map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    map.setInfoWindowAdapter(new CustomWindowAdapter(getLayoutInflater()));
                }
            });
        } else {
            Toast.makeText(this, "Error - Map Fragment was null!!", Toast.LENGTH_SHORT).show();
        }

    }

    private void moveToLocation(LatLng latLng, final boolean moveCamera) {
        if (latLng == null) {
            return;
        }
        moveMarker(latLng);
        mLocation = latLng;
        new Runnable() {
            @Override
            public void run() {
                if (map != null && moveCamera) {
                    map.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(mLocation, 11.0f)));
                }
            }
        };
    }

    private void moveMarker(LatLng latLng) {
        if (mLocationMarker != null) {
            mLocationMarker.remove();
        }
        //mLocationMarker = map.addMarker(new MarkerOptions()
         //       .position(latLng).anchor(0.5f, 0.5f));
        //.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_my_location))
    }

    public void setToolBar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        View logo = getLayoutInflater().inflate(R.layout.home_activity_toolbar, null);
        toolbar.addView(logo);
        setSupportActionBar(toolbar);

        mProfileLogo = (ImageView) findViewById(R.id.ivProfileLogo);

        if(ParseUser.getCurrentUser() != null) {

            String profileUrl = ParseUser.getCurrentUser().get("profilePictureUrl").toString();
            Glide.with(MapFullScreenActivity.this).load(profileUrl).into(mProfileLogo);
        }


    }

    public void updateFooter(Product product){
        Log.i(TAG," marker "+product.getProductName());
        mSelectedProduct = product;
        title.setText(product.getProductName());
        desc.setText(product.getProductDescription());
        price.setText(product.getPrice()+"");
    }

    public void updateMarker(Marker marker){
        Product product = (Product) mMarkers.get(marker.getId());
        updateFooter(product);
    }

    public void loadMarkersFromItems(ArrayList<Product> products){
        Log.i(TAG,"load markers "+products.size());
        Product product=null;
        for(int i=0;i<products.size();i++) {
            product = products.get(i);
            BitmapDescriptor defaultMarker =
                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
            if (product.getAddress() != null) {
                Log.i(TAG,"added a marker ");
                Marker marker = map.addMarker(new MarkerOptions()
                        .position(new LatLng(product.getAddress().getLatitude(), product.getAddress().getLongitude()))
                        .title(product.getProductName())
                        .snippet(product.getProductDescription())
                        .icon(defaultMarker));
                mMarkers.put(marker.getId(), product);
                LatLng latLng = new LatLng(product.getAddress().getLatitude(), product.getAddress().getLongitude());
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 12);
                map.animateCamera(cameraUpdate);
                dropPinEffect(marker);
            }
        }
        mSelectedProduct = product;
        //updateFooter(mSelectedProduct);
    }

    protected void loadMap(GoogleMap googleMap) {
        map = googleMap;
        if (map != null) {
            // Map is ready
            map.getUiSettings().setMapToolbarEnabled(true);
            /*final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(10);
            executor.schedule(new Runnable() {
                @Override
                public void run() {
                    loadMarkersFromItems(mItems);
                }
            },1, 1000);*/
            loadMarkersFromItems(mItems);
                   // Attach marker click listener to the map here
            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                public boolean onMarkerClick(Marker marker) {
                    // Handle marker click here
                    //show the details on the banner
                    BitmapDescriptor greenMarker =
                            BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
                    marker.setIcon(greenMarker);
                    moveToLocation(marker.getPosition(), true);
                    updateMarker(marker);

                    return true;
                }
            });
            map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    //if (mItems !=null)
                      //  loadMarkersFromItems(mItems);
                }
            });
            map.setOnMapLongClickListener(this);
            Toast.makeText(this, "Map Fragment was loaded properly!", Toast.LENGTH_SHORT).show();
            MapFullScreenActivityPermissionDispatcher.getMyLocationWithCheck(this);
        } else {
            Toast.makeText(this, "Error - Map was null!!", Toast.LENGTH_SHORT).show();
        }
    }
    // Fires when a long press happens on the map
    @Override
    public void onMapLongClick(final LatLng point) {
        Toast.makeText(this, "Long Press", Toast.LENGTH_LONG).show();
        // Custom code here...
        // Display the alert dialog
        showAlertDialogForPoint(point);

    }

    // Display the alert that adds the marker
    private void showAlertDialogForPoint(final LatLng point) {
        // inflate message_item.xml view
        View messageView = LayoutInflater.from(MapFullScreenActivity.this).
                inflate(R.layout.message_item, null);
        // Create alert dialog builder
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // set message_item.xml to AlertDialog builder
        alertDialogBuilder.setView(messageView);

        // Create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();

        // Configure dialog button (OK)
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Define color of marker icon
                        BitmapDescriptor defaultMarker =
                                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
                        // Extract content from alert dialog
                        String title = ((EditText) alertDialog.findViewById(R.id.etTitle)).
                                getText().toString();
                        String snippet = ((EditText) alertDialog.findViewById(R.id.etSnippet)).
                                getText().toString();
                        // Creates and adds marker to the map
                        Marker marker = map.addMarker(new MarkerOptions()
                                .position(point)
                                .title(title)
                                .snippet(snippet)
                                .icon(defaultMarker));
                        //mMarkers.put(marker.getId(), new Item(title, point));
                        // Animate marker using drop effect
                        // --> Call the dropPinEffect method here
                        dropPinEffect(marker);

                    }
                });
// Configure dialog button (Cancel)
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) { dialog.cancel(); }
                });

        // Display the dialog
        alertDialog.show();
    }




    private void dropPinEffect(final Marker marker) {
        // Handler allows us to repeat a code block after a specified delay
        final android.os.Handler handler = new android.os.Handler();
        final long start = SystemClock.uptimeMillis();
        final long duration = 1500;

        // Use the bounce interpolator
        final android.view.animation.Interpolator interpolator =
                new BounceInterpolator();

        // Animate marker with a bounce updating its position every 15ms
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                // Calculate t for bounce based on elapsed time
                float t = Math.max(
                        1 - interpolator.getInterpolation((float) elapsed
                                / duration), 0);
                // Set the anchor
                marker.setAnchor(0.5f, 1.0f + 14 * t);

                if (t > 0.0) {
                    // Post this event again 15ms from now.
                    handler.postDelayed(this, 15);
                } else { // done elapsing, show window
                    marker.showInfoWindow();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MapFullScreenActivityPermissionDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    void getMyLocation() {
        if (map != null) {
            // Now that map has loaded, let's get our location!
            try {
                map.setMyLocationEnabled(true);
            } catch (SecurityException e){
                e.printStackTrace();
            }
            map.getUiSettings().setMapToolbarEnabled(true);
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();
            connectClient();
        }
    }

    protected void connectClient() {
        // Connect the client.
        if (isGooglePlayServicesAvailable() && mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    /*
     * Called when the Activity becomes visible.
    */
    @Override
    protected void onStart() {
        super.onStart();
        connectClient();
    }

    /*
	 * Called when the Activity is no longer visible.
	 */
    @Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    /*
     * Handle results returned to the FragmentActivity by Google Play services
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Decide what to do based on the original request code
        switch (requestCode) {

            case CONNECTION_FAILURE_RESOLUTION_REQUEST:
			/*
			 * If the result code is Activity.RESULT_OK, try to connect again
			 */
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        mGoogleApiClient.connect();
                        break;
                }

        }
    }

    private boolean isGooglePlayServicesAvailable() {
        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d("Location Updates", "Google Play services is available.");
            return true;
        } else {
            // Get the error dialog from Google Play services
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                    CONNECTION_FAILURE_RESOLUTION_REQUEST);

            // If Google Play services can provide an error dialog
            if (errorDialog != null) {
                // Create a new DialogFragment for the error dialog
                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                errorFragment.setDialog(errorDialog);
                errorFragment.show(getSupportFragmentManager(), "Location Updates");
            }

            return false;
        }
    }

    /*
     * Called by Location Services when the request to connect the client
     * finishes successfully. At this point, you can request the current
     * location or start periodic updates
     */
    @Override
    public void onConnected(Bundle dataBundle) {
        // Display the connection status
        Location location = null;
        try {
            location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        } catch(SecurityException e){
            e.printStackTrace();
        }
        if (location != null) {
            Toast.makeText(this, "GPS location was found!", Toast.LENGTH_SHORT).show();
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);

            map.animateCamera(cameraUpdate);
        } else {
            Toast.makeText(this, "Current location was null, enable GPS on emulator!", Toast.LENGTH_SHORT).show();
        }
        //startLocationUpdates();
    }

    protected void startLocationUpdates() throws SecurityException {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);
    }

    public void onLocationChanged(Location location) {
        // Report to the UI that the location was updated
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

    }

    /*
     * Called by Location Services if the connection to the location client
     * drops because of an error.
     */
    @Override
    public void onConnectionSuspended(int i) {
        if (i == CAUSE_SERVICE_DISCONNECTED) {
            Toast.makeText(this, "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
        } else if (i == CAUSE_NETWORK_LOST) {
            Toast.makeText(this, "Network lost. Please re-connect.", Toast.LENGTH_SHORT).show();
        }
    }

    /*
     * Called by Location Services if the attempt to Location Services fails.
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
		/*
		 * Google Play services can resolve some errors it detects. If the error
		 * has a resolution, try sending an Intent to start a Google Play
		 * services activity that can resolve error.
		 */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
				/*
				 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getApplicationContext(),
                    "Sorry. Location services not available to you", Toast.LENGTH_LONG).show();
        }
    }

    // Define a DialogFragment that displays the error dialog
    public static class ErrorDialogFragment extends DialogFragment {

        // Global field to contain the error dialog
        private Dialog mDialog;

        // Default constructor. Sets the dialog field to null
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }

        // Set the dialog to display
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        // Return a Dialog to the DialogFragment.
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_items,menu);
        MenuItem searchItem =menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String q) {
                //fetch new results
                // Define the class we would like to query
                ParseQuery<Product> query = ParseQuery.getQuery(Product.class);
                // Define our query conditions
                query.whereContains("productDescription", q);
                // Execute the find asynchronously
                query.findInBackground(new FindCallback<Product>() {
                    public void done(List<Product> itemList, ParseException e) {
                        if (e == null) {
                            // Access the array of results here
                            if (itemList !=null){
                                String firstItemId = itemList.get(0).getObjectId();
                                Toast.makeText(MapFullScreenActivity.this, firstItemId, Toast.LENGTH_SHORT).show();
                            } else
                                Toast.makeText(MapFullScreenActivity.this, "No Matching Results found !! try again !! :)", Toast.LENGTH_SHORT).show();

                        } else {
                            Log.d(TAG, "Error: " + e.getMessage());
                        }
                    }
                });
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.login_fb:
                Intent i =new Intent(MapFullScreenActivity.this,LoginActivity.class);
                startActivity(i);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
