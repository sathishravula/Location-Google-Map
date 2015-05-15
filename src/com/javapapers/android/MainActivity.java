package com.javapapers.android;

import android.content.Context;
import android.graphics.Color;
import android.location.*;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.*;
import com.google.gson.Gson;
import com.javapapers.android.model.Example;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.apache.http.Header;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends FragmentActivity implements LocationListener {
  private static final LatLng LOWER_MANHATTAN = new LatLng(40.722543,
      -73.998585);
  private static final LatLng TIMES_SQUARE = new LatLng(40.7577, -73.9857);
  private static final LatLng BROOKLYN_BRIDGE = new LatLng(40.7057, -73.9964);
  private static final LatLng PS = new LatLng(17.439833, 78.39613570000006);
  private static final LatLng EHC = new LatLng(17.441460, 78.398411);
  private GoogleMap googleMap;
  private LocationManager locationManager;
  private Marker marker;
  private Context con;
  private double celsius;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    setUpMapIfNeeded();
    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
        1000 * 10, 1, this);

    find_Location(getApplicationContext());
  }

  private void setUpMapIfNeeded() {
    // check if we have got the googleMap already
    if (googleMap == null) {
      googleMap = ((SupportMapFragment) getSupportFragmentManager()
          .findFragmentById(R.id.map)).getMap();
      if (googleMap != null) {
        addLines();
//				locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
      }
    }
  }

  private void addLines() {

    googleMap
        .addPolyline((new PolylineOptions())
            .add(PS, EHC).width(5).color(Color.BLUE)
            .geodesic(true));

    marker = googleMap.addMarker(new MarkerOptions()
        .position(EHC)
        .title("EHC")
        .snippet("Population: 25 Employees")
        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ehc)));
    // move camera to zoom on map
    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(EHC,
        13));
  }


  @Override
  public void onLocationChanged(Location location) {
    Log.d("test1", "Latitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude() + ", Time: " + new Date());
    String msg = "New Latitude: " + location.getLatitude()
        + "New Longitude: " + location.getLongitude();

    Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
  }

  @Override
  public void onProviderDisabled(String provider) {
    Log.d("Latitude", "disable");
  }

  @Override
  public void onProviderEnabled(String provider) {
    Log.d("Latitude", "enable");
  }

  @Override
  public void onStatusChanged(String provider, int status, Bundle extras) {
    Log.d("Latitude", "status");
  }

  public void find_Location(Context con) {
    Log.d("Find Location", "in find_location");
    this.con = con;
    String location_context = Context.LOCATION_SERVICE;
    locationManager = (LocationManager) con.getSystemService(location_context);

    List<String> providers = locationManager.getProviders(true);
    for (String provider : providers) {
      locationManager.requestLocationUpdates(provider, 1000, 0, new LocationListener() {
        public void onLocationChanged(Location location) {
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
      });
      Location location = locationManager.getLastKnownLocation(provider);
      if (location != null) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
//        String addr = ConvertPointToLocation(18.363853, 79.279025);
//        String addr = ConvertPointToLocation(17.439896, 78.396051);
//        String addr = ConvertPointToLocation(18.365354, 79.274332);
        String addr = ConvertPointToLocation(16.545837, 81.492541);
//        String addr = ConvertPointToLocation(latitude, longitude);
        Log.d("test11", "address:" + addr);
        SendToUrl(addr);

      }
    }
  }

  public String ConvertPointToLocation(double pointlat, double pointlog) {

    String address = "";
    Geocoder geoCoder = new Geocoder(con,
        Locale.getDefault());
    try {
      List<Address> addresses = geoCoder.getFromLocation(pointlat, pointlog, 1);
      if (addresses.size() > 0) {
        int maxIndex = addresses.get(0).getMaxAddressLineIndex();

        for (int index = 1; index <= addresses.get(0)
            .getMaxAddressLineIndex(); index++)
          if ((maxIndex - 1) <= index)
            address += addresses.get(0).getAddressLine(index) + " ";
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return address;
  }

  private void SendToUrl(String string) {
    // TODO Auto-generated method stub
    AsyncHttpClient client = new AsyncHttpClient();
    RequestParams params = new RequestParams();
    params.add("q", "select item from weather.forecast where woeid in (select woeid from geo.places(1) where text='" + string + "')");
    params.add("format", "json");
    client.get("https://query.yahooapis.com/v1/public/yql", params, new AsyncHttpResponseHandler() {
      @Override
      public void onSuccess(int i1, Header[] headers, byte[] bytes) {
        Example example = new Gson().fromJson(new String(bytes), Example.class);
        String temp = example.getQuery().getResults().getChannel().getItem().getCondition().getTemp();

        int fahrenheit = Integer.parseInt(temp);
        Log.d("test11", "fahrenheit:" + fahrenheit);
        celsius = (fahrenheit - 32)* (5/9d);
        Log.d("test11", "celsius:" + celsius);
      }

      @Override
      public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
        Log.d("test11", "onFailure");
      }
    });

  }


  public String convertStreamToString(InputStream is) {
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    StringBuffer stringBuffer = new StringBuffer();
    String line = null;
    try {
      while ((line = reader.readLine()) != null) {
        stringBuffer.append(line + "\n");
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        is.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return stringBuffer.toString();
  }
}
//select * from weather.forecast where woeid in (select woeid from geo.places(1) where text="nome, ak")
//    https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22nome%2C%20ak%22)&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys
