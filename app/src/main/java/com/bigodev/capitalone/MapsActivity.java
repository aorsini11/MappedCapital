package com.bigodev.capitalone;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.reimaginebanking.api.java.NessieClient;
import com.reimaginebanking.api.java.NessieException;
import com.reimaginebanking.api.java.NessieResultsListener;
import com.reimaginebanking.api.java.models.Merchant;
import com.reimaginebanking.api.java.models.Purchase;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class MapsActivity extends FragmentActivity implements GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    public static ArrayList<Purchase> allPurchases;
    public static String userId = "5712b37c01c7065b0fceb51f";
    public static ArrayList<Marker> markerList;
    public static HashMap<String,LatLng> locations;
    public static HashMap<String,String> vendorNames;
    public static HashMap<String,Integer> purchaseCount;
    public static HashMap<String,Double> purchaseTotal;
    NessieClient nessieClient;
    Button options;
    Button summary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        nessieClient = NessieClient.getInstance();
        nessieClient.setAPIKey("03ad007932e02fcbd509126943e65840");
        options = (Button) findViewById(R.id.options_button);
        summary = (Button) findViewById(R.id.summary_button);

        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater layoutInflater
                        = (LayoutInflater)getBaseContext()
                        .getSystemService(LAYOUT_INFLATER_SERVICE);
                final View popupView = layoutInflater.inflate(R.layout.popup, null);
                final PopupWindow popupWindow = new PopupWindow(
                        popupView,
                        ViewPager.LayoutParams.WRAP_CONTENT,
                        ViewPager.LayoutParams.WRAP_CONTENT);

                popupWindow.setFocusable(true);
                popupWindow.update();

                Button btnDismiss = (Button)popupView.findViewById(R.id.dismiss);
                Button filterDate = (Button)popupView.findViewById(R.id.date_button);
                Button filterCost = (Button) popupView.findViewById(R.id.cost_button);
                Button filterType = (Button) popupView.findViewById(R.id.category_button);
                Button clearFilters = (Button) popupView.findViewById(R.id.clear);

                filterDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMap.clear();
                        markerList = null;
                        EditText date1 = (EditText) popupView.findViewById(R.id.date1);
                        EditText date2 = (EditText) popupView.findViewById(R.id.date2);
                        String key = date1.getText().toString() + "," + date2.getText().toString();
                        ArrayList<Purchase> purchases = filter(allPurchases,"purchaseDate",key);
                        loadMarkers(purchases);
                    }
                });

                filterCost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMap.clear();
                        markerList = null;
                        EditText cost1 = (EditText) popupView.findViewById(R.id.cost1);
                        EditText cost2 = (EditText) popupView.findViewById(R.id.cost2);
                        String key = cost1.getText().toString() + "," + cost2.getText().toString();
                        ArrayList<Purchase> purchases = filter(allPurchases,"amount",key);
                        loadMarkers(purchases);
                    }
                });

                filterType.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMap.clear();
                        markerList = null;
                        EditText text = (EditText) popupView.findViewById(R.id.category_text);
                        ArrayList<Purchase> purchases = filter(allPurchases,"category",text.getText().toString());
                        loadMarkers(purchases);
                    }
                });

                clearFilters.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadMarkers();
                    }
                });

                btnDismiss.setOnClickListener(new Button.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        popupWindow.dismiss();
                    }});
                popupWindow.showAsDropDown(options, 50, -30);
            }});

        summary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MapsActivity.this,Summary.class);
                startActivity(i);
            }
        });

        setUpMapIfNeeded();
    }


    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        if(allPurchases!=null && locations!=null && purchaseCount!=null){
            loadMarkers();
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     *
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }*/

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                mMap.setOnMarkerClickListener(this);
                mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

                    @Override
                    public void onMapLongClick(LatLng latLng) {
                        if(markerList!=null) {
                            for (Marker marker : markerList) {
                                if (Math.abs(marker.getPosition().latitude - latLng.latitude) < 0.05 && Math.abs(marker.getPosition().longitude - latLng.longitude) < 0.05) {
                                    Intent intent = new Intent();
                                    intent.setAction(Intent.ACTION_SEND);
                                    intent.setType("text/plain");
                                    String text = "Check out " + marker.getTitle() + "! At the location " + marker.getPosition().latitude + "," + marker.getPosition().longitude;
                                    intent.putExtra(Intent.EXTRA_TEXT, text);
                                    startActivity(Intent.createChooser(intent, "Share"));
                                    break;
                                }
                            }
                        }

                    }
                });
                setUpMap();
            }
        }
        if(locations == null){
            locations = new HashMap<String,LatLng>();
            vendorNames = new HashMap<String,String>();
            loadLocations();
        }
        if(allPurchases == null){
            loadPurchases(userId);
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */


    private void setUpMap() {
        LatLng currLocation = new LatLng(40.5251330,-74.4409000);
        CameraUpdate currPos = CameraUpdateFactory.newLatLngZoom(new LatLng(currLocation.latitude, currLocation.longitude), 16.0f);
        mMap.moveCamera(currPos);
        //mMap.addMarker(new MarkerOptions().position(new LatLng(currLocation.latitude, currLocation.longitude)).title("Current Location"));
        //addDefaultMarkers();
        mMap.setMyLocationEnabled(true);
        UiSettings currSettings = mMap.getUiSettings();
        currSettings.setZoomControlsEnabled(true);
        currSettings.setCompassEnabled(true);
        currSettings.setMyLocationButtonEnabled(true);
        //addHeatMap();

    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        marker.showInfoWindow();

        //Toast.makeText(this,"The distance to this marker is km",Toast.LENGTH_LONG).show();
        return true;
    }

    public void loadPurchases(String id){
        nessieClient.getPurchases(id, new NessieResultsListener() {
            @Override
            public void onSuccess(Object o, NessieException e) {
                ArrayList<Purchase> purchases = (ArrayList<Purchase>) o;
                allPurchases = purchases;
                if(purchaseCount==null){
                    purchaseCount = new HashMap<String, Integer>();
                    purchaseTotal = new HashMap<String, Double>();
                }
                for(Purchase purchase : allPurchases){
                    if(purchaseCount.containsKey(purchase.getMerchant_id())){
                        purchaseCount.put(purchase.getMerchant_id(),purchaseCount.get(purchase.getMerchant_id()) + 1);
                    }
                    else{
                        purchaseCount.put(purchase.getMerchant_id(),1);
                    }
                    if(purchaseTotal.containsKey(purchase.getMerchant_id())){
                        purchaseTotal.put(purchase.getMerchant_id(),purchaseTotal.get(purchase.getMerchant_id()) + purchase.getAmount());
                    }
                    else{
                        purchaseTotal.put(purchase.getMerchant_id(),purchase.getAmount());
                    }
                }
                if(allPurchases!=null && locations!=null){
                    loadMarkers();
                }
            }
        });
    }

    public void loadLocations(){
        nessieClient.getMerchants(new NessieResultsListener() {
            @Override
            public void onSuccess(Object o, NessieException e) {
                ArrayList<Merchant> merchants = (ArrayList<Merchant>) o;
                for (Merchant merchant : merchants){
                    if(merchant.get_id()==null){
                        continue;
                    }
                    else {
                        if (!locations.containsKey(merchant.get_id())) {
                            if(merchant.getGeocode()==null){
                                continue;
                            }
                            locations.put(merchant.get_id(), new LatLng(merchant.getGeocode().getLat(), merchant.getGeocode().getLng()));
                            //Toast.makeText(MapsActivity.this, "Success", Toast.LENGTH_SHORT).show();
                        }
                        if(!vendorNames.containsKey(merchant.get_id())) {
                            if(merchant.getName()!=null) {
                                vendorNames.put(merchant.get_id(), merchant.getName());
                            }
                            else{
                                vendorNames.put(merchant.get_id(), "Vendor");
                            }
                        }
                    }
                }
                if(allPurchases!=null && locations!=null && purchaseCount!=null){
                    loadMarkers();
                }
            }
        });
    }

    public void loadMarkers(){
        //HashMap<String,Boolean> placed = new HashMap<>();
        for(Purchase purchase : allPurchases){
            markerList = new ArrayList<Marker>();
            LatLng temp = locations.get(purchase.getMerchant_id());
            if(temp == null){
                continue;
            }
            String name = "";
            String description = purchaseCount.get(purchase.getMerchant_id()) + " purchases    $" +
                    purchaseTotal.get(purchase.getMerchant_id()) + " spent";
            name = vendorNames.get(purchase.getMerchant_id());
            int count = purchaseCount.get(purchase.getMerchant_id());
            Marker marker = null;
            if(count>7){
                marker = mMap.addMarker(new MarkerOptions().position(temp)
                        .title(name)
                        .snippet(description)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            }
            else if(count>3) {
                marker = mMap.addMarker(new MarkerOptions().position(temp)
                        .title(name)
                        .snippet(description)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
            }
            else{
                marker = mMap.addMarker(new MarkerOptions().position(temp)
                        .title(name)
                        .snippet(description)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            }
            markerList.add(marker);
        }
    }

    public void loadMarkers(ArrayList<Purchase> purchaseList){
        for(Purchase purchase : purchaseList){
            markerList = new ArrayList<Marker>();
            LatLng temp = locations.get(purchase.getMerchant_id());
            if(temp == null){
                continue;
            }
            String name = "";
            String description = purchaseCount.get(purchase.getMerchant_id()) + " purchases    $" +
                    purchaseTotal.get(purchase.getMerchant_id()) + " spent";
            name = vendorNames.get(purchase.getMerchant_id());
            int count = purchaseCount.get(purchase.getMerchant_id());
            Marker marker = null;
            if(count>7){
                marker = mMap.addMarker(new MarkerOptions().position(temp)
                        .title(name)
                        .snippet(description)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            }
            else if(count>3) {
                marker = mMap.addMarker(new MarkerOptions().position(temp)
                        .title(name)
                        .snippet(description)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
            }
            else{
                marker = mMap.addMarker(new MarkerOptions().position(temp)
                        .title(name)
                        .snippet(description)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            }
            markerList.add(marker);
        }
    }

    public ArrayList<Purchase> filter(ArrayList<Purchase> purchases, String filterType, String filterKey){
        ArrayList<Purchase> filteredPurchases = new ArrayList<Purchase>();
        for(int i=0;i<purchases.size();i++){
            if(filterType.equals("category")){
                if(purchases.get(i).getDescription().equalsIgnoreCase(filterKey)){
                    filteredPurchases.add(purchases.get(i));
                }
            } else if(filterType.equals("purchaseDate")){
                // filter key format: yyyy-mm-dd,yyyy-mm-dd
                Date dateLow = new Date(filterKey.substring(0,10));
                Date dateHigh = new Date(filterKey.substring(11));
                Date purchaseDate= new Date(purchases.get(i).getPurchase_date());
                if(purchaseDate.compare(dateLow)>=0 && purchaseDate.compare(dateHigh)<=0){
                    filteredPurchases.add(purchases.get(i));
                }
            } else if(filterType.equals("amount")){
                Scanner scanner = new Scanner(filterKey);
                scanner.useDelimiter(",");
                double aLow=scanner.nextDouble();
                scanner.skip(",");
                double aHigh=scanner.nextDouble();
                scanner.close();
                double amount=purchases.get(i).getAmount();
                if(amount <= aHigh && amount >= aLow){
                    filteredPurchases.add(purchases.get(i));
                }
            }

        }
        return filteredPurchases;
    }

    /*
    public String LoadRequest(String userId){

        String result = "";
        HttpURLConnection c = null;
        String url = "http://api.reimaginebanking.com/accounts/" + userId + "/purchases?key=03ad007932e02fcbd509126943e65840";
        try {
            URL u = new URL(url);
            c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("GET");
            c.setRequestProperty("Content-length", "0");
            c.setUseCaches(false);
            c.setAllowUserInteraction(false);
            c.setConnectTimeout(10000);
            c.setReadTimeout(10000);
            c.connect();
            int status = c.getResponseCode();

            switch (status) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line+"\n");
                    }
                    result = sb.toString();
                    br.close();

            }

        } catch (MalformedURLException ex) {
            //Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            //Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (c != null) {
                try {
                    c.disconnect();
                } catch (Exception ex) {
                    //Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                }
            }
            return result;
        }
    }

    public ArrayList<Purchase> parsePurchases(String json){
        ArrayList<Purchase> purchases = new ArrayList<>();

        JSONParser parser = new JSONParser();
        try{
            JSONArray array = (JSONArray) parser.parse(json);
            for (int i=0;i<array.size();i++){
                JSONObject jsonObj=(JSONObject) array.get(i);
                String id = jsonObj.getString("_id");
                String merchantId = jsonObj.getString("merchant_id");
                String payerId = jsonObj.getString("payer_id");
                String date = jsonObj.getString("purchase_date");
                double amount = jsonObj.getDouble("amount");
                String description = jsonObj.getString("description");

                Purchase temp = new Purchase(id,merchantId,payerId,date,amount,description);
                purchases.add(temp);
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return purchases;
    }


    public void updatePurchaseMarkers(ArrayList<Purchase> purchases){
        for(Purchase purchase:purchases){

        }
    }

    public String getLatLng(String id){
        HttpURLConnection c = null;
        String result = null;
        String url = "http://api.reimaginebanking.com/merchants/"+id +"?key=03ad007932e02fcbd509126943e65840";
        try {
            URL u = new URL(url);
            c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("GET");
            c.setRequestProperty("Content-length", "0");
            c.setUseCaches(false);
            c.setAllowUserInteraction(false);
            c.setConnectTimeout(10000);
            c.setReadTimeout(10000);
            c.connect();
            int status = c.getResponseCode();

            switch (status) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line+"\n");
                    }
                    result = sb.toString();
                    br.close();

            }

        } catch (MalformedURLException ex) {
            //Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            //Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (c != null) {
                try {
                    c.disconnect();
                } catch (Exception ex) {
                    //Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                }
            }
            return result;
        }
    }

    public HashMap<String,LatLng> parseLatLng(String json){
        JSONParser parser = new JSONParser();
        HashMap<String,LatLng> result = new HashMap<>();
        try{
            JSONObject jobj = (JSONObject) parser.parse(json);
            String id = jobj.getString("_id");
            JSONObject geocode = (JSONObject) jobj.get("geocode");
            double latitude = geocode.getDouble("lat");
            double longitude = geocode.getDouble("lng");
            result.put(id,new LatLng(latitude,longitude));
        }catch(Exception e){
            e.printStackTrace();
        }

        return result;
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return LoadRequest(userId);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            ArrayList<Purchase> purchases = parsePurchases(result);
            allPurchases = purchases;
        }
    }

    private class HttpAsyncTask2 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            //return getLatLng(urls[0]);
            return "";
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
           // HashMap<String,LatLng> temp = parseLatLng(result);
            //if(temp)
        }
    }
*/
}

