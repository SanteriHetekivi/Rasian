package com.hetekivi.rasian.Interfaces;

/**
 * Created by Santeri Hetekivi on 5.4.2016.
 */

/**
 * Interface Addable
 * for objects that can add.
 */
public interface Addable {

    /**
     * Function Add
     * for adding data.
     * @param objectToAdd Object that will be added.
     * @return Success of add.
     */
    boolean Add(Object objectToAdd);

    /**
     * Function onAddSuccess
     * This gets called when adding has been done and was successful.
     */
    void onAddSuccess();

    /**
     * Function onAddFailure
     * This gets called when adding has been done and there were errors.
     */
    void onAddFailure();
}
