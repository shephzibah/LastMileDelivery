package com.nihanth.maproutebetweenmarkers;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.journaldev.maproutebetweenmarkers.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    //ArrayList markerPoints= new ArrayList();
    private MarkerOptions options = new MarkerOptions();
    private ArrayList<LatLng> latlngs = new ArrayList<>();
    public ArrayList<String> strings = new ArrayList<>();
    public ArrayList<Double> strings_lat = new ArrayList<>();
    public ArrayList<Double> strings_long = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Intent intent = getIntent();
        if(intent!=null){
            strings = intent.getStringArrayListExtra("coord");
            for(String i:strings){
                String[] x=i.trim().split(" ");
                //StringBuilder builder = new StringBuilder(x[0]);
                strings_lat.add(Double.parseDouble(x[0]));
                strings_long.add(Double.parseDouble(x[1]));
            }

        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //LatLng sydney = new LatLng(17.373567,  78.292635);
        //latlngs.add(sydney);
        //latlngs.add(new LatLng(17.390554, 78.356180));
        //latlngs.add(new LatLng(17.4006, 78.32333));
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 16));
        //String url = getDirectionsUrl(latlngs.get(0), latlngs.get(1));
        //String url_mod = getDirectionsUrl(latlngs.get(1), latlngs.get(2));
        for(int i=0;i<strings_lat.size();i++){
            LatLng y = new LatLng(strings_lat.get(i),strings_long.get(i));
            latlngs.add(y);
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlngs.get(0),12));
        String[] urls = new String[latlngs.size()-1];
        for(int i=0;i<urls.length;i++)
        {
            urls[i] = getDirectionsUrl(latlngs.get(i), latlngs.get(i+1)) + "&key=<KEY>";
        }
       // url=url+"&key=AIzaSyBK6C2_vlhw2pMEBrUdCfFsZ74HHB652cs";
       // url_mod=url_mod+"&key=AIzaSyBK6C2_vlhw2pMEBrUdCfFsZ74HHB652cs";
       Log.d("yo  ",urls[0]);
        for (LatLng point : latlngs) {
            int numm = latlngs.indexOf(point)+1;
            if (numm>3){
                numm=numm%3;
            }
            String num = "number"+(numm);
            int resId = getResources().getIdentifier(
                    num,
                    "drawable",
                    getPackageName());
            Bitmap x = BitmapFactory.decodeResource(getResources(),resId);
            //
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            x.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            x.recycle();
            Bitmap b = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            //profileImage.setImageBitmap(Bitmap.createScaledBitmap(b, 120, 120, false));
            options.position(point);
            options.title("Hello "+point);
            options.snippet("someDesc "+point);
            options.icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(b,120,120,false)));
            mMap.addMarker(options);
        }
       // DownloadTask downloadTask = new DownloadTask();
        //DownloadTask downloadTask1 = new DownloadTask();
        // Start downloading json data from Google Directions API
        //downloadTask.execute(url);
        //downloadTask1.execute(url_mod);


        for(int i=0;i<urls.length;i++)
        {
            DownloadTask dt = new DownloadTask();
            dt.execute(urls[i]);
        }

        //mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
        //    @Override
        /*    public void onMapClick(LatLng latLng) {

                if (markerPoints.size() > 1) {
                    markerPoints.clear();
                    mMap.clear();
                }

                // Adding new item to the ArrayList
                markerPoints.add(latLng);

                // Creating MarkerOptions
                MarkerOptions options = new MarkerOptions();

                // Setting the position of the marker
                options.position(latLng);

                if (markerPoints.size() == 1) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                } else if (markerPoints.size() == 2) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                }

                // Add new marker to the Google Map Android API V2
                mMap.addMarker(options);

                // Checks, whether start and end locations are captured
                if (markerPoints.size() >= 2) {
                    LatLng origin = (LatLng) markerPoints.get(0);
                    LatLng dest = (LatLng) markerPoints.get(1);

                    // Getting URL to the Google Directions API
                    String url = getDirectionsUrl(origin, dest);

                    DownloadTask downloadTask = new DownloadTask();

                    // Start downloading json data from Google Directions API
                    downloadTask.execute(url);
                }

            }
        });*/

    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();


            parserTask.execute(result);

        }
    }


    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d("routes",""+routes);
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            for (int i = 0; i < result.size(); i++) {
                Log.d("enter","entered");
                points = new ArrayList();
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    Log.d("debugg",""+position);

                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(Color.RED);
                lineOptions.geodesic(true);

            }


            mMap.addPolyline(lineOptions);
        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
}
