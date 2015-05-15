package com.javapapers.android;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.*;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
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

public class BackGroundService extends IntentService {
  private SharedPreferences rules;
  private SharedPreferences.Editor rulesEditor;
  private double celsius = 35;
  private double target = 2000;
  private Context con;
  private LocationManager locationManager;

  public BackGroundService() {
    super("");
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    Log.d("test11", "onHandleIntent:" + new Date());

    Date date = new Date();
    int quantity = HelperClass.getQuantity(date);
    DatabaseHandler databaseHandler = new DatabaseHandler(this);
    HydroCare hydroCare = databaseHandler.getToDayEntry(DatabaseHandler.getDateInString());
    Log.d("test22", "" + hydroCare);


    if (hydroCare == null) {
      if (Network.isConnected(getApplicationContext())) {
        find_Location(getApplicationContext());
      }
      hydroCare = new HydroCare();
      hydroCare.setTarget(target);
      hydroCare.setTemperature(celsius);
      hydroCare.setInTake(0);
      databaseHandler.addEntry(hydroCare);
    } else {
      double presentTarget = quantity * hydroCare.getTarget() / 6;
      Log.d("test22", "presentTarget" + presentTarget);
      if (presentTarget > hydroCare.getInTake()) {
        displayNotificationTwo(presentTarget - hydroCare.getInTake());
      }
    }
  }

  protected void displayNotificationTwo(double intake) {
    Log.d("test22", "notification received");
    Log.d("test22", "-----------------------------------------");
    // define sound URI, the sound to be played when there's a notification
    Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

    // Invoking the default notification service
    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);

    mBuilder.setContentTitle("Notification from HydroCare");
    mBuilder.setContentText("you are not reached target....");
    mBuilder.setTicker("HydroCare: New Message Received!");
    mBuilder.setSmallIcon(R.drawable.ic_launcher);
    mBuilder.setSound(soundUri);
    NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

    String[] events = new String[3];
    events[0] = new String("1) Reminder...!");
    events[1] = new String("2) you have to drink" + intake + "ml water to reach your target");
    events[2] = new String("3) from HydroCare");

    // Sets a title for the Inbox style big view
    inboxStyle.setBigContentTitle("More Details:");
    // Moves events into the big view
    for (int i = 0; i < events.length; i++) {
      inboxStyle.addLine(events[i]);
    }
    mBuilder.setStyle(inboxStyle);

    // Increase notification number every time a new notification arrives
    mBuilder.setNumber(1);

    // when the user presses the notification, it is auto-removed
    mBuilder.setAutoCancel(true);

    // Creates an implicit intent
    Intent resultIntent = new Intent("com.example.javacodegeeks.TEL_INTENT",
        Uri.parse("tel:123456789"));
    resultIntent.putExtra("from", "javacodegeeks");

    TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
    stackBuilder.addParentStack(NotificationTwo.class);

    stackBuilder.addNextIntent(resultIntent);
    PendingIntent resultPendingIntent =
        stackBuilder.getPendingIntent(
            0,
            PendingIntent.FLAG_ONE_SHOT
        );
    mBuilder.setContentIntent(resultPendingIntent);

    NotificationManager myNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

    myNotificationManager.notify(11, mBuilder.build());

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
        String address = ConvertPointToLocation(latitude, longitude);
        Log.d("test22", "address:" + address);
        getTemperature(address);

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

        for (int index = 0; index <= addresses.get(0)
            .getMaxAddressLineIndex(); index++)
//          if ((maxIndex - 1) <= index)
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
      Log.d("test22", "result:" + result);
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
