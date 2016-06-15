/**
 * CarRental
 *
 * This file provides the simple communication response object used between the android application and the head office socket server
 * It contains an operation code, a status code, an optional error message and an optional serialized object
 */

package com.vehiclerental.apiClient.socketImplementation;

import com.google.gson.annotations.SerializedName;

public class SocketResponseMessage {
    @SerializedName("op_code")
    public int operationCode;
    @SerializedName("status")
    public int status;
    @SerializedName("error")
    public String error;
    @SerializedName("serialized_object")
    public String serializedObject;
}
