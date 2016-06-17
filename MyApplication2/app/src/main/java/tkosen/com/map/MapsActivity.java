package tkosen.com.map;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tkosen.com.map.modal.MapObject;
import tkosen.com.map.net.CountryAPI;
import tkosen.com.map.net.ServiceGenerator;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private CountryAPI countryAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        countryAPI = ServiceGenerator.createService(CountryAPI.class);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        countryAPI.getCountries().enqueue(new Callback<List<MapObject>>() {
            @Override
            public void onResponse(Call<List<MapObject>> call, Response<List<MapObject>> response) {
                if (response == null || response.body() == null)
                    return;

                Toast.makeText(MapsActivity.this, String.valueOf(response.body().size()), Toast.LENGTH_SHORT).show();

                for (MapObject mapObject : response.body()) {
                    if(mapObject.getLatlng() != null && mapObject.getLatlng().size()>1) {
                        LatLng latLng = new LatLng(mapObject.getLatlng().get(0), mapObject.getLatlng().get(1));
                        mMap.addMarker(new MarkerOptions().position(latLng).title(mapObject.getName()));
                    }
                }
            }

            @Override
            public void onFailure(Call<List<MapObject>> call, Throwable t) {
                Toast.makeText(MapsActivity.this, t.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
