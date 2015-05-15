package com.javapapers.android;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.javapapers.android.model.HydroCare;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by ehc on 14/5/15.
 */
public class HydroCareActivity extends Activity implements View.OnClickListener {
  private Button mButton_200;
  private Button mButton_300;
  private Button mButton_400;
  private Button mButton_500;
  private double target = 2000;
  private double inTake;
  private ProgressBar mProgressBar;
  private DatabaseHandler databaseHandler;
  private TextView mTarget;
  private TextView mRemaining;


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.hydro_care);
    SharedPreferences sharedPreferences = getSharedPreferences("hydrocare", MODE_PRIVATE);
    SharedPreferences.Editor editor = sharedPreferences.edit();
    getWidgets();
    applyListeners();
    mTarget.setText("Target: " + target);
    databaseHandler = new DatabaseHandler(this);
    Date date = new Date();
    HelperClass.setAlarm(getApplicationContext(), 0, HelperClass.getAlarmStartTime(new Date()).getTime(), 1000 * 60 * 60 * 2, true);
    HelperClass.setAlarm(getApplicationContext(), 0, HelperClass.getTemperatureAlarmStartTime(date,false).getTime(), 1000 * 60 * 10, false);
    getNewProgress(0);
    getWeekData();
  }

  private void applyListeners() {
    mButton_200.setOnClickListener(this);
    mButton_300.setOnClickListener(this);
    mButton_400.setOnClickListener(this);
    mButton_500.setOnClickListener(this);
  }

  private void getWidgets() {
    mTarget = (TextView) findViewById(R.id.target);
    mRemaining = (TextView) findViewById(R.id.remaining);
    mProgressBar = (ProgressBar) findViewById(R.id.progress);
    mButton_200 = (Button) findViewById(R.id.button_200);
    mButton_300 = (Button) findViewById(R.id.button_300);
    mButton_400 = (Button) findViewById(R.id.button_400);
    mButton_500 = (Button) findViewById(R.id.button_500);
  }

  @Override
  public void onClick(View view) {
    double newProgress = 0;
    switch (view.getId()) {
      case R.id.button_200:
        newProgress = getNewProgress(200);
        break;
      case R.id.button_300:
        newProgress = getNewProgress(300);
        break;
      case R.id.button_400:
        newProgress = getNewProgress(400);
        break;
      case R.id.button_500:
        newProgress = getNewProgress(500);
        break;
    }
    mProgressBar.setProgress((int) newProgress);
  }
  public void getWeekData(){
    double[] weekIntakes=new double[7];
    ArrayList<HydroCare> hydroCares=databaseHandler.getCurrentWeek();
    for(HydroCare hydroCare:hydroCares){
      weekIntakes[hydroCare.getDateOfEntry().getDay()] =hydroCare.getInTake();
    }
  }

  private double getNewProgress(int inTake) {
    double newProgress;
    double dbInTake = databaseHandler.getInTake(DatabaseHandler.getDateInString());
    Log.d("test11", "prevIntake:" + dbInTake);
    newProgress = (dbInTake + inTake) * 100 / target;
    if (newProgress >= 100)
      newProgress = 100;
    Log.d("test11", "newIntake:" + (dbInTake + inTake));
    updateIntake(dbInTake + inTake);
    Log.d("test11", "Remaining:" + (target - dbInTake - inTake));
    if ((target - dbInTake - inTake) < 0)
      mRemaining.setText("Remaining:0");
    else
      mRemaining.setText("Remaining:" + (target - dbInTake - inTake));
    return newProgress;
  }

  private void updateIntake(double inTake) {
    databaseHandler.updateInTake(DatabaseHandler.getDateInString(), inTake);
  }
}
