/**
 * CarRental
 *
 * This file contains a list of available methods for the distributed system, represented by an integer.
 * The distributed system share those codes in order to accept communication requests.
 */

package com.vehiclerental.apiClient;

public class OperationCodes {
    //Guest methods
    public final static int GET_BRANCHES                    = 0;
    public final static int SEARCH_AVAIL_VEHICLES           = 1;
    public final static int CREATE_ACCOUNT                  = 2;

    //User methods
    public final static int BOOK_VEHICLE                    = 3;
    public final static int GET_USER_BOOKINGS               = 4;
    public final static int GET_ACCOUNT_DETAILS             = 5;

    //Staff methods
    public final static int CREATE_USER                     = 6;
    public final static int UPDATE_OR_CREATE_VEHICLE        = 7;
    public final static int SEARCH_USER                     = 8;
    public final static int GET_BRANCH_BOOKINGS             = 9;
    public final static int CHANGE_BOOKING_STATUS           = 10;
    public final static int SEARCH_ALL_VEHICLES             = 11;
    public final static int SHUTDOWN_SYSTEM                 = 12;
    public final static int GET_VEHICLE_MOVES               = 13;
}
