package com.javapapers.android;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.*;
import android.os.Bundle;
import android.util.Log;
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
import java.util.*;

/**
 * Created by ehc on 15/5/15.
 */
public class HelperClass {
  private Context con;
  private LocationManager locationManager;

  public static Date getAlarmStartTime(Date date) {
    int hour = 9;
    int day = date.getDate();
    switch (date.getHours()) {
      case 9:
      case 10:
        hour = 11;
        break;
      case 11:
      case 12:
        hour = 13;
        break;
      case 13:
      case 14:
        hour = 15;
        break;
      case 15:
      case 16:
        hour = 17;
        break;
      case 17:
      case 18:
        hour = 19;
        break;
      case 19:
      case 20:
        hour = 21;
        break;
      case 21:
      case 22:
      case 23:
        day = day + 1;
        break;
    }
    date.setDate(day);
    date.setHours(hour);
    date.setMinutes(0);
    date.setSeconds(0);
    return date;
  }


  public static Date getTemperatureAlarmStartTime(Date date, boolean flag) {
    int hour = 8;
    int day = date.getDate();
    switch (date.getHours()) {
      case 8:
        if (date.getMinutes() > 50 || flag)
          day = day + 1;
        break;
      case 9:
      case 10:
      case 11:
      case 12:
      case 13:
      case 14:
      case 15:
      case 16:
      case 17:
      case 18:
      case 19:
      case 20:
      case 21:
      case 22:
      case 23:
        day = day + 1;
        break;
    }
    date.setDate(day);
    date.setHours(hour);
    date.setMinutes(0);
    date.setSeconds(0);
    return date;
  }

  public static int getQuantity(Date date) {
    int quantity = 1;
    switch (date.getHours()) {
      case 8:
        break;
      case 9:
      case 10:
        break;
      case 11:
      case 12:
        quantity = 1;
        break;
      case 13:
      case 14:
        quantity = 2;
        break;
      case 15:
      case 16:
        quantity = 3;
        break;
      case 17:
      case 18:
        quantity = 4;
        break;
      case 19:
      case 20:
        quantity = 5;
        break;
      case 21:
        quantity = 6;
        break;
    }
    return quantity;
  }

  public static void setAlarm(Context mContext, int requestCode, long startTime, long interval, boolean flag) {
    Intent myIntent = null;
    if (flag)
      myIntent = new Intent(mContext, BackGroundService.class);
    else
      myIntent = new Intent(mContext, TemperatureService.class);

    PendingIntent pendingIntent = PendingIntent.getService(mContext, requestCode, myIntent, 0);

    cancelAlarmIfExists(mContext, requestCode, myIntent);

    AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, startTime, interval, pendingIntent);
  }

  public static void cancelAlarmIfExists(Context mContext, int requestCode, Intent intent) {
    try {
      PendingIntent pendingIntent = PendingIntent.getService(mContext, requestCode, intent, 0);
      AlarmManager am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
      am.cancel(pendingIntent);
    } catch (Exception e) {
      e.printStackTrace();
    }
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
//        celsius = (fahrenheit - 32) * (5 / 9d);
//        Log.d("test11", "celsius:" + celsius);
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
