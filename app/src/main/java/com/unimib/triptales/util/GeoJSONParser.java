package com.unimib.triptales.util;

import android.content.Context;
import com.google.android.gms.maps.model.LatLng;
import com.unimib.triptales.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class GeoJSONParser {

    private final Context context;
    private final String fileName;

    public GeoJSONParser(Context context, String fileName) {
        this.context = context;
        this.fileName = fileName;
    }

    public List<List<LatLng>> getCountryBorders(String countryName) {
        try {
            InputStream inputStream = context.getResources().openRawResource(
                    context.getResources().getIdentifier(fileName, "raw", context.getPackageName())
            );
            StringBuilder builder = new StringBuilder();
            int byteData;
            while ((byteData = inputStream.read()) != -1) {
                builder.append((char) byteData);
            }

            JSONObject geoJson = new JSONObject(builder.toString());
            JSONArray features = geoJson.getJSONArray("features");

            for (int i = 0; i < features.length(); i++) {
                JSONObject feature = features.getJSONObject(i);
                JSONObject properties = feature.getJSONObject("properties");

                if (properties.has("NAME_IT") && properties.getString("NAME_IT").equalsIgnoreCase(countryName)) {
                    JSONObject geometry = feature.getJSONObject("geometry");
                    String geometryType = geometry.getString("type");

                    List<List<LatLng>> polygons = new ArrayList<>();

                    if (geometryType.equals("Polygon")) {
                        JSONArray coordinatesArray = geometry.getJSONArray("coordinates");
                        polygons.add(parsePolygon(coordinatesArray));
                    } else if (geometryType.equals("MultiPolygon")){
                        JSONArray coordinatesArray = geometry.getJSONArray("coordinates");
                        polygons.addAll(parseMultiPolygon(coordinatesArray));
                    }
                    return polygons;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<LatLng> parsePolygon(JSONArray coordinatesArray) throws Exception {
        List<LatLng> latLngList = new ArrayList<>();
        for (int i = 0; i < coordinatesArray.length(); i++) {
            JSONArray coordinates = coordinatesArray.getJSONArray(i);
            for (int j = 0; j < coordinates.length(); j++) {
                JSONArray point = coordinates.getJSONArray(j);
                double lng = point.getDouble(0);
                double lat = point.getDouble(1);
                latLngList.add(new LatLng(lat, lng));
            }
        }
        if (!latLngList.isEmpty() && !latLngList.get(0).equals(latLngList.get(latLngList.size() - 1))) {
            latLngList.add(latLngList.get(0));
        }
        return latLngList;
    }

    private List<List<LatLng>> parseMultiPolygon(JSONArray multiCoordinatesArray) throws Exception {
        List<List<LatLng>> allPolygons = new ArrayList<>();
        for (int i = 0; i < multiCoordinatesArray.length(); i++) {
            JSONArray polygonCoordinates = multiCoordinatesArray.getJSONArray(i);
            List<LatLng> singlePolygon = parsePolygon(polygonCoordinates);
            allPolygons.add(singlePolygon);
        }
        return allPolygons;
    }


}

