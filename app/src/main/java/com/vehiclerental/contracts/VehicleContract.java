/**
 * CarRental
 *
 * This file provides a light communication object representing the state of a vehicle
 */

package com.vehiclerental.contracts;

public class VehicleContract {
    public int id;
    public BranchContract branch;
    public int type;
    public int status;
    public String registrationNumber;
    public int doors;
    public int seats;
    public boolean automaticTransmission;
    public double poundsPerDay;
    public String name;
}
