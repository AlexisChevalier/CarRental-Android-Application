/**
 * CarRental
 *
 * This file provides a light communication object representing the state of a booking
 */

package com.vehiclerental.contracts;

public class BookingContract {
    public int id;
    public String branch;
    public VehicleContract vehicle;
    public VehicleMoveContract vehicleMove;
    public String pickupDate;
    public String returnDate;
    public long daysCount;
    public double price;
    public boolean bookingValidated;
    public boolean requireVehicleMove;
}
