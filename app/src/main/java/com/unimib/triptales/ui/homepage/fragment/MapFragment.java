package com.unimib.triptales.ui.homepage.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.unimib.triptales.R;

import com.unimib.triptales.repository.diary.IDiaryRepository;
import com.unimib.triptales.ui.diary.viewmodel.ViewModelFactory;
import com.unimib.triptales.ui.homepage.viewmodel.HomeViewModel;
import com.unimib.triptales.util.GeoJSONParser;
import com.unimib.triptales.util.ServiceLocator;
import com.unimib.triptales.util.SharedPreferencesUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;


public class MapFragment extends Fragment implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private final HashMap<String, List<Polygon>> countryPolygons = new HashMap<>();
    private HomeViewModel homeViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        IDiaryRepository diaryRepository = ServiceLocator.getINSTANCE().getDiaryRepository(getContext());
        homeViewModel = new ViewModelProvider(requireActivity(),
                new ViewModelFactory(diaryRepository, requireActivity().getApplication())).get(HomeViewModel.class);

        return rootView;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_gray_style));
        updateMap(homeViewModel.getAllCountries(SharedPreferencesUtils.getLoggedUserId()));

        homeViewModel.getCountriesLiveData().observe(getViewLifecycleOwner(),
                this::updateMap);

        // Controlla i permessi di localizzazione
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        enableUserLocation();
    }

    private void enableUserLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            mMap.setMyLocationEnabled(true);

            // Recupera l'ultima posizione dell'utente
            fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
                if (location != null) {
                    LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(userLocation).title(getString(R.string.tu_sei_qui)));
                } else {
                    Toast.makeText(getContext(), getString(R.string.errore_posizione), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableUserLocation();
            } else {
                Toast.makeText(getContext(), getString(R.string.posizione_necessaria), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateMap(List<String> countryList){
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
                    if(polygons != null) {
                        for (Polygon polygon : polygons) {
                            polygon.remove();
                        }
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
                    .strokeColor(ContextCompat.getColor(requireContext(), R.color.black))
                    .fillColor(ContextCompat.getColor(requireContext(), R.color.primary))
                    .strokeWidth(1);
            Polygon newPolygon = mMap.addPolygon(polygonOptions);
            coloredPolygons.add(newPolygon);
        }

        countryPolygons.put(country, coloredPolygons);
    }
}
