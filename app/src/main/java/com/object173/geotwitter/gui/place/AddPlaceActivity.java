package com.object173.geotwitter.gui.place;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.object173.geotwitter.R;
import com.object173.geotwitter.database.entities.Image;
import com.object173.geotwitter.database.entities.Marker;
import com.object173.geotwitter.gui.base.MyBaseActivity;
import com.object173.geotwitter.gui.images.ImageViewerActivity;
import com.object173.geotwitter.gui.util.MapHelper;
import com.object173.geotwitter.gui.util.ServiceConnectionController;
import com.object173.geotwitter.gui.util.WorkaroundMapFragment;
import com.object173.geotwitter.server.ServerContract;
import com.object173.geotwitter.server.json.AuthResult;
import com.object173.geotwitter.service.place.CrudPlaceTask;
import com.object173.geotwitter.service.place.NewsPlaceService;
import com.object173.geotwitter.util.resources.ChooserManager;

import java.util.ArrayList;
import java.util.List;

public class AddPlaceActivity extends MyBaseActivity
        implements OnMapReadyCallback, ImageListAdapter.OnItemClickListener,
                    ServiceConnectionController.ServiceConnector{

    private EditText titleField;
    private EditText bodyField;

    private ImageListAdapter adapter = null;

    private Marker marker;
    private static final String KEY_MARKER = "marker";

    private final ServiceConnectionController connectionController =
            new ServiceConnectionController();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(!super.onCreate(savedInstanceState, R.layout.activity_add_place, true)) {
            finish();
            return;
        }
        if(savedInstanceState != null) {
            marker = (Marker) savedInstanceState.getSerializable(KEY_MARKER);
        }

        titleField = (EditText) findViewById(R.id.title_field);
        bodyField = (EditText) findViewById(R.id.body_field);

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        recyclerView.setLayoutManager(layoutManager);
        adapter = new ImageListAdapter(this);
        adapter.onCreate(savedInstanceState);
        recyclerView.setAdapter(adapter);

        connectionController.onCreate(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(marker != null) {
            outState.putSerializable(KEY_MARKER, marker);
        }
        adapter.onSaveInstanceState(outState);
        connectionController.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        connectionController.onResume(this, this);
        initMap();
    }

    @Override
    protected void onPause() {
        super.onPause();
        connectionController.onPause(this);
    }

    private void initMap() {

        final ScrollView scrollView = (ScrollView) findViewById(R.id.scroll_view);

        final WorkaroundMapFragment mapFragment = (WorkaroundMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.setListener(new WorkaroundMapFragment.OnTouchListener() {
                    @Override
                    public void onTouch() {
                        scrollView.requestDisallowInterceptTouchEvent(true);
                    }
                });

        final Button editButton = (Button) findViewById(R.id.edit_location_button);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChooserManager.showMarkerChooser(AddPlaceActivity.this);
            }
        });

        if(marker == null) {
            final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (lm != null) {
                Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location == null) {
                    location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
                if (location != null) {
                    marker = new Marker(location.getLatitude(), location.getLongitude());
                }
            }
        }

        if(marker != null) {
            getSupportFragmentManager().beginTransaction().show(mapFragment).commit();
            mapFragment.getMapAsync(this);
        }
        else {
            getSupportFragmentManager().beginTransaction().hide(mapFragment).commit();
        }
    }

    @Override
    public final boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.add_place_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if(item == null) {
            return false;
        }

        switch(item.getItemId()) {
            case R.id.menu_send:
                sendNewPlace();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        googleMap.setMapType(MapHelper.getMapType(this));
        googleMap.clear();

        if(marker == null) {

            return;
        }

        final LatLng position = new LatLng(marker.getLatitude(), marker.getLongitude());
        googleMap.addMarker(new MarkerOptions()
                .position(position));
        final CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(position)
                .zoom(getResources().getInteger(R.integer.simple_map_default_zoom))
                .build();
        final CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        googleMap.moveCamera(cameraUpdate);
    }

    private void sendNewPlace() {
        final String title = titleField.getText().toString();
        final String body = bodyField.getText().toString();

        if(title.length() < ServerContract.PLACE_TITLE_MIN_LENGTH) {
            titleField.setError(getString(R.string.add_place_activity_error_min_length) +
                    " " + ServerContract.PLACE_TITLE_MIN_LENGTH);
            return;
        }
        if(body.length() < ServerContract.PLACE_BODY_MIN_LENGTH) {
            bodyField.setError(getString(R.string.add_place_activity_error_min_length) +
                    " " + ServerContract.PLACE_BODY_MIN_LENGTH);
            return;
        }

        final List<Image> imageList = adapter.getImageList();
        final ArrayList<String> pathList;
        if(imageList != null && imageList.size() > 0) {
            pathList = new ArrayList<>();
            for(Image image : imageList) {
                if(image.getLocalPath() != null) {
                    pathList.add(image.getLocalPath());
                }
            }
        }
        else {
            pathList = null;
        }

        if(connectionController.setRequestId(
                NewsPlaceService.startToAddPlace(this, title, body, marker, pathList))) {
            showProgressDialog();
        }
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if(requestCode == ChooserManager.INTENT_PICK_IMAGE) {
            if(resultCode == RESULT_OK) {
                final Uri uri = ChooserManager.getImageUri(this, data);
                if(uri != null && adapter != null) {
                    adapter.addImage(Image.newLocalImage(uri.getPath()));
                }
            }
        } else
        if (requestCode == ChooserManager.MARKER_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                this.marker = ChooserManager.getMarker(this, data);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onAddClick() {
        ChooserManager.showImageChooser(AddPlaceActivity.this,
                getString(R.string.add_place_activity_title_image_chooser), null);
    }

    @Override
    public void onImageClick(Image image) {
        if(image != null) {
            Log.d("AddPlaceActivity", image.getLocalPath());
            ImageViewerActivity.startActivity(this, image, null);
        }
    }

    @Override
    public void receiveMessage(Intent intent) {
    }

    @Override
    public void finishTask(Intent intent) {
        if(CrudPlaceTask.isAddIntent(intent)) {
            hideProgressDialog();

            final AuthResult.Result result = CrudPlaceTask.getAuthResult(intent);

            if(result.equals(AuthResult.Result.SUCCESS)) {
                finish();
            }
            else {
                showReceiveResult(result);
            }
        }
    }

    @Override
    public void finishTask(Class serviceClass) {
        hideProgressDialog();
    }
}
