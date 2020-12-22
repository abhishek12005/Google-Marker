package com.example.googlewaypoint;

import androidx.fragment.app.FragmentActivity;
import retrofit2.Call;
import retrofit2.http.GET;

import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private List<Marker> markers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        final Button clearbutton = findViewById(R.id.button_id_clear);
        final Button waypointbutton = findViewById(R.id.button_id_find);
        clearbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mMap.clear();
                markers.clear();
            }
        });
        waypointbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                LatLng origin=new LatLng(0,0);
                LatLng destination=new LatLng(0,0);
                List<LatLng> waypoints=new ArrayList<>();
                if(markers.size()>0) {
                    Integer i=0;
                    for (Marker marker : markers) {
                        if(i==0)
                        {
                            origin=marker.getPosition();
                        }
                        else if(i==markers.size()-1)
                        {
                            destination=marker.getPosition();
                        }
                        else
                        {
                            waypoints.add(marker.getPosition());
                        }
                        i++;
                    }
                }
                if(markers.size()>1)
                {
                    Integer i=0;
                    for (Marker marker : markers) {
                        if(i!=markers.size()-1) {
                            Polyline line = mMap.addPolyline(new PolylineOptions()
                                    .add(marker.getPosition(), markers.get(i+1).getPosition())
                                    .width(5)
                                    .color(Color.RED));
                        }
                        i++;
                    }
                    //Commented Code for finding paths through Google Maps Directions API
//                    String url=getMapsApiDirectionsUrl(origin,destination,waypoints);
//                    OkHttpClient client = new OkHttpClient();
//
//                    Request request = new Request.Builder()
//                            .url(url)
//                            .build();
//                    try {
//                        Response response = client.newCall(request).execute();
//                        drawPath(response.body().string());
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                }
                else {

                }
            }
        });
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                Marker tagMarker;

                // Creating a marker
                MarkerOptions markerOptions = new MarkerOptions();

                // Setting the position for the marker
                markerOptions.position(latLng);

                // Setting the title for the marker.
                // This will be displayed on taping the marker
                markerOptions.title(latLng.latitude + " : " + latLng.longitude);

                // Animating to the touched position
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                // Placing a marker on the touched position
                tagMarker=mMap.addMarker(markerOptions);
                markers.add(tagMarker);
            }
        });
    }

//    private String getMapsApiDirectionsUrl(LatLng originpoint,LatLng destinationpoint,List<LatLng> waypointslist) {
//        String origin = "origin=" + originpoint.latitude + "," + originpoint.longitude;
//        String waypoints = "waypoints=";
//        for(LatLng list:waypointslist) {
//            waypoints = waypoints + list.latitude + "," + list.longitude + "|";
//        }
//        String destination = "destination=" + destinationpoint.latitude + "," + destinationpoint.longitude;
//
//        String sensor = "sensor=false";
//        String key = "key="+R.string.google_maps_key;
//        String params = origin + "&" + waypoints + "&"  + destination + "&" + sensor+ "&" +key;
//        String output = "json";
//        String url = "https://maps.googleapis.com/maps/api/directions/"
//                + output + "?" + params;
//        return url;
//    }

//    public void drawPath(String result) {
//
//        try {
//            //Tranform the string into a json object
//            final JSONObject json = new JSONObject(result);
//            JSONArray routeArray = json.getJSONArray("routes");
//            JSONObject routes = routeArray.getJSONObject(0);
//            JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
//            String encodedString = overviewPolylines.getString("points");
//            List<LatLng> list = decodePoly(encodedString);
//
//            Polyline line = mMap.addPolyline(new PolylineOptions()
//                    .addAll(list)
//                    .width(12)
//                    .color(Color.parseColor("#05b1fb"))//Google maps blue color
//                    .geodesic(true)
//            );
//
//        } catch (JSONException e) {
//                e.printStackTrace();
//        }
//    }

//    private List<LatLng> decodePoly(String encoded) {
//
//        List<LatLng> poly = new ArrayList<LatLng>();
//        int index = 0, len = encoded.length();
//        int lat = 0, lng = 0;
//
//        while (index < len) {
//            int b, shift = 0, result = 0;
//            do {
//                b = encoded.charAt(index++) - 63;
//                result |= (b & 0x1f) << shift;
//                shift += 5;
//            } while (b >= 0x20);
//            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
//            lat += dlat;
//
//            shift = 0;
//            result = 0;
//            do {
//                b = encoded.charAt(index++) - 63;
//                result |= (b & 0x1f) << shift;
//                shift += 5;
//            } while (b >= 0x20);
//            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
//            lng += dlng;
//
//            LatLng p = new LatLng((((double) lat / 1E5)),
//                    (((double) lng / 1E5)));
//            poly.add(p);
//        }
//
//        return poly;
//    }
}


