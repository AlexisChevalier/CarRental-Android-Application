/**
 * CarRental
 *
 * This file provides a light communication object representing the parameters for the create or update vehicle action
 *
 * It could have been separated in two pieces but the operations is really small and it would complexify the system
 * rather than simplify it
 */

package com.vehiclerental.contracts;

public class CreateUpdateVehicleContract {
    public int type;
    public String registrationNumber;
    public int doors;
    public int seats;
    public boolean automaticTransmission;
    public double poundsPerDay;
    public String name;

    //Update-specific properties
    public boolean isUpdateOperation;
    //public int newBranchId;
    public int id;
    public int newStatusId;
}
