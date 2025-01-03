package com.unimib.triptales.ui.homepage.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
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
import com.unimib.triptales.util.GeoJSONParser;

import java.util.List;


public class MapFragment extends Fragment implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;

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

        return rootView;
    }


       @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;

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
  
            //colora i confini
            String userCountry = "Italy";

            GeoJSONParser parser = new GeoJSONParser(getContext(), "world_countries");
            List<List<LatLng>> countryBorders = parser.getCountryBorders(userCountry);

            for (List<LatLng> polygon : countryBorders) {
                PolygonOptions polygonOptions = new PolygonOptions()
                        .addAll(polygon)
                        .strokeColor(R.color.secondary)
                        .fillColor(R.color.primary);
                mMap.addPolygon(polygonOptions);
            }

            /*// Rimuovi un poligono specifico
            Polygon polygonToRemove = countryPolygons.get("Italy");
            if (polygonToRemove != null) {
                polygonToRemove.remove(); // Rimuovi il poligono dalla mappa
                countryPolygons.remove("Italy"); // Rimuovi dalla mappa
            }*/
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
    }
