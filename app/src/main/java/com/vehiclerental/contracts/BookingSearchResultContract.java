/**
 * CarRental
 *
 * This file provides a light communication object representing a booking search result
 */

package com.vehiclerental.contracts;

public class BookingSearchResultContract {
    public boolean requireVehicleMove;
    public VehicleContract vehicle;
    public String pickupDate;
    public String returnDate;
    public long daysCount;
    public double price;
}
