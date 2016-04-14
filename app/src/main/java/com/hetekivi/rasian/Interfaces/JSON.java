package com.hetekivi.rasian.Interfaces;

import org.json.JSONObject;

/**
 * Created by Santeri Hetekivi on 12.4.2016.
 */

/**
 * Interface JSON
 * for making and reading from JSON file.
 */
public interface JSON {
    /**
     * Function toJSON
     * for making object to JSONObject
     * @return JSONObject that contains objects data.
     */
    JSONObject toJSON();

    /**
     * Function onToJSONSuccess
     * This gets called when ToJSONTask has been done and was successful.
     */
    void onToJSONSuccess();

    /**
     * Function onToJSONFailure
     * This gets called when ToJSONTask has been done and there were failure.
     */
    void onToJSONFailure();


    /**
     * Function fromJSON
     * for reading data from JSONObject to object.
     * @param jsonObject JSONObject to read from.
     * @return Success of read.
     */
    boolean fromJSON(JSONObject jsonObject);

    /**
     * Function onFromJSONSuccess
     * This gets called when FromJSONTask has been done and was successful.
     */
    void onFromJSONSuccess();

    /**
     * Function onFromJSONFailure
     * This gets called when FromJSONTask has been done and there were failure.
     */
    void onFromJSONFailure();
}
