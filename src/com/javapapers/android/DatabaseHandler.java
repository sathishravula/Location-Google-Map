package com.javapapers.android;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.javapapers.android.model.HydroCare;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ehc on 9/3/15.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

  // All Static variables
  // Database Version
  private static final int DATABASE_VERSION = 1;

  // Database Name
  private static final String DATABASE_NAME = "eKinCare";

  // Contacts table name
  private static final String TABLE_HYDROCARE = "HydroCare";

  // Contacts Table Columns names
  private static final String DATE_OF_ENTRY = "date_of_entry";
  private static final String STATUS = "status";
  private static final String TARGET = "target";
  private static final String INTAKE = "intake";
  private static final String TEMPERATURE = "temperature";

  public DatabaseHandler(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  // Creating Tables
  @Override
  public void onCreate(SQLiteDatabase db) {
    String CREATE_HYDRO_CARE_TABLE = "CREATE TABLE " + TABLE_HYDROCARE + "(" + DATE_OF_ENTRY + " DATE PRIMARY KEY," + TARGET
        + " REAL," + INTAKE + " REAL,"
        + TEMPERATURE + " REAL," + STATUS + " INTEGER DEFAULT 0)";

    db.execSQL(CREATE_HYDRO_CARE_TABLE);
  }

  // Upgrading database
  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    // Drop older table if existed
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_HYDROCARE);
    // Create tables again
    onCreate(db);
  }

  /**
   * All CRUD(Create, Read, Update, Delete) Operations
   */

  long addEntry(HydroCare hydroCare) {
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put(DATE_OF_ENTRY, getDateInString());
    values.put(INTAKE, hydroCare.getInTake());
    values.put(TARGET, hydroCare.getTarget());
    values.put(TEMPERATURE, hydroCare.getTemperature());
    long rowId = db.insert(TABLE_HYDROCARE, null, values);
    db.close();
    return rowId;
  }

  private Date getDate(String date) {
    SimpleDateFormat dateFormat = new SimpleDateFormat(
        "MM-dd-yyyy", Locale.getDefault());
    try {
      return dateFormat.parse(date);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static String getDateInString() {
    SimpleDateFormat dateFormat = new SimpleDateFormat(
//        "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        "yyyy-MM-dd", Locale.getDefault());
    Date date = new Date();
    return dateFormat.format(date);
  }

  public ArrayList<HydroCare> getAllSMS() {
    ArrayList<HydroCare> smsArrayList = new ArrayList<HydroCare>();
    String selectQuery = "SELECT  * FROM " + TABLE_HYDROCARE;
    SQLiteDatabase db = this.getWritableDatabase();
    Cursor cursor = db.rawQuery(selectQuery, null);
    if (cursor.moveToFirst()) {
      do {
        HydroCare hydroCare = new HydroCare();
        hydroCare.setDateOfEntry(getDate(cursor.getString(0)));
        hydroCare.setTarget(cursor.getDouble(1));
        hydroCare.setInTake(cursor.getDouble(2));
        hydroCare.setTemperature(cursor.getDouble(3));
        hydroCare.setStatus(cursor.getInt(4));
        smsArrayList.add(hydroCare);
      } while (cursor.moveToNext());
    }
    cursor.close();
    return smsArrayList;
  }


  public int getOverAllSMSCount() {
    String selectQuery = "SELECT  * FROM " + TABLE_HYDROCARE + " where status=1";
    SQLiteDatabase db = this.getWritableDatabase();
    Cursor cursor = db.rawQuery(selectQuery, null);
    int count = cursor.getCount();
    cursor.close();
    return count;
  }

  public int updateStatus(String date, int status) {
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put(STATUS, status);
    return db.update(TABLE_HYDROCARE, values, DATE_OF_ENTRY + " = ?",
        new String[]{date});
  }

  public int updateInTake(String date, double inTake) {
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put(INTAKE, inTake);
    return db.update(TABLE_HYDROCARE, values, DATE_OF_ENTRY + " = ?",
        new String[]{date});
  }

  public Double getInTake(String date) {
    String selectQuery = "SELECT  * FROM " + TABLE_HYDROCARE + "  where " + DATE_OF_ENTRY
        + " between datetime(julianday(date('now','localtime'))) and datetime('now','localtime') AND status=1";
    SQLiteDatabase db = this.getWritableDatabase();
    Cursor cursor = db.query(TABLE_HYDROCARE, new String[]{INTAKE}, DATE_OF_ENTRY + " = ?",
        new String[]{date}, null, null, null, null);
    double inTake = 0.0d;
    if (cursor != null) {
      if (cursor.moveToFirst())
        inTake = cursor.getDouble(0);
    }
    return inTake;
  }

  public void deletePreviousWeekData(String date) {
    SQLiteDatabase db = this.getWritableDatabase();
    db.delete(TABLE_HYDROCARE, "where STATUS = ?", new String[]{String.valueOf(1)});
  }

  public int getTodaySms() {
    String selectQuery = "SELECT  * FROM " + TABLE_HYDROCARE + "  where " + DATE_OF_ENTRY
        + " between datetime(julianday(date('now','localtime'))) and datetime('now','localtime') AND status=1";
    SQLiteDatabase db = this.getWritableDatabase();
    Cursor cursor = db.rawQuery(selectQuery, null);
    return cursor.getCount();
  }

  public HydroCare getToDayEntry(String date) {
    SQLiteDatabase db = this.getWritableDatabase();
    Cursor cursor = db.query(TABLE_HYDROCARE, new String[]{DATE_OF_ENTRY,TARGET,INTAKE,TEMPERATURE,STATUS}, DATE_OF_ENTRY + " = ?",
        new String[]{date}, null, null, null, null);
    double inTake = 0.0d;
    if (cursor != null) {
      if (cursor.moveToFirst()) {
        HydroCare hydroCare = new HydroCare();
        hydroCare.setDateOfEntry(getDate(cursor.getString(0)));
        hydroCare.setTarget(cursor.getDouble(1));
        hydroCare.setInTake(cursor.getDouble(2));
        hydroCare.setTemperature(cursor.getDouble(3));
        hydroCare.setStatus(cursor.getInt(4));
        return hydroCare;
      }
    }
    return null;
  }

  public int getWeekSms(int weekday) {
    String selectQuery = "SELECT  * FROM " + TABLE_HYDROCARE + " where " + DATE_OF_ENTRY + " between datetime( julianday(date(date('now','localtime','-" + weekday + " days'))))and datetime('now','localtime') AND status=1";
    SQLiteDatabase db = this.getWritableDatabase();
    Cursor cursor = db.rawQuery(selectQuery, null);
    return cursor.getCount();
  }

  public int getWeekDay() {
    String selectQuery = "SELECT strftime('%w','now','localtime')";
    SQLiteDatabase db = this.getWritableDatabase();
    Cursor cursor = db.rawQuery(selectQuery, null);
    cursor.moveToFirst();
    return cursor.getInt(0);
  }

}
