/**
 * CarRental
 *
 * This file provides multiple serialization abstraction methods
 *
 * I am using the gson library to serialize everything to JSON
 */

package com.vehiclerental.utils;

import com.google.gson.Gson;

import java.lang.reflect.Type;

public class SerializationUtils {

    //Static gson object
    private static Gson gsonObject = new Gson();

    /**
     * Serialize the provided object to a json string
     *
     * @param object the object to serialize
     * @param <T> the generic object type
     * @return the serialized json string
     */
    public static <T> String serialize(T object) {
        return gsonObject.toJson(object);
    }

    /**
     * Deserialize the provided string into the given type
     *
     * @param serialized the serialized json string
     * @param type the expected object type
     * @param <T> the generic object type
     * @return the deserialized object
     */
    public static <T> T deserialize(String serialized, Type type) {
        return gsonObject.fromJson(serialized, type);
    }
}
