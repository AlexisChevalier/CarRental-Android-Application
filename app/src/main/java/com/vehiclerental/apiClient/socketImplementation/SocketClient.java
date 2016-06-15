/**
 * CarRental
 *
 * This file provides an abstraction of the native secure socket client
 */

package com.vehiclerental.apiClient.socketImplementation;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.vehiclerental.CarRentalApplication;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import javax.net.ssl.SSLSocket;

public class SocketClient {

    /**
     * Process given request and return results
     *
     * @param url the server url
     * @param port the server port
     * @param request the given request
     * @return the deserialized response if the request succeeded, null otherwise
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static SocketResponseMessage doRequest(String url, int port, SocketRequestMessage request) throws IOException, ClassNotFoundException {
        SSLSocket socket;
        try {
            socket = (SSLSocket) CarRentalApplication.getSslContext().getSocketFactory().createSocket(url, port);

            //Using direct JsonReader and JsonWriter streams directly on the socket
            Gson gson = new Gson();
            JsonReader reader = new JsonReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            JsonWriter writer = new JsonWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));

            //serializing and sending request
            gson.toJson(request, SocketRequestMessage.class, writer);
            writer.flush();

            //receiving and deserializing response
            SocketResponseMessage responseMessage = gson.fromJson(reader, SocketResponseMessage.class);

            writer.close();

            return responseMessage;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
