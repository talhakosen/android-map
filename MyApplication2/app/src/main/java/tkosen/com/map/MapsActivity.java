package tkosen.com.map;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tkosen.com.map.modal.MapObject;
import tkosen.com.map.net.CountryAPI;
import tkosen.com.map.net.ServiceGenerator;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    List<MapObject> mapObjects;
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setInfoWindowAdapter(new MapInfoAdapter());

        countryAPI.getCountries().enqueue(new Callback<List<MapObject>>() {
            @Override
            public void onResponse(Call<List<MapObject>> call, Response<List<MapObject>> response) {
                if (response == null || response.body() == null)
                    return;

                Toast.makeText(MapsActivity.this, String.valueOf(response.body().size()), Toast.LENGTH_SHORT).show();
                mapObjects = response.body();
                for (MapObject mapObject : response.body()) {
                    if (mapObject.getLatlng() != null && mapObject.getLatlng().size() > 1) {
                        LatLng latLng = new LatLng(mapObject.getLatlng().get(0), mapObject.getLatlng().get(1));
                        mMap.addMarker(new MarkerOptions().position(latLng).title(mapObject.getName())).setSnippet(mapObject.getAlpha2Code());
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                    }
                }
            }

            @Override
            public void onFailure(Call<List<MapObject>> call, Throwable t) {
                Toast.makeText(MapsActivity.this, t.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    class MapInfoAdapter implements GoogleMap.InfoWindowAdapter {
        private final View myContentsView;

        MapInfoAdapter() {
            myContentsView = getLayoutInflater().inflate(R.layout.custom_info_contents, null);
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            String alphaCode = marker.getSnippet();
            MapObject selectedMapObject = null;
            for (MapObject mapObject : mapObjects) {
                if (mapObject.getAlpha2Code().equalsIgnoreCase(alphaCode))
                    selectedMapObject = mapObject;
            }

            if(selectedMapObject== null)
                return myContentsView;


            TextView txt_name = ((TextView) myContentsView.findViewById(R.id.txt_name));
            TextView txt_capital = ((TextView) myContentsView.findViewById(R.id.txt_capital));
            TextView txt_population = ((TextView) myContentsView.findViewById(R.id.txt_population));

            txt_name.setText(selectedMapObject.getName());
            txt_capital.setText(selectedMapObject.getCapital());
            txt_population.setText(String.valueOf(selectedMapObject.getPopulation()));

            return myContentsView;
        }
    }
}
