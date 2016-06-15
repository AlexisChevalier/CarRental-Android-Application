/**
 * CarRental
 *
 * This file provides a specialized exception
 * This exceptions is thrown the server returns a parameter error
 */

package com.vehiclerental.exceptions;

public class ApiInvalidParameterException extends Exception {
    /**
     * Constructor
     *
     * @param error failure details
     */
    public ApiInvalidParameterException(String error) {
        super(error);
    }
}
