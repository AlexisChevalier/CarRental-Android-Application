/**
 * CarRental
 *
 * This file provides a specialized exception
 * This exceptions is thrown when the server returns an error
 */

package com.vehiclerental.exceptions;

public class ApiException extends Exception {
    private int _code;

    /**
     * Constructor
     *
     * @param code API error code
     * @param error API error message
     */
    public ApiException(int code, String error) {
        super(error);
        _code = code;
    }

    /**
     * Constructor
     *
     * @param error failure details
     */
    public ApiException(String error) {
        super(error);
    }

    /**
     * Returns the API error code number
     *
     * @return API error code
     */
    public int getCode() {
        return _code;
    }
}
