package com.javapapers.android;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.*;
import android.os.Bundle;
import android.util.Log;
import com.google.gson.Gson;
import com.javapapers.android.model.Example;
import com.javapapers.android.model.HydroCare;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * Created by ehc on 5/3/15.
 */
public class TemperatureService extends IntentService {

  private Context con;
  private LocationManager locationManager;
  private double celsius = 35;
  private DatabaseHandler databaseHandler;
  private double target = 2000;

  public TemperatureService() {
    super("");
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    Log.d("test22", "TemperatureService:" + new Date());
    boolean isTemperatureAvailable = false;
    DatabaseHandler databaseHandler = new DatabaseHandler(getApplicationContext());
    HydroCare hydroCare = databaseHandler.getToDayEntry(DatabaseHandler.getDateInString());
    if (hydroCare == null) {
      if (Network.isConnected(getApplicationContext())) {
        find_Location(getApplicationContext());
        hydroCare = new HydroCare();
        hydroCare.setTarget(target);
        hydroCare.setTemperature(celsius);
        hydroCare.setInTake(0);
        databaseHandler.addEntry(hydroCare);
        isTemperatureAvailable = true;
      }
    } else {
      isTemperatureAvailable = true;
    }
    if (isTemperatureAvailable)
      HelperClass.setAlarm(getApplicationContext(), 0, HelperClass.getTemperatureAlarmStartTime(new Date(), isTemperatureAvailable).getTime(), 1000 * 60 * 10, false);
  }


  public void find_Location(Context con) {
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
        String addr = ConvertPointToLocation(latitude, longitude);
        Log.d("test11", "address:" + addr);
        getTemperature(addr);

        break;
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


  public void getTemperature(String string) {
    List<NameValuePair> params = new LinkedList<NameValuePair>();
    params.add(new BasicNameValuePair("q", "select item from weather.forecast where woeid in (select woeid from geo.places(1) where text='" + string + "')"));
    params.add(new BasicNameValuePair("format", "json"));
    String paramString = URLEncodedUtils.format(params, "utf-8");
    HttpGet httpGet = new HttpGet("https://query.yahooapis.com/v1/public/yql" + "?" + paramString);
    DefaultHttpClient client = new DefaultHttpClient();
    HttpResponse httpresponse = null;
    try {
      httpresponse = client.execute(httpGet);
      HttpEntity entity = httpresponse.getEntity();
      InputStream stream = entity.getContent();
      String result = convertStreamToString(stream);
      Log.d("test11", "result:" + result);

      Example example = new Gson().fromJson(result, Example.class);
      String temp = "";
      try {
        temp = example.getQuery().getResults().getChannel().getItem().getCondition().getTemp();
        int fahrenheit = Integer.parseInt(temp);
        Log.d("test22", "fahrenheit:" + fahrenheit);
        celsius = (fahrenheit - 32) * (5 / 9d);
        Log.d("test22", "celsius:" + celsius);
      } catch (Exception e) {
        Log.d("test22", "celsius in error:" + celsius);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
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
      return stringBuffer.toString();
    }
  }

}
