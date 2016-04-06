package com.hetekivi.rasian.Interfaces;

/**
 * Created by Santeri Hetekivi on 2.4.2016.
 */

/**
 * Interface Updatable
 * for objects that can update their data.
 */
public interface Updatable
{
    /**
     * Function Update
     * for updating objects data.
     * @param updateAll Does update go thought all.
     * @param setAll Sets all and overwrites limits.
     * @return Success of update.
     */
    boolean Update(boolean updateAll, boolean setAll);

    /**
     * Function onUpdateSuccessful
     * This gets called when update has been done and was successful.
     */
    void onUpdateSuccessful();

    /**
     * Function onUpdateFailed
     * This gets called when update has been done and there were errors.
     */
    void onUpdateFailed();
}
