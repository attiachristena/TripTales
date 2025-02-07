package com.unimib.triptales.ui.homepage.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.unimib.triptales.R;
import com.unimib.triptales.database.AppRoomDatabase;
import com.unimib.triptales.database.DiaryDao;

import com.unimib.triptales.util.GeoJSONParser;
import com.unimib.triptales.util.SharedPreferencesUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;


public class MapFragment extends Fragment implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private DiaryDao diaryDao;
    private HashMap<String, List<Polygon>> countryPolygons = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        // Inizializza il provider della posizione
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // Inizializza la mappa
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        diaryDao = AppRoomDatabase.getDatabase(getContext()).diaryDao();

        return rootView;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_gray_style));
        updateMap();

        // Controlla i permessi di localizzazione
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Richiedi i permessi
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        // Permessi concessi: abilita la posizione dell'utente
        enableUserLocation();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mMap != null) {
            updateMap();
        }
    }

    private void enableUserLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            mMap.setMyLocationEnabled(true);

            // Recupera l'ultima posizione dell'utente
            fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
                if (location != null) {
                    LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(userLocation).title("You are here"));
                    //CameraPosition cameraPosition = new CameraPosition.Builder().target(userLocation).zoom(15).build();
                    //mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                } else {
                    Toast.makeText(getContext(), "Unable to get location", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permessi concessi
                enableUserLocation();
            } else {
                // Permessi negati
                Toast.makeText(getContext(), "Location permission is required to show your location on the map", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateMap(){
        String userId = SharedPreferencesUtils.getLoggedUserId();
        List<String> countryList = diaryDao.getAllCountries(userId);

        HashSet<String> countryListSet = new HashSet<>(countryList);
        HashSet<String> countryPolygonsSet = new HashSet<>(countryPolygons.keySet());

        // aggiunge nuvoi poligoni se il paese non è ancora disegnato
        for(String country : countryList){
            if(!countryPolygons.containsKey(country)){
                colorPolygons(country);
            }
        }

        // rimuove i poligoni dei paesi che non sono più nel database
        for(String country : countryPolygonsSet){
            if(!countryListSet.contains(country)){
                if(countryPolygons.containsKey(country)){
                    List<Polygon> polygons = countryPolygons.get(country);

                    for(Polygon polygon : polygons){
                        polygon.remove();
                    }

                    countryPolygons.remove(country);
                }
            }
        }
    }

    private void colorPolygons(String country){
        GeoJSONParser parser = new GeoJSONParser(getContext(), "world_countries");
        List<List<LatLng>> countryBorders = parser.getCountryBorders(country);
        if(countryBorders == null || countryBorders.isEmpty()) return;

        List<Polygon> coloredPolygons = new ArrayList<>();

        for (List<LatLng> polygon : countryBorders) {
            PolygonOptions polygonOptions = new PolygonOptions()
                    .addAll(polygon)
                    .strokeColor(getResources().getColor(R.color.black))
                    .fillColor(getResources().getColor(R.color.primary))
                    .strokeWidth(1);
            Polygon newPolygon = mMap.addPolygon(polygonOptions);
            coloredPolygons.add(newPolygon);
        }

        countryPolygons.put(country, coloredPolygons);
    }
}
