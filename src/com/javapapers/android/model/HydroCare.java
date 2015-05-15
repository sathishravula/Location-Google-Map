package com.javapapers.android.model;

import java.util.Date;

/**
 * Created by ehc on 14/5/15.
 */
public class HydroCare {
  private Date dateOfEntry;
  private double temperature;
  private double inTake;
  private double target;
  private int status;

  public Date getDateOfEntry() {
    return dateOfEntry;
  }

  public void setDateOfEntry(Date dateOfEntry) {
    this.dateOfEntry = dateOfEntry;
  }

  public double getTemperature() {
    return temperature;
  }

  public void setTemperature(double temperature) {
    this.temperature = temperature;
  }

  public double getInTake() {
    return inTake;
  }

  public void setInTake(double inTake) {
    this.inTake = inTake;
  }

  public double getTarget() {
    return target;
  }

  public void setTarget(double target) {
    this.target = target;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  @Override
  public String toString() {
    return "HydroCare{" +
        "dateOfEntry=" + dateOfEntry +
        ", temperature=" + temperature +
        ", inTake=" + inTake +
        ", target=" + target +
        ", status=" + status +
        '}';
  }
}
