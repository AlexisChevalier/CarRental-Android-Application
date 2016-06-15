/**
 * CarRental
 *
 * This file provides a light communication object representing the parameters for the search available vehicles action
 */

package com.vehiclerental.contracts;

public class SearchAvailableVehiclesRequestContract {
    public int vehicleType;
    public String pickupDate;
    public String returnDate;
}
