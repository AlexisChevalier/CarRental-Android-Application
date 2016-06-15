/**
 * CarRental
 *
 * This file provides the simple communication request object used between the android application and the head office socket server
 * It contains an operation code, a basic auth string (base64(email:password)), a current branch ID and an optional serialized object
 */

package com.vehiclerental.apiClient.socketImplementation;

import com.google.gson.annotations.SerializedName;

public class SocketRequestMessage {
    @SerializedName("op_code")
    public int OperationCode;
    @SerializedName("auth")
    public String BasicAuth;
    @SerializedName("branch")
    public int BranchId;
    @SerializedName("serialized_object")
    public String SerializedObject;
}
