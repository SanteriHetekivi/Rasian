package com.hetekivi.rasian.Interfaces;

import org.xmlpull.v1.XmlPullParser;

/**
 * Created by Santeri Hetekivi on 2.4.2016.
 */

/**
 * Interface Listener
 * for attaching listener to task.
 */
public interface Listener
{
    /**
     * Function onSuccess
     * This gets called when task has been done and was successful.
     */
    //void onSuccess();

    /**
     * Function onSuccess
     * This gets called when task has been done and was successful.
     * @param additional Additional object to pass.
     */
    void onSuccess(Object additional);

    /**
     * Function onFailure
     * This gets called when task has been done and there were errors.
     */
    //void onFailure();

    /**
     * Function onFailure
     * This gets called when task has been done and there were errors.
     * @param additional Additional object to pass.
     */
    void onFailure(Object additional);

}
