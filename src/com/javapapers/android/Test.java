package com.javapapers.android;

import java.util.Date;

/**
 * Created by ehc on 15/5/15.
 */
public class Test {
  public static void main(String[] a) {
    Date date = new Date();
    System.out.println("current date:" + date);
    System.out.println("current date:" + date.getTime());
    int day = date.getDate();
    date.setDate(31);
    date.setHours(12);
    date.setMinutes(0);
    date.setSeconds(0);
    System.out.println("   next date:" + date);
    System.out.println("   next date:" + date.getTime());

    day = date.getDate();
    date.setDate(day + 1);
    date.setHours(12);
    date.setMinutes(0);
    date.setSeconds(0);
    System.out.println("   next date:" + date);
    System.out.println("   next date:" + date.getTime());
  }
}
