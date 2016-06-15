/**
 * CarRental
 *
 * This file provides a specialized exception
 * This exceptions is thrown when the server refuses a request because of an authorization problem
 */

package com.vehiclerental.exceptions;

public class ApiUnauthorizedException extends Exception {
    /**
     * Constructor
     *
     * @param error failure details
     */
    public ApiUnauthorizedException(String error) {
        super(error);
    }
}
