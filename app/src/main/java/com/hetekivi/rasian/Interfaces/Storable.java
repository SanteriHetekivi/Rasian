package com.hetekivi.rasian.Interfaces;

/**
 * Created by Santeri Hetekivi on 2.4.2016.
 */

/**
 * Interface Storable
 * for objects that can store their data.
 */
public interface Storable
{
    /**
     * Function Load
     * for loading data.
     * @return Success of load.
     */
    boolean Load();

    /**
     * Function onLoadSuccess
     * This gets called when loading has been done and was successful.
     */
    void onLoadSuccess();

    /**
     * Function onLoadFailure
     * This gets called when loading has been done and there were errors.
     */
    void onLoadFailure();

    /**
     * Function Save
     * for saving data.
     * @return Success of save.
     */
    boolean Save();

    /**
     * Function onSaveSuccess
     * This gets called when saving has been done and was successful.
     */
    void onSaveSuccess();

    /**
     * Function onSaveFailure
     * This gets called when saving has been done and there were errors.
     */
    void onSaveFailure();
}
