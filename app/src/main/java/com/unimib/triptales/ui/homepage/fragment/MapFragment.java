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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.unimib.triptales.R;
import com.unimib.triptales.ui.homepage.viewmodel.SharedViewModel;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.unimib.triptales.util.GeoJSONParser;

import java.util.ArrayList;
import java.util.List;


public class MapFragment extends Fragment implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private SharedViewModel sharedViewModel;
    private String userCountry;
    private List<String> countryList;

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

        //usare DiaryViewModel per prendere i paesi dei diari salvati
        //countryPolygonList = diaryViewModel.getAllCountries();

        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        countryList = new ArrayList<>();

        sharedViewModel.getDiaryCountry().observe(requireActivity(), new Observer<String>() {
            @Override
            public void onChanged(String country) {
                userCountry = country;
                // usare DiaryViewModel per verificare che il paese non sia già colorato
                /*CountryPolygon verifica = diaryViewModel.getByCountry(country);
                if(verifica == null && !country.isEmpty()) {
                    CountryPolygon countryPolygon = new CountryPolygon(country);
                    countryPolygonDao.insert(countryPolygon);
                }*/
                if(!country.isEmpty()){
                    countryList.add(country);
                }
            }
        });

        if(countryList != null) {
            for (String country : countryList) {
                GeoJSONParser parser = new GeoJSONParser(getContext(), "world_countries");
                List<List<LatLng>> countryBorders = parser.getCountryBorders(country);

                if(countryBorders != null) {
                    colorPolygons(countryBorders);
                }
            }
        }

        GeoJSONParser parser = new GeoJSONParser(getContext(), "world_countries");
        List<List<LatLng>> countryBorders = parser.getCountryBorders(userCountry);

        //CountryPolygon verifica = countryPolygonDao.getByName(userCountry);
        /*if(verifica == null && countryBorders != null) {
            colorPolygons(countryBorders);
        }*/

        if(countryBorders != null) {
            colorPolygons(countryBorders);
        }

        /*// Rimuovi un poligono specifico
        Polygon polygonToRemove = countryPolygons.get("Italy");
        if (polygonToRemove != null) {
            polygonToRemove.remove(); // Rimuovi il poligono dalla mappa
            countryPolygons.remove("Italy"); // Rimuovi dalla mappa
        }*/

        /*// Controlla i permessi di localizzazione
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Richiedi i permessi
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        // Permessi concessi: abilita la posizione dell'utente
        enableUserLocation();*/
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
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(userLocation).zoom(15).build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
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

    public void colorPolygons(List<List<LatLng>> countryBorders){
        for (List<LatLng> polygon : countryBorders) {
            PolygonOptions polygonOptions = new PolygonOptions()
                    .addAll(polygon)
                    .strokeColor(getResources().getColor(R.color.secondary))
                    .fillColor(Color.parseColor("#70F6EEE5"))
                    .strokeWidth(4);
            mMap.addPolygon(polygonOptions);
        }
    }
}
