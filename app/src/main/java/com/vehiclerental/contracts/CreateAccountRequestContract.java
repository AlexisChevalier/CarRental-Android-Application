/**
 * CarRental
 *
 * This file provides a light communication object representing the parameters for the create account action
 */

package com.vehiclerental.contracts;

public class CreateAccountRequestContract {
    public String fullName;
    public String emailAddress;
    public String phoneNumber;
    public String password;
    public String address_street;
    public String address_city;
    public String address_postalCode;
    public String address_country;
}
