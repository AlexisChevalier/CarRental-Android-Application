/**
 * CarRental
 *
 * This file provides a light communication object representing the parameters for the search vehicle action
 */

package com.vehiclerental.contracts;

public class SearchVehicleContract {
    //If this isn't provided, the type id will be used
    public String registrationNumber;
    public int vehicleTypeId;
}
