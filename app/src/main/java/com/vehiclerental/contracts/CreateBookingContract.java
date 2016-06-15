/**
 * CarRental
 *
 * This file provides a light communication object representing the parameters for the create booking action
 */

package com.vehiclerental.contracts;

public class CreateBookingContract {
    public int vehicleId;
    public int vehicleBranchId;
    public int bookingBranchId;
    public String pickupDate;
    public String returnDate;
    public String creditCardNumber;
    public String creditCardExpirationMonth;
    public String creditCardExpirationYear;
    public String creditCardCvcCode;

    //If this property is used, the action will require staff credentials
    public Integer bookingOwnerUserId;
}
